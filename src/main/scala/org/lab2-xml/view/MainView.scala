package org.lab2.view

import scalafx.Includes.*
import scalafx.scene.layout.BorderPane
import scala.util.{Success, Failure}
import org.lab2.loader.{FileProps, Loader}
import org.lab2.parser.{DomParser, SaxParser}
import org.lab2.transform.{XmlTransformer, HtmlTransformer}
import org.lab2.controller.ControllerState
import org.lab2.misc.Logger

class MainView extends BorderPane:

  private val xmlTransformer = new XmlTransformer()
  private var currentXslContent: Option[String] = None

  lazy val topBarView = new TopBarView(
    onXmlSelected = handleXmlSelected,
    onXsltSelected = handleXsltSelected,
    onParserChanged = handleParserChanged
  )

  lazy val filterPanelView = new FilterPanelView(
    onApplyFilters = () => applyFilters()
  )

  lazy val contentView = new ContentView()

  lazy val bottomBarView = new BottomBarView(
    onExportXml = handleExportXml,
    onExportHtml = handleExportHtml
  )

  top = topBarView
  left = filterPanelView
  center = contentView
  bottom = bottomBarView

  def handleXmlSelected(fileProps: FileProps): Unit =
    val loader = Loader(fileProps.storageKind)
    loader.load(fileProps) match
      case Success(rawContent) =>
        Logger.info(s"Loaded XML from ${fileProps.displayName} (${rawContent.length} bytes)")
        ControllerState.sourceFile.value = Some(fileProps)
        parseAndApply(rawContent)
      case Failure(ex) =>
        DialogUtils.showError("Failed to Load XML", s"Error loading file: ${ex.getMessage}", Some(ex))

  private def parseAndApply(rawContent: String): Unit =
    val parser = ControllerState.parser.value
    parser(rawContent) match
      case Success(tree) =>
        Logger.info(s"Parsed XML using ${parser.getClass.getSimpleName}. Root node: <${tree.name}> with ${tree.children.size} child records.")
        ControllerState.sourceTree.value = Some(tree)
        filterPanelView.updatePossibleValues(tree)
        applyFilters()
      case Failure(ex) =>
        DialogUtils.showError("XML Parsing Failed", s"Parser encountered an error: ${ex.getMessage}", Some(ex))

  def handleXsltSelected(fileProps: FileProps): Unit =
    val loader = Loader(fileProps.storageKind)
    loader.load(fileProps) match
      case Success(xslText) =>
        Logger.info(s"Loaded XSLT from ${fileProps.displayName}")
        ControllerState.transformFile.value = Some(fileProps)
        currentXslContent = Some(xslText)
        renderHtml()
      case Failure(ex) =>
        DialogUtils.showError("Failed to Load XSLT", s"Error loading XSLT file: ${ex.getMessage}", Some(ex))

  def handleParserChanged(isDom: Boolean): Unit =
    val newParser = if isDom then new DomParser() else new SaxParser()
    ControllerState.parser.value = newParser
    Logger.info(s"Active parsing strategy switched to: ${newParser.getClass.getSimpleName}")
    ControllerState.sourceFile.value.foreach { fileProps =>
      Loader(fileProps.storageKind).load(fileProps).foreach { rawContent =>
        parseAndApply(rawContent)
      }
    }

  def applyFilters(): Unit =
    ControllerState.sourceTree.value match
      case Some(root) =>
        val filter = ControllerState.filter.value
        val filtered = filter.apply(root)
        ControllerState.displayedTree.value = Some(filtered)
        Logger.info(s"Applied filters: ${filtered.children.size} of ${root.children.size} records matched.")

        xmlTransformer(filtered) match
          case Success(xmlStr) =>
            contentView.setXml(xmlStr)
          case Failure(ex) =>
            DialogUtils.showError("XML Serialization Error", ex.getMessage, Some(ex))

        renderHtml()
      case None =>
        // No XML loaded yet
        ()

  private def renderHtml(): Unit =
    val targetTree = ControllerState.displayedTree.value.orElse(ControllerState.sourceTree.value)
    (targetTree, currentXslContent) match
      case (Some(tree), Some(xsl)) =>
        val transformer = new HtmlTransformer(xmlTransformer, xsl)
        transformer(tree) match
          case Success(html) =>
            contentView.setHtml(html)
          case Failure(ex) =>
            DialogUtils.showError("HTML Transformation Error", s"XSLT processing failed: ${ex.getMessage}", Some(ex))
      case _ =>
        contentView.resetHtmlPrompt()

  def handleExportXml(fileProps: FileProps): Unit =
    val treeOpt = ControllerState.displayedTree.value.orElse(ControllerState.sourceTree.value)
    treeOpt match
      case Some(tree) =>
        xmlTransformer(tree) match
          case Success(xmlStr) =>
            Loader(fileProps.storageKind).save(xmlStr, fileProps) match
              case Success(_) =>
                DialogUtils.showInfo("Export Successful", s"XML successfully exported to ${fileProps.displayName}")
              case Failure(ex) =>
                DialogUtils.showError("Export Failed", s"Failed to save XML: ${ex.getMessage}", Some(ex))
          case Failure(ex) =>
            DialogUtils.showError("Export Failed", s"Failed to serialize XML: ${ex.getMessage}", Some(ex))
      case None =>
        DialogUtils.showWarning("No Data to Export", "Please load an XML file first.")

  def handleExportHtml(fileProps: FileProps): Unit =
    currentXslContent match
      case None =>
        DialogUtils.showWarning("No XSLT Selected", "Please select an XSLT file first in order to generate HTML for export.")
      case Some(xsl) =>
        val treeOpt = ControllerState.displayedTree.value.orElse(ControllerState.sourceTree.value)
        treeOpt match
          case Some(tree) =>
            val transformer = new HtmlTransformer(xmlTransformer, xsl)
            transformer(tree) match
              case Success(htmlStr) =>
                Loader(fileProps.storageKind).save(htmlStr, fileProps) match
                  case Success(_) =>
                    DialogUtils.showInfo("Export Successful", s"HTML successfully exported to ${fileProps.displayName}")
                  case Failure(ex) =>
                    DialogUtils.showError("Export Failed", s"Failed to save HTML: ${ex.getMessage}", Some(ex))
              case Failure(ex) =>
                DialogUtils.showError("Export Failed", s"HTML transformation failed: ${ex.getMessage}", Some(ex))
          case None =>
            DialogUtils.showWarning("No Data to Export", "Please load an XML file first.")
