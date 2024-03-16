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

import scala.collection.{SortedSet, immutable}



object User {
  trait Command extends JsonSerializable

  case class JoinedUser(user: ActorRef[User.Command], members: List[Int]) extends Command
  case class GetMessage(userAc: ActorRef[User.Command], mes: String ) extends Command
  case class PrivateMessage(userAc: ActorRef[User.Command], mes: String, receiverName: String) extends Command


  def apply(address: String, port: Int, controller: Controller): Behavior[Command] = Behaviors.setup { context =>


    Behaviors.receiveMessage {

      case JoinedUser(user, members) =>
       /* //Platform.runLater(() => controller.user_but.setText(s"${user.path.name}"))
        //Platform.runLater(() => controller.users_list.getChildren.add(controller.user_but))
        val name = user.path.name
        Platform.runLater(() => {
          if (name == "user-1") controller.user1_but.setVisible(true)
          if (name == "user-2") controller.user2_but.setVisible(true)
          if (name == "user-3") controller.user3_but.setVisible(true)
        })

*/
        println(members.toString)
        Platform.runLater(() => {
            controller.addUserButton(user.path.name, context.self.path.name)
           controller.addAnotherUserButton(user.path.name, context.self.path.name, members, port)
          controller.pubTextArea.appendText(s"${user.path.name}: joined \n")})
        Behaviors.same

      case GetMessage(user, mes) =>
        println(s"${user.path.name}: $mes")
        controller.pubTextArea.appendText(s"${user.path.name}: $mes \n")
        Behaviors.same
      case PrivateMessage(user, mes, receiverName) =>
        if (controller.userTextArea.getPromptText == receiverName) {
        controller.userTextArea.appendText(s"${user.path.name}: $mes \n")}
        Behaviors.same
    }
  }
}
