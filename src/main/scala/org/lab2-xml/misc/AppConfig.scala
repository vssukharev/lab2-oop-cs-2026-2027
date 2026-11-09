package org.lab2.misc

case class AppConfig(
  googleDataStore: String = ".authdata",
  googleSecretsPath: String = "src/main/resources/client_secrets.json",
  defaultLogPath: String = "app.log"
)
