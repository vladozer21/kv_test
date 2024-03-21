package Irc_chat

import Irc_chat.ChatRoom.{JoinUser, Leave, PutMessage, PutPrivateMessage}
import akka.actor.typed.{ActorRef, ActorSystem}
import com.typesafe.config.{Config, ConfigFactory}
import javafx.fxml.FXML
import javafx.scene.control.{Button, SplitPane, TextArea, TextField}
import javafx.scene.layout.{AnchorPane, VBox}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Controller {

  @FXML var splitPane: SplitPane = _
  @FXML var anchorChat: AnchorPane = _

  @FXML var usersList: VBox = _
  var userButton: Button = _

  @FXML var pubTextArea: TextArea = _
  @FXML var sendButton: Button = _
  @FXML var textField: TextField = _

  val areas: mutable.Map[String, TextArea] = mutable.Map().empty


  var user: ActorRef[User.Command] = _
  var room: ActorRef[ChatRoom.Command] = _
  var system: ActorSystem[User.Command] = _


  def startSystem(userName: String, port: Int): Unit = {
    val config: Config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port= $port
         |""".stripMargin).withFallback(ConfigFactory.load())


    system = ActorSystem(User(this), "ChatClusterSystem", config)


    user = system.systemActorOf(User(this), userName)


    room = system.systemActorOf(ChatRoom(), "room")



    room ! JoinUser(user)


  }

  def stopSystem(): Unit = {
    room ! Leave(user)
    system.terminate()
  }


  @FXML
  def buttonClicked(): Unit = {
    val mes = textField.getText
    room ! PutMessage(user, mes)
    textField.clear()
  }


  def addUserButton(anotherUserName: String, selfUserName: String, connectedUsersList: ListBuffer[String]): Unit = {


    if (anotherUserName != selfUserName) {
      createUserButton(anotherUserName)
    } else {
      Thread.sleep(2000)
      connectedUsersList.map(userName => {
        createUserButton(userName)
      })
    }


    def createUserButton(nameUserButton: String) = {
      userButton = new Button(nameUserButton)

      val userTextArea = new TextArea()
      userTextArea.setLayoutX(6.0)
      userTextArea.setLayoutY(6.0)
      userTextArea.setPrefSize(426.0, 330.0)
      userTextArea.setPromptText(nameUserButton)
      areas.put(nameUserButton, userTextArea)

      val send_but = new Button("Отправить")
      send_but.setLayoutX(354.0)
      send_but.setLayoutY(347.0)
      send_but.setMnemonicParsing(false)
      send_but.setPrefSize(86, 54)

      val text_field = new TextField()
      text_field.setLayoutY(346.0)
      text_field.setPrefSize(354.0, 54.0)

      send_but.setOnAction(_ => {
        room ! PutPrivateMessage(user, text_field.getText, userTextArea.getPromptText)
        text_field.clear()
      })

      userButton.setOnAction(_ => {
        anchorChat.getChildren.clear()
        anchorChat.getChildren.addAll(text_field, send_but, userTextArea)
        splitPane.getItems.set(1, anchorChat)
      })
      usersList.getChildren.add(userButton)
    }
  }

  def deleteUser(userName: String): Unit = {
    usersList.getChildren.removeIf(but => {
      val button = but.asInstanceOf[Button].getText
      button == userName
    })
  }

  @FXML
  def PublicButtonClicked(): Unit = {
    anchorChat.getChildren.clear()
    anchorChat.getChildren.addAll(textField, sendButton, pubTextArea)
    splitPane.getItems.set(1, anchorChat)

  }

}