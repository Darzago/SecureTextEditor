package logic;
import java.io.File;
import java.util.Iterator;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistence.FileData;
import persistence.FileManager;

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
	
	List<FileData> dataList;

	private ComboBox<PaddingType> paddingTypeBox;
	private ComboBox<EncryptionType> encryptionTypeBox;
	private ComboBox<EncryptionMode> encryptionModeBox;
	
	/**
	 * Displays a new window if the current file has been changed but not saved, otherwise opens the new file
	 */
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
    		
    		findFileData(fileToOpen);
    		
    		this.setText(FileManager.openFileFromPath(openFileName, encryptionTypeBox.getValue(), encryptionModeBox.getValue(), paddingTypeBox.getValue()));
    		
    		updateTitle(fileToOpen.getName());
    	}
	}
	
	private void findFileData(File file)
	{
		Iterator<FileData> iterator = dataList.iterator();
		while (iterator.hasNext()){
	         FileData currentlyViewedData = iterator.next();

	         if(currentlyViewedData.getFilePath().equals(file.getName()))
				{
					System.out.println("Datei erkannt!");
					this.encryptionTypeBox.setValue(currentlyViewedData.getEncryptionType());
					this.encryptionModeBox.setValue(currentlyViewedData.getEncryptionMode());
					this.paddingTypeBox.setValue(currentlyViewedData.getPaddingType());
				}
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
				updateFileData(new FileData(paddingTypeBox.getValue(), 	encryptionTypeBox.getValue(), encryptionModeBox.getValue(), fileToSave.getName()));
				FileManager.saveFileInPath(fileToSave, this.getText(), 	encryptionTypeBox.getValue(), encryptionModeBox.getValue(), paddingTypeBox.getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			myStage.setTitle(documentName);
			
			updateTitle(fileToSave.getName());
		}
	}
	
	private void updateFileData(FileData fileData)
	{
		Iterator<FileData> iterator = dataList.iterator();
		while (iterator.hasNext()){

	         FileData currentlyViewedData = iterator.next();

	         if(currentlyViewedData.getFilePath().equals(fileData.getFilePath()))
				{
					iterator.remove();
				}
			}
		dataList.add(fileData);
		FileManager.writeConfig(dataList);
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
				updateFileData(new FileData(paddingTypeBox.getValue(), encryptionTypeBox.getValue(), encryptionModeBox.getValue(), fileToWrite.getName()));
				FileManager.saveFileInPath(fileToWrite, this.getText(), encryptionTypeBox.getValue(), encryptionModeBox.getValue(), paddingTypeBox.getValue());
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
	public TextEditor(Stage _myStage, ComboBox<EncryptionType> encryptionDropDown, ComboBox<EncryptionMode> encryptionModeDropDown,  ComboBox<PaddingType> paddingDropDown)
	{
		this.myStage = _myStage;
		updateTitle(defaultName);
		this.setPrefRowCount(999999);
		this.encryptionTypeBox = encryptionDropDown;
		this.paddingTypeBox = paddingDropDown;
		this.encryptionModeBox = encryptionModeDropDown;
		
		dataList = FileManager.loadConfig();
		
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
