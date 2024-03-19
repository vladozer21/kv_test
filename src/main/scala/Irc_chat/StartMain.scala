package Irc_chat

import javafx.application.{Application, Platform}
import javafx.fxml.FXMLLoader
import javafx.scene.control.SplitPane
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.WindowEvent

object Launcher extends App {
  Application.launch(classOf[StartMain], args: _*)
}


class StartMain extends Application {


  override def start(primaryStage: Stage): Unit = {


    val loader = new FXMLLoader(getClass.getResource("/chat/irc_chat/chatRoomUI.fxml"))
    val root = loader.load().asInstanceOf[SplitPane]

    val controller = loader.getController.asInstanceOf[Controller]


    controller.startSystem("user-2", 2552)
    Thread.sleep(5000)
    primaryStage.setTitle("Chat Room 3")
    primaryStage.setScene(new Scene(root))


    primaryStage.setOnCloseRequest((event: WindowEvent) => {
      def stop(event: WindowEvent): Unit = {
        controller.stopSystem()
        super.stop()
        Platform.exit()
      }

      stop(event)
    })

    primaryStage.show()

  }

}



