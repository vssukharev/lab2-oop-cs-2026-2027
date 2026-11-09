package org.lab2.filter

import org.lab2.tree.TreeNode

/**
 * Filter that searches for substring matches on leaves matching targetQuery.
 *
 * @param targetQuery Reversed tag hierarchy, e.g. List("faculty", "department")
 * @param query Substring or exact string to search for
 */
case class TextFilter(
  targetQuery: List[String],
  var query: String = ""
) extends TreeFilter:

  override def matchesRecord(record: TreeNode): Boolean =
    val cleanQuery = query.trim.toLowerCase
    if cleanQuery.isEmpty then
      true
    else
      val matchingLeaves = record.extractLeaves().filter(_.tagPath.startsWith(targetQuery))
      if matchingLeaves.isEmpty then
        false
      else
        // Existential semantics: passes if at least one matching leaf contains the query
        matchingLeaves.exists(_.text.toLowerCase == cleanQuery)
