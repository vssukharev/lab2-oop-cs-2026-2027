package org.lab2.view

import scalafx.Includes.*
import scalafx.scene.control.*
import scalafx.scene.layout.{Priority, VBox}
import scalafx.scene.web.WebView

class ContentView extends TabPane:
  vgrow = Priority.Always

  val xmlTextArea = new TextArea:
    editable = false
    style = "-fx-font-family: monospace; -fx-font-size: 12px;"

  val rawHtmlTextArea = new TextArea:
    editable = false
    style = "-fx-font-family: monospace; -fx-font-size: 12px;"
    text = "Select an XSLT file in order to see HTML"

  val webView = new WebView()
  private val placeholderHtml =
    "<html><body style='font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100%; color: #6c757d;'>" +
    "<h3>Select an XSLT file in order to see HTML</h3></body></html>"
  webView.engine.loadContent(placeholderHtml)

  val xmlTab = new Tab:
    text = "XML"
    closable = false
    content = xmlTextArea

  val rawHtmlTab = new Tab:
    text = "HTML (Source)"
    closable = false
    content = rawHtmlTextArea

  val renderedHtmlTab = new Tab:
    text = "HTML (Rendered)"
    closable = false
    content = webView

  tabs = Seq(xmlTab, rawHtmlTab, renderedHtmlTab)

  def setXml(content: String): Unit =
    xmlTextArea.text = content

  def setHtml(content: String): Unit =
    rawHtmlTextArea.text = content
    webView.engine.loadContent(content)

  def resetHtmlPrompt(): Unit =
    rawHtmlTextArea.text = "Select an XSLT file in order to see HTML"
    webView.engine.loadContent(placeholderHtml)
