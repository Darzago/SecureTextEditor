import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.PaddingType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Implements the logic of the editor 
 * @author Joel
 *
 */
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
	
	List<FileData> dataList = new ArrayList<FileData>();
	
	private PaddingType selectedPadding ;
	
	private EncryptionType selectedEncryption;
	
	private EncryptionMode selectedMode;
	
	/**
	 * Sets the currently selected padding type
	 * @param paddingType padding type to be set
	 */
	public void setPaddingType(PaddingType paddingType)
	{
		this.selectedPadding = paddingType;
	}
	
	/**
	 * Sets the currently selected encryption type
	 * @param encryptionType encryption type to be set
	 */
	public void setEncryptionType(EncryptionType encryptionType)
	{
		this.selectedEncryption = encryptionType;
	}
	
	
	/**
	 * Sets the currently selected encryption mode
	 * @param encryptionMode encryption mode to be set
	 */
	public void setEncryptionMode(EncryptionMode encryptionMode)
	{
		this.selectedMode = encryptionMode;
	}
	
	/**
	 * Displays a new window if the current file has been changed but not saved, otherwise opens the new file
	 */
	public void newFileDialogue()
	{
		
		//FileData testdata = new FileData(PaddingType.NoPadding,EncryptionType.none,new File("Peter.txt"),"hashValue");
		//dataList.add(testdata);
		//fileManager.writeConfig(dataList);
		
		//fileManager.loadConfig("test.xml");
		
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
	
	/**
	 * Displays a new window if the current file has been changed but not saved, otherwise creates a new file
	 * TODO Code duplication
	 */
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
	 * Opens a file from the file system and displays it in the editor
	 */
	private void openFile()
	{
    	FileChooser fileChooser = new FileChooser();
    	File fileToOpen = fileChooser.showOpenDialog(null);
    	
    	//if clause prevents an error if the user pressed the x of the 'save' dialogue
    	if(fileToOpen != null){
    		String openFileName = fileToOpen.getAbsolutePath();
    		
    		this.documentOrigin = openFileName;
    		
    		this.setText(fileManager.openFileFromPath(openFileName, selectedEncryption, selectedMode, selectedPadding));
    		
    		updateTitle(fileToOpen.getName());
    	}
	}
	
	/**
	 * Saves the current content of the editor in a user specified directory using the filechooser
	 */
	public void saveFileAs()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName(documentName);
		
		//Sets the datatype that is displayed in the filechooser
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File fileToSave = fileChooser.showSaveDialog(null);
		
		//if claus eprevents an error if the user pressed on the x of the save dialogue
		if(fileToSave!= null){
			
			changeFileOrigin(fileToSave);
			
			try {
				fileManager.saveFileInPath(fileToSave, this.getText(), selectedEncryption, selectedMode, selectedPadding);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			myStage.setTitle(documentName);
			
			updateTitle(fileToSave.getName());
		}
	}
	
	/**
	 * Saves the content in the editor in a specified directory/its origin directory
	 */
	public void saveFile()
	{
		if(documentOrigin != null)
		{			
			File fileToWrite = new File(documentOrigin); 
			try {
				fileManager.saveFileInPath(fileToWrite, this.getText(), selectedEncryption, selectedMode, selectedPadding);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	 * @param _myStage main stage 
	 * @param _encryptionType currently selected {@link EncryptionType}
	 * @param _selectedMode currently selected {@link EncryptionMode}
	 * @param _selectedPadding currently selected {@link PaddingType}
	 */
	TextEditor(Stage _myStage, EncryptionType _encryptionType, EncryptionMode _selectedMode,  PaddingType _selectedPadding)
	{
		this.myStage = _myStage;
		updateTitle(defaultName);
		this.setPrefRowCount(999999);
		this.selectedEncryption = _encryptionType;
		this.selectedPadding = _selectedPadding;
		this.selectedMode = _selectedMode;
		
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
		
		//Create and modify an alert object
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
