/**
 * Here the helper methods are introduced
 */
package org.lab2

import org.w3c.dom
import scala.collection.Factory

extension (nodes: dom.NodeList)
  def to[F[_]](using factory: Factory[dom.Node, F[dom.Node]]): F[dom.Node] = 
    val builder = factory.newBuilder
    val len = nodes.getLength
    builder.sizeHint(len)
    for i <- 0 until len do
      builder += nodes.item(i)
    builder.result()


