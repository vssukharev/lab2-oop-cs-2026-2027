package org.lab2.filter

import org.lab2.tree.TreeNode
import scala.collection.mutable.Buffer

/**
 * Composite filter that performs a logical OR (disjunction) over active sub-filters.
 */
class DisjunctiveFilter(val items: Buffer[FilterItem] = Buffer.empty) extends TreeFilter:

  def activeFilters: Seq[TreeFilter] =
    items.filter(_.enabled.value).map(_.filter).toSeq

  override def matchesRecord(record: TreeNode): Boolean =
    val active = activeFilters
    if active.isEmpty then true
    else active.exists(_.matchesRecord(record))

  override def apply(root: TreeNode): TreeNode =
    val active = activeFilters
    if active.isEmpty then
      root
    else
      val kept = root.children.filter(record => active.exists(_.matchesRecord(record)))
      root.copy(children = kept)
