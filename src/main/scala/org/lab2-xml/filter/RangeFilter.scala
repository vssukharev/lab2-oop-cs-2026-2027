package org.lab2.filter

import org.lab2.tree.TreeNode

/**
 * Filter that checks if a numeric leaf value lies within a given range [min, max].
 *
 * @param targetQuery Reversed tag hierarchy, e.g. List("grade", "course", "courses")
 * @param min Optional lower bound
 * @param max Optional upper bound
 */
case class RangeFilter(
  targetQuery: List[String],
  var min: Option[Double] = None,
  var max: Option[Double] = None
) extends TreeFilter:

  override def matchesRecord(record: TreeNode): Boolean =
    if min.isEmpty && max.isEmpty then
      true
    else
      val matchingLeaves = record.extractLeaves().filter(_.tagPath.startsWith(targetQuery))
      if matchingLeaves.isEmpty then
        false
      else
        // Existential semantics: passes if at least one numeric leaf falls in [min, max]
        matchingLeaves.exists { leaf =>
          leaf.text.trim.toDoubleOption match
            case Some(num) =>
              min.forall(m => num >= m) && max.forall(m => num <= m)
            case None =>
              false
        }
