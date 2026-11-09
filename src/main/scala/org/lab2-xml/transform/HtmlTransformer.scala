package org.lab2.transform

import org.lab2.tree.TreeNode
import scala.util.Try
import java.io.{StringReader, StringWriter}
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.{StreamResult, StreamSource}

/**
 * Transforms a TreeNode into an HTML document using an XML serialization stage and an XSLT stylesheet.
 */
class HtmlTransformer(
  val xmlStage: XmlTransformer = new XmlTransformer(),
  val xslContent: String
) extends TreeTransformer:

  override def apply(root: TreeNode): Try[String] = Try {
    val xmlString = xmlStage(root).get

    val factory = TransformerFactory.newInstance()
    val xsltSource = new StreamSource(new StringReader(xslContent))
    val transformer = factory.newTransformer(xsltSource)

    val xmlInput = new StreamSource(new StringReader(xmlString))
    val outWriter = new StringWriter()
    transformer.transform(xmlInput, new StreamResult(outWriter))

    outWriter.toString
  }
