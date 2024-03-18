package Irc_chat

import scala.collection.immutable._
import Irc_chat.User.{GetMessage, JoinedUser, PrivateMessage}
import akka.actor.typed.pubsub.Topic
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.typed.Cluster
import akka.cluster.typed.Join
import akka.cluster.typed.Subscribe
import akka.cluster.typed._
import javafx.scene.control.TextArea

import java.util
import scala.collection.convert.ImplicitConversions.`iterable AsScalaIterable`
import scala.collection.mutable

object ChatRoom {

  trait Command extends JsonSerializable

  case class JoinUser(user: ActorRef[User.Command]) extends Command

  case class Leave(user: ActorRef[User.Command]) extends Command

  case class PutMessage(user: ActorRef[User.Command], message: String) extends Command

  case class PutPrivateMessage(user: ActorRef[User.Command], message: String, receiverName: String) extends Command



  def apply(controller: Controller): Behavior[Command] = Behaviors.setup { context =>
    val topic: ActorRef[Topic.Command[User.Command]] = context.spawn(Topic[User.Command]("chat-room-1"), "chat-room-topic-1")
    val topicPrivate: ActorRef[Topic.Command[User.Command]] = context.spawn(Topic[User.Command]("private-room"), "private-room-topic")
    val cluster = Cluster(context.system)


    Behaviors.receiveMessage {
      case JoinUser(user) =>
        topic ! Topic.Subscribe(user)

        Thread.sleep(7000)
        val mem = cluster.state.getMembers.map(member => member.address.port.getOrElse(0)).toList
        topic ! Topic.Publish(JoinedUser(user, mem))

        println(s"User ${user.path.name} joined to ROOM: ${context.self.path.name}")
        Behaviors.same

      case Leave(user) =>
        topic ! Topic.Unsubscribe(user)
        println(s"User ${user.path.name} leaved ROOM: ${context.self.path.name}")
        Behaviors.same


      case PutMessage(user, mes) =>
        topic ! Topic.Publish(GetMessage(user, mes))
        println(s"${context.self.path.name} get MESSAGE:$mes from ${user.path.name}")
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
