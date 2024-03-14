package Irc_chat
import ChatRoom._
import akka.actor.typed.pubsub.Topic
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.EntityRef

object User extends Controller {

  trait Command extends JsonSerializable

  case class GetMessage(user: ActorRef[User.Command], mes: String) extends Command
  case class PrivateMessage(mes: String) extends Command


  def apply(address: String, port: Int): Behavior[Command] = Behaviors.receive { (context, message) =>


    message match {
      case GetMessage(user, mes) =>
        println(s"${user.path.name}: $mes")
        Behaviors.same
      case PrivateMessage(mes) => ???

    }
  }
}
