package org.lab2.parser

import org.lab2.tree.TreeNode
import scala.util.Try

/**
 * Strategy trait for XML parsers.
 */
trait Parser:
  def apply(content: String): Try[TreeNode]
