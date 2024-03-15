package Irc_chat

import Irc_chat.ChatRoom.{JoinUser, PutMessage}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.Cluster
import com.typesafe.config.{Config, ConfigFactory}
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.{Button, SplitPane, TextArea, TextField}
import javafx.scene.Scene
import javafx.stage.Stage



object Launcher extends App {
  Application.launch(classOf[StartMain], args: _*)
}


class StartMain extends Application {


  override def start(primaryStage: Stage): Unit = {


    val loader = new FXMLLoader(getClass.getResource("/chat/irc_chat/hello-view.fxml"))
    val root = loader.load().asInstanceOf[SplitPane]

    val controller = loader.getController.asInstanceOf[Controller]
    controller.startSystem("user-2", 2552)

    primaryStage.setTitle("Chat Room")
    primaryStage.setScene(new Scene(root))

    primaryStage.show()
  }

}

class Controller {

  @FXML var sendButton: Button = _
  @FXML var textArea: TextArea = _
  @FXML var textField: TextField = _

  var user: ActorRef[User.Command] = _
  var room: ActorRef[ChatRoom.Command] = _


  def startSystem(userName: String, port: Int): Unit = {
    val config: Config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port= $port
         |""".stripMargin).withFallback(ConfigFactory.load())


    val system = ActorSystem(User("localhost", port, this), "ChatClusterSystem", config)

     user = system.systemActorOf(User("localhost", port, this), userName)
     val cluster = Cluster(system)


     room = system.systemActorOf(ChatRoom(), "room")

     room ! JoinUser(user)



  }


  @FXML
  def ButtonClicked(actionEvent: ActionEvent): Unit = {
    val mes = textField.getText
    room ! PutMessage(user, mes)


  }

}

