package Irc_chat

import Irc_chat.ChatRoom.{JoinUser, PutMessage}
import Irc_chat.User._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.Cluster
import akka.cluster.typed.ClusterStateSubscription
import akka.cluster.typed._
import akka.cluster.ClusterEvent._
import akka.cluster.MemberStatus
import akka.actor.typed.pubsub.Topic
import akka.actor.typed.pubsub._
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityRef, EntityTypeKey}
import com.typesafe.config.{Config, ConfigFactory}
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.{Button, SplitPane, TextArea, TextField}
import javafx.scene.Scene
import javafx.stage.Stage

import java.net.URL
import java.util.ResourceBundle
import scala.io.StdIn.readLine


object Launcher extends App {
  Application.launch(classOf[StartMain], args: _*)
}


class StartMain extends Application {


  override def start(primaryStage: Stage): Unit = {


    val loader = new FXMLLoader(getClass.getResource("/chat/irc_chat/hello-view.fxml"))
    val root = loader.load().asInstanceOf[SplitPane]

    val controller = loader.getController.asInstanceOf[Controller]
    controller.startSystem("user-2", 2652)

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


    val system = ActorSystem(User("localhost", port), "ChatClusterSystem", config)

     user = system.systemActorOf(User("localhost", port), userName)
     val cluster = Cluster(system)


     room = system.systemActorOf(ChatRoom(), "room")

    room ! JoinUser(user)



  }


  @FXML
  def ButtonClicked(actionEvent: ActionEvent): Unit = {
    val mes = textField.getText

    appendText(user, mes)
    room ! PutMessage(user, mes)


  }

  def appendText(user: ActorRef[User.Command], mes: String): Unit = {
    textArea.appendText(s"${user.path.name}:  $mes \n")
  }

}

