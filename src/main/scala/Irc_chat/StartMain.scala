package Irc_chat

import Irc_chat.User.{JoinRoom, LeaveRoom, Message}
import akka.actor.typed.{ActorRef, ActorSystem}
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

//case class ClusterStart(portUser: Int, nameUser: String, controller: Controller) {
//
//
//
////  val UserNode: ActorSystem[User.Command] = ActorSystem(User("localhost", portUser), "ChatClusterSystem", config)
//
//  val RoomTypeKey: EntityTypeKey[ChatRoom.Command] = EntityTypeKey[ChatRoom.Command]("Chatroom")
//  val sharding: ClusterSharding = ClusterSharding(UserNode)
//  sharding.init(Entity(RoomTypeKey)(createBehavior = _ => ChatRoom()))
//
//  val user: ActorRef[User.Command] = UserNode.systemActorOf(User("localhost", portUser), nameUser)
//
//  val room: EntityRef[ChatRoom.Command] = sharding.entityRefFor(RoomTypeKey, "room")
//
//
//  user ! JoinRoom(room)
//
//  /*   val mes = readLine()
//
//     user ! Message(room, mes)*/
//
//  controller.sendButton.setOnAction(event => controller.ButtonClicked(event))
//
//
//}

/*
object ClusterStart1 extends ClusterStart(2551, "User_1")
object ClusterStart2 extends ClusterStart(2652, "User_2")
object ClusterStart3 extends ClusterStart(2753, "User_3")

*/

class StartMain extends Application {


  override def start(primaryStage: Stage): Unit = {


    val loader = new FXMLLoader(getClass.getResource("/chat/irc_chat/hello-view.fxml"))
    val root = loader.load().asInstanceOf[SplitPane]

    val controller = loader.getController.asInstanceOf[Controller]
    controller.startSystem("user-1", 2652)

    primaryStage.setTitle("Chat Room")
    primaryStage.setScene(new Scene(root))

    primaryStage.show()
  }

}

class Controller {

  @FXML var sendButton: Button = _
  @FXML var textArea: TextArea = _
  @FXML var textField: TextField = _

  var ac: ActorSystem[User.Command] = _
  var room: EntityRef[ChatRoom.Command] = _

  def startSystem(userName: String, port: Int): Unit = {
    val config: Config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port= $port
         |""".stripMargin).withFallback(ConfigFactory.load())


    ac = ActorSystem(User("localhost", port), "ChatClusterSystem", config)


    val RoomTypeKey: EntityTypeKey[ChatRoom.Command] = EntityTypeKey[ChatRoom.Command]("Chatroom")
    val sharding: ClusterSharding = ClusterSharding(ac)
    sharding.init(Entity(RoomTypeKey)(createBehavior = _ => ChatRoom()))



    room = sharding.entityRefFor(RoomTypeKey, "room")

    ac ! JoinRoom(room)
  }

  @FXML
  def ButtonClicked(actionEvent: ActionEvent): Unit = {
    val mes = textField.getText
    ac ! Message(room, mes)


  }

}
