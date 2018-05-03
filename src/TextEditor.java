import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.*;

public class TextEditor extends TextArea{
	
	public final String title = "Secure Text Editor - ";
	private final String defaultName = "Unbenannt.txt";
	
	//Stores the origin path of the current file
	private String documentOrigin;
	//Stores the name of the current file to display it as window title
	private String documentName = defaultName;
	//Main Stage
	private Stage myStage;
	//Alert that is displayed if the User tries to exit without saving the file
	private Alert saveQuitAlert;
	//Flag that shows if there is unsaved progress
	private boolean textHasChanged = false;
	
	FileManager fileManager = new FileManager();
	
	public void newFileDialogue()
	{
		if(textHasChanged)
		{
			if(displaySaveAlert())
			{
				newFile();
			}
		}
		else 
		{
			newFile();
		}
	}
	
	//TODO Code duplication --
	public void openFileDialogue()
	{
		if(textHasChanged)
		{
			if(displaySaveAlert())
			{
				openFile();
			}
		}
		else 
		{
			openFile();
		}
	}
	
	/**
	 * Resets the content and origin of the file
	 */
	private void newFile()
	{
		this.setText("");
		textHasChanged = false;
		this.documentOrigin = null;
		updateTitle(defaultName);
	}
	
	/**
	 * Opens a file from the file system
	 */
	private void openFile()
	{
    	FileChooser fileChooser = new FileChooser();
    	File fileToOpen = fileChooser.showOpenDialog(null);
    	
    	
    	if(fileToOpen != null){
    		String openFileName = fileToOpen.getAbsolutePath();
    		
    		this.documentOrigin = openFileName;
    		
    		this.setText(fileManager.openFileFromPath(openFileName));
    		
    		updateTitle(fileToOpen.getName());
    	}
	}
	
	/**
	 * Saves the current content of the editor in a specified directory using the filechooser
	 */
	public void saveFileAs()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName(documentName + ".txt");
		
		//Sets the datatype that is displayed in the filechooser
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File fileToSave = fileChooser.showSaveDialog(null);
		changeFileOrigin(fileToSave);
		
		fileManager.saveFileInPath(fileToSave, this.getText());
		myStage.setTitle(documentName);
		updateTitle(fileToSave.getName());
	}
	
	/**
	 * Saves the content in the editor in a specified directory/its origin directory
	 */
	public void saveFile()
	{
		if(documentOrigin != null)
		{			
			File fileToWrite = new File(documentOrigin);
			fileManager.saveFileInPath(fileToWrite, this.getText());
			myStage.setTitle(documentName);
			updateTitle(fileToWrite.getName());
		}
		else
		{
			saveFileAs();
		}
		
	}
	


	/**
	 * Changes the origin directory of the file if the new directory is not null
	 * @param newOrigin
	 */
	private void changeFileOrigin(File newOrigin)
	{
		if(newOrigin != null){
			documentOrigin = newOrigin.getPath();
		}
	}	
	private void updateTitle(String _title)
	{
		this.documentName = _title;
		this.myStage.setTitle(title + documentName);
		textHasChanged = false;
	}
	
	/**
	 * Constructor
	 * Sets the stage and adds a listener to detect if the content of the text area has changed
	 */
	TextEditor(Stage _myStage)
	{
		this.myStage = _myStage;
		updateTitle(defaultName);
		this.setPrefRowCount(999999);
		
		TextEditor copy = this;
		this.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
		    	if(!copy.textHasChanged)
		    	{
		    		copy.myStage.setTitle(title + "*" + documentName);
		    		copy.textHasChanged = true;
		    	}		    	
		    }
		});
		
		saveQuitAlert = new Alert(AlertType.CONFIRMATION);
		saveQuitAlert.setGraphic(null);
		saveQuitAlert.setTitle("Warning");
		saveQuitAlert.setContentText("Discard all changes of the current file?");
		saveQuitAlert.setHeaderText(null);
	}
	
	/**
	 * Displays an Alert that the user has to respond to
	 * @return returns the answer of the user (true = OK false = CANCEL)
	 */
	private boolean displaySaveAlert()
	{
		Optional<ButtonType> result = saveQuitAlert.showAndWait();
		if(result.get() == ButtonType.OK)
		{
			return true;
		}
		else
		if(result.get() == ButtonType.CANCEL)
		{
			return false;
		}
		return false;
	}
	
	
}
