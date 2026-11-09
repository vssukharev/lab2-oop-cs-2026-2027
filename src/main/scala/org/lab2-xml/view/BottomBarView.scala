package org.lab2.view

import scalafx.Includes.*
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, Priority, Region}
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.FileChooser
import org.lab2.loader.FileProps
import org.lab2.misc.Logger

class BottomBarView(
  onExportXml: FileProps => Unit,
  onExportHtml: FileProps => Unit
) extends HBox:

  spacing = 10
  padding = Insets(8, 12, 8, 12)
  alignment = Pos.CenterLeft
  style = "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;"

  private val xmlFilters = Seq(
    new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"),
    new FileChooser.ExtensionFilter("All Files", "*.*")
  )

  private val htmlFilters = Seq(
    new FileChooser.ExtensionFilter("HTML Files (*.html, *.htm)", Seq("*.html", "*.htm")),
    new FileChooser.ExtensionFilter("All Files", "*.*")
  )

  val exportXmlButton = new FileSelectionView("Export XML ▾", FileOperation.Save, xmlFilters, onExportXml)
  val exportHtmlButton = new FileSelectionView("Export HTML ▾", FileOperation.Save, htmlFilters, onExportHtml)

  val spacer = new Region:
    hgrow = Priority.Always

  val logLabel = new Label(s"Log file: ${Logger.logFileCanonicalPath}"):
    style = "-fx-text-fill: #6c757d; -fx-font-size: 11px;"

  children = Seq(exportXmlButton, exportHtmlButton, spacer, logLabel)
