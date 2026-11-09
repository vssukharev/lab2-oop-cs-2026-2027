package org.lab2.loader

import scala.util.Try
import java.nio.file.{Files, Path}
import java.nio.charset.StandardCharsets

/**
 * Loader for the local filesystem.
 */
class LocalFsLoader extends Loader:

  override def load(file: FileProps): Try[String] = Try {
    Files.readString(Path.of(file.path), StandardCharsets.UTF_8)
  }

  override def save(content: String, file: FileProps): Try[Unit] = Try {
    val targetPath = Path.of(file.path)
    val parent = targetPath.getParent
    if parent != null && !Files.exists(parent) then
      Files.createDirectories(parent)
    Files.writeString(targetPath, content, StandardCharsets.UTF_8)
  }
