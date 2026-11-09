package org.lab2

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import org.lab2.view.MainView

object Main extends JFXApp3:

  override def start(): Unit =
    val mainView = new MainView()

    stage = new JFXApp3.PrimaryStage:
      title = "XML to HTML Processor (DOM & SAX)"
      scene = new Scene(mainView, 1050, 720)
