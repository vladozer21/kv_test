package Irc_chat


import Irc_chat.User.{GetMessage, JoinedUser, LeaveUser, PrivateMessage}
import akka.actor.typed.pubsub.Topic
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}



object ChatRoom {

  sealed trait Command extends JsonSerializable

  case class JoinUser(user: ActorRef[User.Command]) extends Command

  case class Leave(user: ActorRef[User.Command]) extends Command

  case class PutMessage(user: ActorRef[User.Command], message: String) extends Command

  case class PutPrivateMessage(user: ActorRef[User.Command], message: String, receiverName: String) extends Command


  def apply(): Behavior[Command] = Behaviors.setup { context =>
    val topic: ActorRef[Topic.Command[User.Command]] = context.spawn(Topic[User.Command]("chat-room-1"), "chat-room-topic-1")



    Behaviors.receiveMessage {
      case JoinUser(user) =>
        topic ! Topic.Subscribe(user)
        Thread.sleep(7000)
        topic ! Topic.Publish(JoinedUser(user))
        Behaviors.same

      case Leave(user) =>
        topic ! Topic.Unsubscribe(user)
        topic ! Topic.Publish(LeaveUser(user))
        Behaviors.same


      case PutMessage(user, mes) =>
        topic ! Topic.Publish(GetMessage(user, mes))
        Behaviors.same

      case PutPrivateMessage(user, mes, receiverName) =>
        topic ! Topic.Publish(PrivateMessage(user, mes, receiverName))
        Behaviors.same

      case _ =>
        println("Received message from unknown user.")
        Behaviors.same
    }
  }


}
