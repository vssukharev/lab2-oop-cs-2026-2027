
package org.lab2

type AttributePath = List[String]

trait Parser:
  def apply(fileName: String): Unit
  def apply(content: String): Unit

