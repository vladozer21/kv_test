package Irc_chat

import javafx.application.{Application, Platform}
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import javafx.stage.{Modality, Stage}


class StartMain extends Application {

  override def start(primaryStage: Stage): Unit = {


    val mainLoader = new FXMLLoader(getClass.getResource("/chat/irc_chat/chatRoomUI.fxml"))
    val mainRoot = mainLoader.load().asInstanceOf[SplitPane]
    val mainController = mainLoader.getController.asInstanceOf[Controller]


    val inputStage = new Stage()
    val inputLoader = new FXMLLoader(getClass.getResource("/chat/irc_chat/addNameAndPort.fxml"))
    val inputRoot = inputLoader.load().asInstanceOf[AnchorPane]
    val inputController = inputLoader.getController.asInstanceOf[LoginController]
    inputStage.setScene(new Scene(inputRoot))
    inputStage.initModality(Modality.APPLICATION_MODAL)
    inputStage.showAndWait()


    primaryStage.setOnCloseRequest(_ => {
      mainController.stopSystem()
      Platform.exit()
    })

    mainController.startSystem(inputController.name, inputController.port)


    primaryStage.setTitle(inputController.name)
    primaryStage.setScene(new Scene(mainRoot))
    primaryStage.show()
  }
}
