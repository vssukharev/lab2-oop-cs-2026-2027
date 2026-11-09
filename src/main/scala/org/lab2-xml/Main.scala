
package org.lab2




@main def main(): Unit =
  val strategy = DomStrategy("test.xml")
  println( strategy.getPossibleValues("student" :: "department" :: "faculty" :: Nil) )
  println( strategy.getPossibleValues("student" :: "full_name" :: Nil) )

