package Irc_chat
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object ChatRoom extends Control {

   sealed trait Command extends JsonSerializable

   case class Subscribe(user: ActorRef[User.Command]) extends Command

   case class Leave(user: ActorRef[User.Command]) extends Command

   case class putMessage(user: ActorRef[User.Command], message: String) extends Command



   def notJoined: Behavior[Command] = Behaviors.receive { (context, message) =>
      message match {
         case Subscribe(user) =>

            println(s"User ${user.path.name} joined to ROOM: ${context.self.path.name}")
            joined(user) // Переключаемся на поведение после входа в комнату
         case _ =>
            println("Received message before user joined the room.")
            Behaviors.same
      }
   }

   def joined(user: ActorRef[User.Command]): Behavior[Command] = Behaviors.receive { (context, message) =>
      message match {

         case Leave(user) =>
            println(s"User ${user.path.name} leaved ROOM: ${context.self.path.name}")
            notJoined // Переключаемся на поведение до входа в комнату
         case putMessage(user, mes) =>

            println(s"${context.self.path.name} get MESSAGE:$mes from ${user.path.name}")
            Behaviors.same
         case _ =>
            println("Received message from unknown user.")
            Behaviors.same
      }
   }

   def apply(): Behavior[Command] = notJoined
}
