package Irc_chat
import ChatRoom._
import akka.actor.typed.pubsub.Topic
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.EntityRef
import javafx.fxml.FXML
import javafx.scene.control.TextArea



object User {
  trait Command extends JsonSerializable

  case class GetMessage(userAc: ActorRef[User.Command], mes: String ) extends Command
  case class PrivateMessage(mes: String) extends Command


  def apply(address: String, port: Int, cntr: Controller): Behavior[Command] = Behaviors.setup { context =>


    Behaviors.receiveMessage {
      case GetMessage(user, mes) =>
        println(s"${user.path.name}: $mes")
        cntr.textArea.appendText(s"${user.path.name}: $mes \n")
        Behaviors.same
      case PrivateMessage(mes) => ???

    }
  }
}
