package org.lab2.view

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import org.lab2.misc.Logger

object DialogUtils:
  def showError(dialogTitle: String, message: String, ex: Option[Throwable] = None): Unit =
    Logger.error(s"$dialogTitle: $message", ex)
    new Alert(AlertType.Error):
      title = dialogTitle
      headerText = dialogTitle
      contentText = message
    .showAndWait()

  def showWarning(dialogTitle: String, message: String): Unit =
    Logger.warn(s"$dialogTitle: $message")
    new Alert(AlertType.Warning):
      title = dialogTitle
      headerText = dialogTitle
      contentText = message
    .showAndWait()

  def showInfo(dialogTitle: String, message: String): Unit =
    Logger.info(s"$dialogTitle: $message")
    new Alert(AlertType.Information):
      title = dialogTitle
      headerText = dialogTitle
      contentText = message
    .showAndWait()
