package Irc_chat



import Irc_chat.User.{JoinRoom, LeaveRoom, Message}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityRef, EntityTypeKey}
import com.typesafe.config.ConfigFactory
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.Scene
import javafx.scene.control.{Button, SplitPane, TextArea, TextField}

import javafx.stage.Stage

import scala.io.StdIn.readLine



class ClusterStart(hostUser: String, portUser: Int, nameUser: String) extends App {




    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port= $portUser
         |""".stripMargin).withFallback(ConfigFactory.load())


    val UserNode = ActorSystem(User(hostUser, portUser), "ChatClusterSystem", config)

    val RoomTypeKey = EntityTypeKey[ChatRoom.Command]("Chatroom")
    val sharding = ClusterSharding(UserNode)
    sharding.init(Entity(RoomTypeKey)(createBehavior = _ => ChatRoom()))


    val user = UserNode.systemActorOf(User(hostUser, portUser), nameUser)

    val room: EntityRef[ChatRoom.Command] = sharding.entityRefFor(RoomTypeKey, "room")



    Application.launch(classOf[StartMain], args: _*)


    Thread.sleep(5000)

    user ! JoinRoom(room)

    val m = readLine()
    user ! Message(room, m)

    user ! LeaveRoom(room)




}

object ClusterStart1 extends ClusterStart("localhost", 2551, "User_1")
object ClusterStart2 extends ClusterStart("localhost", 2652, "User_2")
object ClusterStart3 extends ClusterStart("localhost", 2753, "User_3")



class Control {
  @FXML
  val sendButton: Button = new Button()

  @FXML
  val textField: TextField = new TextField()

  @FXML
  val textArea: TextArea = new TextArea()

  @FXML
  def ButtonClicked(action: ActionEvent): Unit = {
    println("ddddddd")
  }


}


class StartMain extends Application {

  override def start(primaryStage: Stage): Unit = {
    val loader = new FXMLLoader(getClass.getResource("/chat/irc_chat/hello-view.fxml"))
    val root = loader.load().asInstanceOf[SplitPane]




    primaryStage.setTitle("Chat Room")
    primaryStage.setScene(new Scene(root))
    primaryStage.show()
  }


}