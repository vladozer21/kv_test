package Irc_chat

import ChatRoom._
import akka.actor.typed.pubsub.Topic
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.Member
import akka.cluster.sharding.typed.scaladsl.EntityRef
import com.sun.javafx.PlatformUtil
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.TextArea

import scala.collection.{SortedSet, immutable, mutable}


object User {
  trait Command extends JsonSerializable

  case class JoinedUser(user: ActorRef[User.Command], members: List[Int]) extends Command

  case class GetMessage(userAc: ActorRef[User.Command], mes: String) extends Command

  case class PrivateMessage(userAc: ActorRef[User.Command], mes: String, receiverName: String) extends Command


  def apply(address: String, port: Int, controller: Controller): Behavior[Command] = Behaviors.setup { context =>


    Behaviors.receiveMessage {

      case JoinedUser(user, members) =>
        println(members.toString)
        Platform.runLater(() => {
          controller.addUserButton(user.path.name, context.self.path.name, members, port)
          controller.pubTextArea.appendText(s"${user.path.name} joined \n")
        })
        Behaviors.same

      case GetMessage(user, mes) =>
        println(s"${user.path.name}: $mes")
        controller.pubTextArea.appendText(s"${user.path.name}: $mes \n")
        Behaviors.same
      case PrivateMessage(user, mes, receiverName) =>
        if (context.self.path.name == user.path.name) {
          println(controller.Areas.toString)
          println(receiverName)
          println(user.path.name + " send to " + receiverName)
          controller.Areas.getOrElse(receiverName, null).appendText(s"${user.path.name}: $mes \n")
          Behaviors.same
        }
        else if (context.self.path.name == receiverName) {
          println(controller.Areas.toString)
          println(user.path.name)
          println("You have mes from " + user.path.name)
          controller.Areas.getOrElse(user.path.name, null).appendText(s"${user.path.name}: $mes \n")
          Behaviors.same
        }
        Behaviors.same

    }

  }

}
