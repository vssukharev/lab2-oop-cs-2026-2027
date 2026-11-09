package org.lab2.tree

/**
 * Represents a leaf element in the tree hierarchy with its full path.
 * The path is reversed for O(1) prepend performance:
 * - head is the leaf TreeNode itself
 * - tail contains ancestor TreeNodes up to the root
 */
case class Leaf(path: List[TreeNode]):
  def node: TreeNode = path.head
  def name: String = node.name
  def text: String = node.text.getOrElse("")
  def tagPath: List[String] = path.map(_.name)
