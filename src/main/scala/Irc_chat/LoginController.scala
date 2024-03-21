package Irc_chat

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}

class LoginController {
  @FXML var addNameField: TextField = _
  @FXML var addPortField: TextField = _
  @FXML var loginButton: Button = _
  var name = ""
  var port = 0


  @FXML
  def handleLogin(event: ActionEvent): Unit = {
    name = addNameField.getText.trim
    port = addPortField.getText.trim.toInt
    loginButton.getScene.getWindow.hide()
  }


}
