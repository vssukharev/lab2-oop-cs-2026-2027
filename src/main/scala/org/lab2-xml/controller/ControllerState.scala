package org.lab2.controller

import scalafx.beans.property.ObjectProperty
import org.lab2.tree.TreeNode
import org.lab2.loader.{FileProps, StorageKind}
import org.lab2.parser.{Parser, DomParser}
import org.lab2.filter.ConjunctiveFilter
import org.lab2.transform.{TreeTransformer, XmlTransformer}
import org.lab2.misc.AppConfig

object ControllerState:
  val sourceFile = ObjectProperty[Option[FileProps]](None)
  val targetFile = ObjectProperty[Option[FileProps]](None)
  val transformFile = ObjectProperty[Option[FileProps]](None)
  val logFile = ObjectProperty[Option[FileProps]](Some(FileProps("app.log", StorageKind.Local)))
  val parser = ObjectProperty[Parser](DomParser())
  val filter = ObjectProperty(ConjunctiveFilter())
  val transformer = ObjectProperty[TreeTransformer](XmlTransformer())
  val sourceTree = ObjectProperty[Option[TreeNode]](None)
  val displayedTree = ObjectProperty[Option[TreeNode]](None)
  val appConfig = ObjectProperty(AppConfig())
