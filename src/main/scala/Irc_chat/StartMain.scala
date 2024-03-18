package Irc_chat

import Irc_chat.ChatRoom.{JoinUser, PutMessage, PutPrivateMessage}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.Cluster
import com.typesafe.config.{Config, ConfigFactory}
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.{Button, SplitPane, TextArea, TextField}
import javafx.scene.Scene
import javafx.scene.layout.{AnchorPane, VBox}
import javafx.stage.Stage

import java.net.URL
import java.util.ResourceBundle
import scala.collection.mutable
import scala.collection.mutable.Map



object Launcher extends App {
  Application.launch(classOf[StartMain], args: _*)
}


class StartMain extends Application {


  override def start(primaryStage: Stage): Unit = {


    val loader = new FXMLLoader(getClass.getResource("/chat/irc_chat/hello-view.fxml"))
    val root = loader.load().asInstanceOf[SplitPane]

    val controller = loader.getController.asInstanceOf[Controller]


    controller.startSystem("user-3", 2553)
    Thread.sleep(5000)
    primaryStage.setTitle("Chat Room")
    primaryStage.setScene(new Scene(root))

    primaryStage.show()
  }

}

class Controller {

  @FXML var splitPane: SplitPane = _
  @FXML var anchorChat: AnchorPane = _
  @FXML var anchorUsers: AnchorPane = _

  @FXML var users_list: VBox = _

  @FXML var pubTextArea: TextArea = _

  @FXML var sendButton: Button = _
  var user_but: Button = _

  @FXML var publicChat_but: Button = _

  @FXML var textField: TextField = _

  var userGetTextArea: TextArea = _
  var userSendTextArea: TextArea = _

  val Areas: mutable.Map[String, TextArea] = mutable.Map()



  var user: ActorRef[User.Command] = _
  var room: ActorRef[ChatRoom.Command] = _
  var receiveUser: ActorRef[User.Command] = _


  def startSystem(userName: String, port: Int): Unit = {
    val config: Config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port= $port
         |""".stripMargin).withFallback(ConfigFactory.load())



    val system = ActorSystem(User("localhost", port, this), "ChatClusterSystem", config)

     user = system.systemActorOf(User("localhost", port, this), userName)
     val cluster = Cluster(system)

     //receiveUser = system.systemActorOf(User("localhost", port, this), userName)

     room = system.systemActorOf(ChatRoom(this), "room")

     room ! JoinUser(user)



  }


  @FXML
  def ButtonClicked(actionEvent: ActionEvent): Unit = {
    val mes = textField.getText
    room ! PutMessage(user, mes)

   /* if (anchorChat.getChildren.contains(pubTextArea)) {room ! PutMessage(user, mes)}
    else if (anchorChat.getChildren.contains(userTextArea)) {
      val receiveName = userTextArea.getPromptText
      room ! PutPrivateMessage(user, mes, receiveName)
      println(s"${user.path.name} SEND $mes TO $receiveName")
    }*/
  }


  def addUserButton(anotherUserName: String, selfUserName: String, members: List[Int], selfPort: Int): Unit = {


    if (anotherUserName != selfUserName) {
      user_but = new Button(anotherUserName)

      var userTextArea = new TextArea()
                          userTextArea.setLayoutX(6.0)
                          userTextArea.setLayoutY(6.0)
                          userTextArea.setPrefSize(426.0, 330.0)
                          userTextArea.setPromptText(anotherUserName)
                          Areas.put(anotherUserName, userTextArea)
                          println(Areas.toString())

      var send_but = new Button("Отправить")
                          send_but.setLayoutX(354.0)
                          send_but.setLayoutY(347.0)
                          send_but.setMnemonicParsing(false)
                          send_but.setPrefSize(86, 54)

      var text_field = new TextField()
                          text_field.setLayoutY(346.0)
                          text_field.setPrefSize(354.0, 54.0)

      send_but.setOnAction((actionEvent) => {
        room ! PutPrivateMessage(user, text_field.getText, userTextArea.getPromptText)
      })

      user_but.setOnAction((actionEvent) => {
        anchorChat.getChildren.clear()
        anchorChat.getChildren.addAll(text_field, send_but, userTextArea)
        splitPane.getItems.set(1, anchorChat)
      })
      users_list.getChildren.add(user_but)
    } else {
      Thread.sleep(2000)
      members.filter(port => port != selfPort).map(port => {
        val portName = port.toString.replaceFirst("255", "user-")
        user_but = new Button(portName)


        var userTextArea = new TextArea()
                          userTextArea.setLayoutX(6.0)
                          userTextArea.setLayoutY(6.0)
                          userTextArea.setPrefSize(426.0, 330.0)
                          userTextArea.setPromptText(portName)
                          Areas.put(portName, userTextArea)
                          println(Areas.toString())


        var send_but = new Button("Отправить")
                        send_but.setLayoutX(354.0)
                        send_but.setLayoutY(347.0)
                        send_but.setMnemonicParsing(false)
                        send_but.setPrefSize(86, 54)

        var text_field = new TextField()
                          text_field.setLayoutY(346.0)
                          text_field.setPrefSize(354.0, 54.0)

        send_but.setOnAction((actionEvent) => {
          room ! PutPrivateMessage(user, text_field.getText, userTextArea.getPromptText)
        })

        user_but.setOnAction((actionEvent) => {
          anchorChat.getChildren.clear()
          anchorChat.getChildren.addAll(text_field, send_but, userTextArea)
          splitPane.getItems.set(1, anchorChat)
        })

        users_list.getChildren.add(user_but)
      })
    }
  }


  @FXML
  def PublicButtonClicked(actionEvent: ActionEvent): Unit ={
    anchorChat.getChildren.clear()
    anchorChat.getChildren.addAll(textField, sendButton, pubTextArea)
    splitPane.getItems.set(1, anchorChat)

  }

}

