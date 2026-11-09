package org.lab2.view

import scalafx.Includes.*
import scalafx.scene.control.*
import scalafx.scene.layout.{HBox, VBox, Priority}
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.FileChooser
import org.lab2.loader.FileProps
import org.lab2.controller.ControllerState

class TopBarView(
  onXmlSelected: FileProps => Unit,
  onXsltSelected: FileProps => Unit,
  onParserChanged: Boolean => Unit
) extends VBox:

  spacing = 4
  padding = Insets(10)
  style = "-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;"

  private val xmlFilters = Seq(
    new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"),
    new FileChooser.ExtensionFilter("All Files", "*.*")
  )

  private val xsltFilters = Seq(
    new FileChooser.ExtensionFilter("XSLT Stylesheets (*.xsl, *.xslt)", Seq("*.xsl", "*.xslt")),
    new FileChooser.ExtensionFilter("All Files", "*.*")
  )

  val xmlButton = new FileSelectionView("Select XML ▾", FileOperation.Load, xmlFilters, onXmlSelected)
  val xsltButton = new FileSelectionView("Select XSLT ▾", FileOperation.Load, xsltFilters, onXsltSelected)

  val xmlLabel = new Label("(No XML file selected)"):
    style = "-fx-font-size: 11px; -fx-text-fill: #6c757d;"

  val xsltLabel = new Label("(No XSLT file selected)"):
    style = "-fx-font-size: 11px; -fx-text-fill: #6c757d;"

  val parserToggleGroup = new ToggleGroup()
  val domRadio = new RadioButton("DOM"):
    toggleGroup = parserToggleGroup
    selected = true

  val saxRadio = new RadioButton("SAX"):
    toggleGroup = parserToggleGroup

  parserToggleGroup.selectedToggle.onChange { (_, _, newToggle) =>
    val isDom = (newToggle == domRadio)
    onParserChanged(isDom)
  }

  ControllerState.sourceFile.onChange { (_, _, newFile) =>
    xmlLabel.text = newFile.map(_.displayName).getOrElse("(No XML file selected)")
  }

  ControllerState.transformFile.onChange { (_, _, newFile) =>
    xsltLabel.text = newFile.map(_.displayName).getOrElse("(No XSLT file selected)")
  }

  private val xmlColumn = new VBox(2, xmlButton, xmlLabel)
  private val xsltColumn = new VBox(2, xsltButton, xsltLabel)
  private val parserBox = new HBox(8, new Label("Parser:"), domRadio, saxRadio):
    alignment = Pos.CenterLeft
    padding = Insets(0, 0, 10, 20)

  private val topRow = new HBox(15, xmlColumn, xsltColumn, parserBox):
    alignment = Pos.TopLeft

  children = Seq(topRow)
