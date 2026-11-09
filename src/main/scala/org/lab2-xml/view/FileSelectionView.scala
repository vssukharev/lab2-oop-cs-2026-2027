package org.lab2.view

import scalafx.Includes.*
import scalafx.scene.control.{MenuButton, MenuItem}
import scalafx.stage.{FileChooser, Window}
import org.lab2.loader.{FileProps, StorageKind}

enum FileOperation:
  case Load, Save

class FileSelectionView(
  labelText: String,
  operation: FileOperation,
  allowedExtensions: Seq[FileChooser.ExtensionFilter] = Seq.empty,
  onFileSelected: FileProps => Unit
) extends MenuButton(labelText):

  private val localTitle = if operation == FileOperation.Load then "Choose Local File" else "Save Local File"
  private val remoteTitle = if operation == FileOperation.Load then "Select File from Google Drive" else "Save File to Google Drive"

  private val localItem = new MenuItem(if operation == FileOperation.Load then "From Local File..." else "To Local File..."):
    onAction = _ =>
      val chooser = new FileChooser:
        title = localTitle
        extensionFilters ++= allowedExtensions.map(_.delegate)
      val window: Window = scene.value.window.value
      val file = if operation == FileOperation.Load then chooser.showOpenDialog(window) else chooser.showSaveDialog(window)
      if file != null then
        onFileSelected(FileProps(path = file.getAbsolutePath, storageKind = StorageKind.Local))

  private val remoteItem = new MenuItem(if operation == FileOperation.Load then "From Google Drive..." else "To Google Drive..."):
    onAction = _ =>
      GoogleFileChooserDialog.show(remoteTitle, forSave = (operation == FileOperation.Save)).foreach { props =>
        onFileSelected(props)
      }

  items = Seq(localItem, remoteItem)
