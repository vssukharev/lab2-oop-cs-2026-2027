package org.lab2.parser

import org.lab2.tree.TreeNode
import scala.util.Try
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.xml.parsers.SAXParserFactory
import org.xml.sax.{Attributes, InputSource}
import org.xml.sax.helpers.DefaultHandler
import scala.collection.mutable.{ListBuffer, Stack}

/**
 * SAX-based XML Parser strategy.
 */
class SaxParser extends Parser:

  override def apply(content: String): Try[TreeNode] = Try {
    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(true)
    val saxParser = factory.newSAXParser()

    case class Frame(
      name: String,
      children: ListBuffer[TreeNode] = ListBuffer.empty,
      textBuffer: StringBuilder = new StringBuilder()
    )

    var rootNode: Option[TreeNode] = None
    val stack = Stack[Frame]()

    val handler = new DefaultHandler:
      override def startElement(uri: String, localName: String, qName: String, attributes: Attributes): Unit =
        val tagName = if qName != null && qName.nonEmpty then qName else localName
        stack.push(Frame(name = tagName))

      override def characters(ch: Array[Char], start: Int, length: Int): Unit =
        if stack.nonEmpty then
          stack.top.textBuffer.appendAll(ch, start, length)

      override def endElement(uri: String, localName: String, qName: String): Unit =
        val finishedFrame = stack.pop()
        val node =
          if finishedFrame.children.nonEmpty then
            TreeNode(name = finishedFrame.name, children = finishedFrame.children.toList, text = None)
          else
            val trimmed = finishedFrame.textBuffer.toString.trim
            val optText = if trimmed.nonEmpty then Some(trimmed) else None
            TreeNode(name = finishedFrame.name, children = Nil, text = optText)

        if stack.nonEmpty then
          stack.top.children += node
        else
          rootNode = Some(node)

    val is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
    saxParser.parse(new InputSource(is), handler)

    rootNode.getOrElse(throw new IllegalStateException("Failed to parse root element from XML"))
  }
