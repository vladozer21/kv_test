package Irc_chat
import ChatRoom._
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.scaladsl.EntityRef

object User {


  sealed trait Command extends JsonSerializable
  case class JoinRoom(room: EntityRef[ChatRoom.Command]) extends Command
  case class LeaveRoom(room: EntityRef[ChatRoom.Command]) extends Command
  case class Message(room: EntityRef[ChatRoom.Command], mes: String) extends Command
  case class PrivateMessage(mes: String) extends Command

  def apply(address: String, port: Int): Behavior[Command] = Behaviors.receive { (context, message) =>


    message match {
      case JoinRoom(room) =>
        room ! Subscribe(context.self)
        Behaviors.same
      case LeaveRoom(room) =>
        room ! Leave(context.self)
        Behaviors.same
      case Message(room, mes) =>
        room ! putMessage(context.self, mes)
        Behaviors.same
      case PrivateMessage(mes) => ???

    }
  }
}
