package org.lab2.loader

import scala.util.Try

/**
 * Strategy trait for file loaders.
 */
trait Loader:
  def load(file: FileProps): Try[String]
  def save(content: String, file: FileProps): Try[Unit]

object Loader:
  def apply(kind: StorageKind): Loader = kind match
    case StorageKind.Local  => LocalFsLoader()
    case StorageKind.Remote => GoogleDriveLoader()
