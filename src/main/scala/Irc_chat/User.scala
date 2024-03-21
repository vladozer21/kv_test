package Irc_chat

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.serialization.jackson.JsonSerializable
import javafx.application.Platform
import javafx.scene.control.TextArea

import scala.collection.mutable.ListBuffer


object User {
  sealed trait Command extends JsonSerializable

  case class SendName(name: String) extends Command

  case class JoinedUser(user: ActorRef[User.Command]) extends Command

  case class LeaveUser(user: ActorRef[User.Command]) extends Command

  case class GetMessage(userAc: ActorRef[User.Command], mes: String) extends Command

  case class PrivateMessage(userAc: ActorRef[User.Command], mes: String, receiverName: String) extends Command


  def apply(controller: Controller): Behavior[Command] = Behaviors.setup { context =>
    val userListAc: ListBuffer[String] = ListBuffer().empty


    Behaviors.receiveMessage {

      case JoinedUser(user) =>
        Platform.runLater(() => {
          if (user.path.name != context.self.path.name) {
            user ! SendName(context.self.path.name)
          }
          controller.addUserButton(user.path.name, context.self.path.name, userListAc)
          controller.pubTextArea.appendText(s"${user.path.name} joined \n")
        })
        Behaviors.same

      case SendName(name) =>
        userListAc.addOne(name)
        Behaviors.same

      case GetMessage(user, mes) =>
        controller.pubTextArea.appendText(s"${user.path.name}: $mes \n")
        Behaviors.same


      case LeaveUser(user) =>
        Platform.runLater(() => controller.deleteUser(user.path.name))
        controller.pubTextArea.appendText(s"${user.path.name} leaved \n")
        Behaviors.same

      case PrivateMessage(user, mes, receiverName) =>
        if (context.self.path.name == user.path.name) {
          controller.areas.getOrElseUpdate(receiverName, new TextArea()).appendText(s"${user.path.name}: $mes \n")
          Behaviors.same
        }
        else if (context.self.path.name == receiverName) {
          controller.areas.getOrElseUpdate(user.path.name, new TextArea()).appendText(s"${user.path.name}: $mes \n")
          Behaviors.same
        }
        Behaviors.same

    }

  }

}
