package org.lab2.tree

type AttributePath = List[String]

/**
 * Immutable tree data structure representing an XML element node.
 * Purely functional without circular parent references.
 */
case class TreeNode(
  name: String,
  children: List[TreeNode] = Nil,
  text: Option[String] = None
):
  /**
   * Recursively extracts all leaves with their reversed ancestor path.
   * Path head is the leaf itself, tail contains parents/ancestors.
   */
  def extractLeaves(pathSoFar: List[TreeNode] = Nil): List[Leaf] =
    val currentPath = this :: pathSoFar
    if children.isEmpty then
      List(Leaf(currentPath))
    else
      children.flatMap(_.extractLeaves(currentPath))

  /**
   * Returns a sorted list of unique values matching the reversed targetQuery path.
   * e.g., targetQuery = List("faculty", "department")
   */
  def getPossibleValues(targetQuery: List[String]): List[String] =
    extractLeaves()
      .filter(_.tagPath.startsWith(targetQuery))
      .map(_.text.trim)
      .filter(_.nonEmpty)
      .distinct
      .sorted
