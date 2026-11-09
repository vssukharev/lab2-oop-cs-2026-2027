package org.lab2.filter

import org.lab2.tree.TreeNode
import scala.collection.mutable.Buffer

/**
 * Composite filter that performs a logical AND (conjunction) over active sub-filters.
 */
class ConjunctiveFilter(val items: Buffer[FilterItem] = Buffer.empty) extends TreeFilter:

  def activeFilters: Seq[TreeFilter] =
    items.filter(_.enabled.value).map(_.filter).toSeq

  override def matchesRecord(record: TreeNode): Boolean =
    val active = activeFilters
    if active.isEmpty then true
    else active.forall(_.matchesRecord(record))

  override def apply(root: TreeNode): TreeNode =
    val active = activeFilters
    if active.isEmpty then
      root
    else
      val kept = root.children.filter(record => active.forall(_.matchesRecord(record)))
      root.copy(children = kept)
