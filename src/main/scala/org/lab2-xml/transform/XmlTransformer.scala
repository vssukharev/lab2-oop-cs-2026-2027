package org.lab2.transform

import org.lab2.tree.TreeNode
import scala.util.Try

/**
 * Transforms a TreeNode hierarchy into a formatted XML string.
 */
class XmlTransformer extends TreeTransformer:

  override def apply(root: TreeNode): Try[String] = Try {
    val sb = new StringBuilder()
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
    buildXml(root, 0, sb)
    sb.toString()
  }

  private def buildXml(node: TreeNode, indent: Int, sb: StringBuilder): Unit =
    val spaces = "  " * indent
    if node.children.isEmpty then
      node.text match
        case Some(txt) if txt.nonEmpty =>
          sb.append(spaces)
            .append("<").append(node.name).append(">")
            .append(escapeXml(txt))
            .append("</").append(node.name).append(">\n")
        case _ =>
          sb.append(spaces)
            .append("<").append(node.name).append("/>\n")
    else
      sb.append(spaces).append("<").append(node.name).append(">\n")
      for child <- node.children do
        buildXml(child, indent + 1, sb)
      sb.append(spaces).append("</").append(node.name).append(">\n")

  private def escapeXml(str: String): String =
    str.replace("&", "&amp;")
       .replace("<", "&lt;")
       .replace(">", "&gt;")
       .replace("\"", "&quot;")
       .replace("'", "&apos;")
