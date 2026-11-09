package org.lab2.filter

import org.lab2.tree.TreeNode
import scalafx.beans.property.BooleanProperty

/**
 * Filter item wrapping a TreeFilter with a reactive enabled BooleanProperty for UI checkboxes.
 */
case class FilterItem(
  name: String,
  filter: TreeFilter,
  enabled: BooleanProperty = BooleanProperty(false)
)

/**
 * Base trait for tree filters using the Composite pattern.
 */
trait TreeFilter:
  /**
   * Applies the filter structurally by filtering the direct child records of the root.
   */
  def apply(root: TreeNode): TreeNode =
    val kept = root.children.filter(matchesRecord)
    root.copy(children = kept)

  /**
   * Checks if an entity record (e.g. <student>) matches this filter.
   */
  def matchesRecord(record: TreeNode): Boolean

