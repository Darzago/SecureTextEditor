package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


//Source: https://gist.github.com/drguildo/ba2834bf52d624113041
public class PasswordDialog extends Dialog<char[]> {
  private PasswordField passwordField;

  public PasswordDialog() {
    setTitle("Password");
    setHeaderText("Please enter your password.");

    ButtonType passwordButtonType = new ButtonType("Decrypt", ButtonData.OK_DONE);
    getDialogPane().getButtonTypes().addAll(passwordButtonType, ButtonType.CANCEL);

    //Not secure since passwords should not be stored in a string but in a char array
    passwordField = new PasswordField();
    passwordField.setPromptText("Password");

    HBox hBox = new HBox();
    hBox.getChildren().add(passwordField);
    hBox.setPadding(new Insets(20));

    HBox.setHgrow(passwordField, Priority.ALWAYS);

    getDialogPane().setContent(hBox);

    Platform.runLater(() -> passwordField.requestFocus());

    setResultConverter(dialogButton -> {
      if (dialogButton == passwordButtonType) {
    	  
    	  //Convert the input into a char array
    	  CharSequence charSequence = passwordField.getCharacters();
    	  char[] array = new char[charSequence.length()];
    	  for(int i = 0; i < charSequence.length(); i++)
    	  {
    		  array[i] = charSequence.charAt(i);
    	  }
        return array;
      }
      return null;
    });
  }

  public PasswordField getPasswordField() {
    return passwordField;
  }
}