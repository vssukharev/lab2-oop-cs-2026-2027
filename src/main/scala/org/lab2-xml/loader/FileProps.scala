package org.lab2.loader

enum StorageKind:
  case Local
  case Remote

case class FileProps(
  path: String,
  storageKind: StorageKind,
  id: String = ""
):
  def displayName: String =
    if storageKind == StorageKind.Local then
      java.io.File(path).getName
    else
      if path.nonEmpty then path else s"Drive: $id"
