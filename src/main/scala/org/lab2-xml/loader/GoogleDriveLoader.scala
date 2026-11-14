package org.lab2.loader

import scala.util.Try
import scala.io.Source
import java.io.{ByteArrayInputStream, File, FileInputStream, FileNotFoundException, InputStreamReader}
import java.nio.charset.StandardCharsets
import java.util.Collections
import scala.jdk.CollectionConverters.*
import scala.sys.process.*

import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.{Drive, DriveScopes}
import com.google.api.services.drive.model.{File as GFile}

import org.lab2.misc.AppConfig

/**
 * Headless Google Drive Loader interacting directly with Google Drive API v3.
 * Completely decoupled from ScalaFX UI components.
 */
class GoogleDriveLoader extends Loader:

  override def load(file: FileProps): Try[String] = Try {
    val service = GoogleDriveLoader.getService()
    val fileId =
      if file.id.nonEmpty then file.id
      else
        GoogleDriveLoader.findFileByName(file.path) match
          case Some(f) => f.getId
          case None    => throw new FileNotFoundException(s"File not found on Google Drive: ${file.path}")

    val is = service.files().get(fileId).executeMediaAsInputStream()
    Source.fromInputStream(is, "UTF-8").mkString
  }

  override def save(content: String, file: FileProps): Try[Unit] = Try {
    val service = GoogleDriveLoader.getService()
    val mimeType = if file.path.endsWith(".html") || file.path.endsWith(".htm") then "text/html" else "application/xml"
    val metadata = GFile().setName(file.path).setMimeType(mimeType)
    val mediaContent = ByteArrayContent(mimeType, content.getBytes(StandardCharsets.UTF_8))

    val existing =
      if file.id.nonEmpty then Some(file.id)
      else GoogleDriveLoader.findFileByName(file.path).map(_.getId)

    existing match
      case Some(id) =>
        service.files().update(id, metadata, mediaContent).execute()
      case None =>
        service.files().create(metadata.setParents(GoogleDriveLoader.root), mediaContent).execute()
    ()
  }

object GoogleDriveLoader:
  private val config = AppConfig()
  val appData: String = "appDataFolder"
  val root: java.util.List[String] = Collections.singletonList(appData)
  val jsonFactory: GsonFactory = GsonFactory.getDefaultInstance()
  val scopes: java.util.List[String] = Collections.singletonList(DriveScopes.DRIVE_APPDATA)
  val transport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

  private var _service: Drive =
    new Drive.Builder(transport, jsonFactory, getCredential())
      .setApplicationName("lab2-oop")
      .build()

  def getCredential(): Credential =
    val secretsFile = new File(config.googleSecretsPath)
    if !secretsFile.exists() then
      throw new FileNotFoundException(s"Google client secrets not found at: ${config.googleSecretsPath}")

    val is = InputStreamReader(FileInputStream(secretsFile))
    val secrets = GoogleClientSecrets.load(jsonFactory, is)
    val flow = GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, secrets, scopes)
      .setDataStoreFactory(FileDataStoreFactory(File(config.googleDataStore)))
      .setAccessType("offline")
      .build()
    val receiver = LocalServerReceiver.Builder()
      .setPort(6969)
      .build()
    val browser = new AuthorizationCodeInstalledApp.Browser:
      override def browse(url: String): Unit =
        println(s"Open URL link in your browser: $url")

    AuthorizationCodeInstalledApp(flow, receiver, browser)
      .authorize("user")

  def getService(): Drive = _service

  def getListing(): Try[List[GFile]] = Try {
    val fileList = getService().files().list()
      .setSpaces(appData)
      .setFields("files(id, name)")
      .execute()
    val files = fileList.getFiles
    if files == null then Nil else files.asScala.toList
  }

  def findFileByName(name: String): Option[GFile] =
    getListing().toOption.flatMap(_.find(_.getName == name))
