package org.lab2.parser

import org.lab2.tree.TreeNode
import scala.util.Try
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.{Document, Element, Node}
import scala.compiletime.uninitialized

/**
 * DOM-based XML Parser strategy.
 */
class DomParser extends Parser:
  private var _doc: Document = uninitialized

  def doc: Document = _doc

  override def apply(content: String): Try[TreeNode] = Try {
    val factory = DocumentBuilderFactory.newInstance()
    factory.setNamespaceAware(true)
    // Secure parser features
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)

    val builder = factory.newDocumentBuilder()
    val is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
    _doc = builder.parse(is)
    _doc.getDocumentElement.normalize()

    convertElement(_doc.getDocumentElement)
  }

  private def convertElement(element: Element): TreeNode =
    val tagName = element.getTagName
    val childNodes = element.getChildNodes
    val childElements = collection.mutable.ListBuffer[TreeNode]()
    val textBuffer = new StringBuilder()

    for i <- 0 until childNodes.getLength do
      val node = childNodes.item(i)
      node.getNodeType match
        case Node.ELEMENT_NODE =>
          childElements += convertElement(node.asInstanceOf[Element])
        case Node.TEXT_NODE | Node.CDATA_SECTION_NODE =>
          textBuffer.append(node.getNodeValue)
        case _ => // ignore comments, processing instructions, etc.

    if childElements.nonEmpty then
      TreeNode(name = tagName, children = childElements.toList, text = None)
    else
      val trimmed = textBuffer.toString.trim
      val optText = if trimmed.nonEmpty then Some(trimmed) else None
      TreeNode(name = tagName, children = Nil, text = optText)
