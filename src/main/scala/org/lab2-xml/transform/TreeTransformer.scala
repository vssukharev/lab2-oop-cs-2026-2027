package org.lab2.transform

import org.lab2.tree.TreeNode
import scala.util.Try

/**
 * Base trait for tree transformers.
 */
trait TreeTransformer:
  def apply(root: TreeNode): Try[String]
