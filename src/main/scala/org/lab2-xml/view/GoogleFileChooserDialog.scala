package org.lab2.view

import scalafx.Includes.*
import scalafx.scene.control.*
import scalafx.scene.layout.{GridPane, HBox, VBox, Priority}
import scalafx.geometry.{Insets, Pos}
import scala.util.{Success, Failure}
import org.lab2.loader.{FileProps, GoogleDriveLoader, StorageKind}

object GoogleFileChooserDialog:

  def show(titleText: String, defaultName: String = "", operation: FileOperation = FileOperation.Load): Option[FileProps] =
    val listingTry = GoogleDriveLoader.getListing()
    val files = listingTry match
      case Success(list) => list
      case Failure(ex) =>
        DialogUtils.showError("Google Drive Error", s"Could not fetch file list: ${ex.getMessage}", Some(ex))
        return None

    val inputField = new TextField:
      text = defaultName
      hgrow = Priority.Always

    val fileGrid = new GridPane:
      hgap = 8
      vgap = 6
      padding = Insets(6)

    if files.isEmpty then
      fileGrid.add(new Label("(No files found)") {
        style = "-fx-text-fill: #6c757d; -fx-font-style: italic;"
      }, 0, 0)
    else
      val numCols = 3
      files.zipWithIndex.foreach { (f, idx) =>
        val name = f.getName
        val btn = new Button(name):
          maxWidth = Double.MaxValue
          style = "-fx-alignment: CENTER-LEFT; -fx-font-size: 11px;"
          onAction = _ => inputField.text = name
        fileGrid.add(btn, idx % numCols, idx / numCols)
      }

    val gridScroll = new ScrollPane:
      content = fileGrid
      fitToWidth = true
      prefHeight = 120
      maxHeight = 200

    val inputSection = new HBox:
      spacing = 10
      alignment = Pos.CenterLeft
      children = Seq(new Label("File:"), inputField)

    val contentBox = new VBox:
      spacing = 10
      padding = Insets(10)
      children = Seq(gridScroll, inputSection)

    val dialog = new Dialog[String]:
      title = "Google Drive"
      headerText = titleText
      dialogPane = new DialogPane:
        content = contentBox
        buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
      resultConverter = dialogButton =>
        if dialogButton == ButtonType.OK then inputField.text.value else null

    dialog.showAndWait() match
      case Some(res) if res != null && res.toString.trim.nonEmpty =>
        val trimmed = res.toString.trim
        val fileId = files.find(_.getName == trimmed).map(_.getId).getOrElse("")
        Some(FileProps(path = trimmed, storageKind = StorageKind.Remote, id = fileId))
      case _ =>
        None
