package logic;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.PaddingType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistence.MetaData;
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
	
	//List of all currently known metadata
	List<MetaData> dataList;
	
	//Metadata of the file the editor currently edits
	private MetaData currentFileData;
	
	Thread detectionThread;
	
	//Dropdown Menus in the encryption option window
	private ComboBox<PaddingType> paddingTypeBox;
	private ComboBox<EncryptionType> encryptionTypeBox;
	private ComboBox<EncryptionMode> encryptionModeBox;
	private ComboBox<HashFunction> hashFunctionModeBox;
	
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
	 * Writes metadata in a Gridpane to make it readable to the user
	 * @param pane Gridpane to write metadata
	 */
	public void writeListInGrid(GridPane pane)
	{
		Text nameText = new Text("Name:");
		nameText.setUnderline(true);
		pane.add(nameText, 0, 0);
		
		Text typeText = new Text("Type:");
		typeText.setUnderline(true);
		pane.add(typeText, 1, 0);
		
		Text modeText = new Text("Mode:");
		modeText.setUnderline(true);
		pane.add(modeText, 2, 0);
		
		Text paddingText = new Text("Padding:");
		paddingText.setUnderline(true);
		pane.add(paddingText, 3, 0);
				
		int i = 1;
		
		List<MetaData> currentlyPersistentList;
		try {
			currentlyPersistentList = FileManager.loadConfig();
			
			for(MetaData data : currentlyPersistentList)
			{
				pane.add(new Text(data.getFilePath()), 0, i);
				pane.add(new Text(data.getEncryptionType().toString()), 1, i);
				pane.add(new Text(data.getEncryptionMode().toString()), 2, i);
				pane.add(new Text(data.getPaddingType().toString()), 3, i);
				i++;
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * Displays a new window if the current file has been changed but not saved, otherwise creates a new file
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
    		try {
        		String openFileName = fileToOpen.getAbsolutePath();
        		
        		this.documentOrigin = openFileName;
        		
        		findAndOpenFileData(fileToOpen);
        		
				this.setText(FileManager.openFileFromPath(openFileName, currentFileData));
				
				updateTitle(fileToOpen.getName());
			} 
    		catch (Exception e) 
    		{
    			showError(e);
				e.printStackTrace();
			}
    		
    	}
	}
	
	/**
	 * Searches the known metadata for an input file and loads its information into the editor
	 * @param file
	 */
	private void findAndOpenFileData(File file)
	{
		boolean fileFound = false;
		
		try {
			dataList = FileManager.loadConfig();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Iterator<MetaData> iterator = dataList.iterator();
		while (iterator.hasNext())
		{
	         MetaData currentlyViewedData = iterator.next();

	         if(currentlyViewedData.getFilePath().equals(file.getName()))
				{
	        	 	fileFound = true;
					System.out.println("Datei erkannt!");
					
					this.currentFileData = currentlyViewedData;
					
					this.encryptionTypeBox.setValue(currentlyViewedData.getEncryptionType());
					this.encryptionModeBox.setValue(currentlyViewedData.getEncryptionMode());
					this.paddingTypeBox.setValue(currentlyViewedData.getPaddingType());
					this.hashFunctionModeBox.setValue(currentlyViewedData.getHashFunction());
				}
			}
		
		if(!fileFound)
		{
			try 
			{
				MetaData newFileData = new MetaData(paddingTypeBox.getValue(), encryptionTypeBox.getValue(), encryptionModeBox.getValue(), hashFunctionModeBox.getValue(), "", file.getName());
				this.currentFileData = newFileData;
				dataList.add(newFileData);
			} 
			catch (Exception e) 
			{
				showError(e);
				e.printStackTrace();
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
			
			try {
				changeFileOrigin(fileToSave);
				
				FileManager.saveFileInPath(fileToSave, this.getText(), 	currentFileData);
				
				updateFileData();
				
				FileManager.writeConfig(dataList);
				
				myStage.setTitle(documentName);
				
				updateTitle(fileToSave.getName());
				
			} catch (Exception e) {
				showError(e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Updates the metadata of the file currently being edited (in the list of metadata)
	 */
	private void updateFileData()
	{
		try 
		{
			Iterator<MetaData> iterator = dataList.iterator();
			while (iterator.hasNext()){

		         MetaData currentlyViewedData = iterator.next();

		         if(currentlyViewedData.getFilePath().equals(currentFileData.getFilePath()))
					{
						iterator.remove();
					}
				}
			dataList.add(currentFileData);
			FileManager.writeConfig(dataList);
		} 
		catch (Exception e) 
		{
			showError(e);
			e.printStackTrace();
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
			try 
			{
				FileManager.saveFileInPath(fileToWrite, this.getText(), currentFileData);
				
				updateFileData();
				
				FileManager.writeConfig(dataList);
				
				myStage.setTitle(documentName);
				
				updateTitle(fileToWrite.getName());
			} 
			catch (Exception e) 
			{
				showError(e);
				e.printStackTrace();
			}
			
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
			currentFileData.setFilePath(newOrigin.getName());
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
	public TextEditor(Stage _myStage, ComboBox<EncryptionType> encryptionDropDown, ComboBox<EncryptionMode> encryptionModeDropDown,  ComboBox<PaddingType> paddingDropDown, ComboBox<HashFunction> hashFunctionDropDown)
	{
		this.myStage = _myStage;
		updateTitle(defaultName);
		this.setPrefRowCount(999999);
		this.encryptionTypeBox = encryptionDropDown;
		this.paddingTypeBox = paddingDropDown;
		this.encryptionModeBox = encryptionModeDropDown;
		this.hashFunctionModeBox = hashFunctionDropDown;
		
		this.currentFileData = new MetaData(paddingDropDown.getValue(), encryptionDropDown.getValue(), encryptionModeDropDown.getValue(), hashFunctionDropDown.getValue(), "",defaultName);
		
		encryptionDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	currentFileData.setEncryptionType(encryptionDropDown.getValue());
            	
            	if(encryptionDropDown.getValue() == EncryptionType.none)
            	{
            		paddingDropDown.setDisable(true);
            		encryptionModeDropDown.setDisable(true);
            	}
            	else
            	{
            		paddingDropDown.setDisable(false);
            		encryptionModeDropDown.setDisable(false);
            	}
            }
        });
		
		encryptionModeDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	currentFileData.setEncryptionMode(encryptionModeDropDown.getValue());
            }
        });
		
		paddingDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	currentFileData.setPaddingType(paddingDropDown.getValue());
            }
        });
		hashFunctionDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	currentFileData.setHashFunction(hashFunctionDropDown.getValue());
            }
        });
		
		try 
		{
			dataList = FileManager.loadConfig();
		} 
		catch (Exception e) 
		{
			dataList = new ArrayList<MetaData>();
			showError(e);
		}
		
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
	
	public void startUSBDetection()
	{
		detectionThread = new USBDetectionThread();
		detectionThread.start();
	}
	
	public void stopUSBDetection()
	{
		if(detectionThread != null)
		{
			if(detectionThread.isAlive())
			{
				detectionThread.stop();
			}
		}
	}
	
	/**
	 * Displays an error message
	 * @param e Exception that caused the error
	 */
    private void showError(Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error alert");
        alert.setHeaderText(e.getMessage());
        alert.setGraphic(null);
        
        String stackTrace = "";
        for(StackTraceElement element : e.getStackTrace())
        {
        	stackTrace += element.toString() + "\n";
        }
        
        TextArea textArea = new TextArea();
        textArea.setText(stackTrace);
 
        VBox dialogPaneContent = new VBox(textArea);
 
        // Set content for Dialog Pane
        alert.getDialogPane().setContent(dialogPaneContent);
 
        alert.showAndWait();
    }
	
	
}
