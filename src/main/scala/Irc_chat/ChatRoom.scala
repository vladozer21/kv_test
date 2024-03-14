package Irc_chat
import akka.serialization.jackson.JsonSerializable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object ChatRoom  {

   sealed trait Command extends JsonSerializable

   case class Subscribe(user: ActorRef[User.Command]) extends Command

   case class Leave(user: ActorRef[User.Command]) extends Command

   case class putMessage(user: ActorRef[User.Command], message: String) extends Command



   def apply(): Behavior[Command] = Behaviors.receive { (context, message) =>
      message match {
         case Subscribe(user) =>
            println(s"User ${user.path.name} joined to ROOM: ${context.self.path.name}")
            Behaviors.same

         case Leave(user) =>
            println(s"User ${user.path.name} leaved ROOM: ${context.self.path.name}")
            Behaviors.same

         case putMessage(user, mes) =>

            println(s"${context.self.path.name} get MESSAGE:$mes from ${user.path.name}")
            Behaviors.same
         case _ =>
            println("Received message from unknown user.")
            Behaviors.same
      }
   }


}
