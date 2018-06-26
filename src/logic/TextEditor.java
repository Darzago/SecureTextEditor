package logic;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import enums.PaddingType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistence.MetaData;
import persistence.USBMetaData;
import view.PasswordDialog;
import persistence.FileManager;

/**
 * Implements the logic of the editor 
 * @author Joel
 *
 */
public class TextEditor extends TextArea{
	
	//Default Strings displayed in the top of the window
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
	
	//List of all currently known usbData
	List<USBMetaData> usbDataList = new ArrayList<USBMetaData>();
	
	//Metadata of the file the editor currently edits
	private MetaData currentFileData;
	
	//Thread used to detect Usb drives
	private USBDetection detectionThread;
	
	//Dropdown Menus in the encryption option window
	private ComboBox<PaddingType> paddingTypeBox;
	private ComboBox<EncryptionType> encryptionTypeBox;
	private ComboBox<EncryptionMode> encryptionModeBox;
	private ComboBox<HashFunction> hashFunctionModeBox;
	private ComboBox<KeyLength> keyLengthBox;
	
	//Text displayed when you register a usb
	private Text usbRegistrationText;
	
	//Text field where the password is entered
	private PasswordField  passwordField;
	
	/**
	 * Displays a new window if the current file has been changed but not saved, otherwise opens the new file
	 */
	public void newFileDialogue()
	{
		//If there are unsaved changes
		if(textHasChanged)
		{
			//Display an alert, if its confirmed
			if(displaySaveAlert())
			{
				//Open a new file
				newFile();
			}
		}
		//If there are no unsaved changes
		else 
		{
			//open a new file
			newFile();
		}
	}
	
	/**
	 * Displays a new window if the current file has been changed but not saved, otherwise creates a new file
	 */
	public void openFileDialogue()
	{
		//if there are unsaved changes
		if(textHasChanged)
		{
			//Display an alert, if its confirmed
			if(displaySaveAlert())
			{
				//open the file
				openFile();
			}
		}
		//if there are no unsaved changes, open the file
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
		//display a dialog to choose the file to be opened
    	FileChooser fileChooser = new FileChooser();
    	File fileToOpen = fileChooser.showOpenDialog(null);
    	
    	try {
    	
    	//If the chosen file does not exist, or is a directory
    	if(!fileToOpen.exists() || fileToOpen.isDirectory())
    	{
    		throw new Exception("Chosen file is not a file!");
    	}
    	
    	//if clause prevents an error if the user pressed the x of the 'save' dialogue 
    	if(fileToOpen != null){
    		
    			//get the actual path of the file
        		String openFileName = fileToOpen.getAbsolutePath();
        		
        		//set the document origin 
        		this.documentOrigin = openFileName;
        		
        		//Load metadata from the file and set it as the currently viewed data
    			currentFileData = FileManager.loadMetaData(fileToOpen);
    			
    			//if the encryption is password based
    			if(currentFileData.getEncryptionType().getOperationMode() == OperationMode.Passwordbased)
    			{
    				//Display password dialog
    				PasswordDialog test = new PasswordDialog();
    				Optional<String> result = test.showAndWait();
    				if(result.get() != null)
    				{
    					System.out.println(result.get());
    				}
    				else
    				{
    					throw new Exception("No Password was entered.");
    				}
    				
    				//Save the entered password in the metadata to pass it into the decryption
    				currentFileData.setPassword(result.get());
    			}
    			//If the encryption is not password based
    			else
    			{
    				//check if there is a valid usb drive connected
    				checkAndLoadUsbDevice();
    			}
    			
    			//Update the dropdowns of the options menu
    			updateOutput(currentFileData);
    			
        		//set the content of the textarea
				this.setText(FileManager.openFileFromPath(openFileName, currentFileData));
				
				//Update the title displayed in the top of the window
				updateTitle(fileToOpen.getName());
			} 	
    	}
    	catch (Exception e) 
		{
			showError(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the dropdown menus with the input metadata
	 * @param metadata data used to update
	 */
	private void updateOutput(MetaData metadata)
	{
		//Updates the values displayed in the options menu
		this.encryptionTypeBox.setValue(metadata.getEncryptionType());
		this.encryptionModeBox.setValue(metadata.getEncryptionMode());
		this.paddingTypeBox.setValue(metadata.getPaddingType());
		this.hashFunctionModeBox.setValue(metadata.getHashFunction());
		this.keyLengthBox.setValue(metadata.getKeyLength());
	}
	

	
	/**
	 * Saves the current content of the editor in a user specified directory using the filechooser
	 */
	public void saveFileAs()
	{
		try {
			
			//Create filechooser to modify
			FileChooser fileChooser = new FileChooser();
			
			//Set the initial file name displayed in the bottom of the dialog
			fileChooser.setInitialFileName(documentName);
			
			//Sets the datatype that is displayed in the filechooser
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
			fileChooser.getExtensionFilters().add(extFilter);
			
			//Display modified filechooser
			File fileToSave = fileChooser.showSaveDialog(null);
			
			//If the encryption is not password based
			if(currentFileData.getEncryptionType().getOperationMode() != OperationMode.Passwordbased)
			{
				//Check for a valid usb drive
				checkAndLoadUsbDevice();
			}
		
			//if claus eprevents an error if the user pressed on the x of the save dialogue
			if(fileToSave!= null){
				
				//Change the origin of the file to the now selected path
				changeFileOrigin(fileToSave);
				
				//Save the file in the chosen path
				FileManager.saveFileInPath(fileToSave, this.getText(), 	currentFileData);
				
				//updates the title displayed at the top of the window
				updateTitle(fileToSave.getName());
			
			}
		} catch (Exception e) {
			showError(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if there is a valid USB drive connected. If this is the case, the usb drive letter is written into the metadata, otherwise an error is displayed
	 * @throws Exception
	 */
	private void checkAndLoadUsbDevice() throws Exception
	{
		//variable to store found usbdata
		USBMetaData usbData = null;
		
		//If the encryption type is something other than none
		if(currentFileData.getEncryptionType() != EncryptionType.none)
		{
			//Get the connected usb drive
			usbData = getConnectedUsb();
			
			//if none is found
			if(usbData == null)
			{
				//Throw an error
				throw new Exception("No known usb stick connected");
			}
		}
		//write the found usb stick into the current metadata
		currentFileData.setUsbData(usbData);
	}
	
	/**
	 * Gets the USBMetadata of a curretly connected usb drive
	 * @return
	 */
	private USBMetaData getConnectedUsb()
	{
		//Variable used to store the found metadata
		USBMetaData foundUsb = null;
		
		//List of all hsahed of connected usb devices
		int[] usbList = USBDetection.getUSBList();
		
		//Check if any known usb hashes fit any of the hashes in the usb List.
		for(USBMetaData currentData : usbDataList)
		{
			int searchedHash = currentData.getHash();
			for(int currentInt : usbList)
			{
				//If they fit
				if(searchedHash == currentInt)
				{
					//save the found usb device in the 'foundUsb' variable to return it
					foundUsb = currentData;			
				}
			}
		}
		//return the found data
		return foundUsb;
	}
	
	/**
	 * Saves the content in the editor in a specified directory/its origin directory
	 */
	public void saveFile()
	{
		//If the file has an origin
		if(documentOrigin != null)
		{
			//Create a file object from the documentOrigin to save it
			File fileToWrite = new File(documentOrigin);
			
			try 
			{
				//If the encryption type is not password based
				if(currentFileData.getEncryptionType().getOperationMode() != OperationMode.Passwordbased)
				{
					//Check and load a valid usb drive
					checkAndLoadUsbDevice();
				}
				
				//save the content of the editor in the specified path
				FileManager.saveFileInPath(fileToWrite, this.getText(), currentFileData);
				
				
				//Change the title displayed at the top of the window
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
		//if the new origin is not null
		if(newOrigin != null){
			documentOrigin = newOrigin.getPath();
		}
	}
	
	/**
	 * Updates the title row of the window with the input string
	 * @param _title new title
	 */
	private void updateTitle(String _title)
	{
		this.documentName = _title;
		this.myStage.setTitle(title + documentName);
		textHasChanged = false;
	}
	
	/**
	 * Registers a usb drive in the USB config, displays an error if it is already registered.
	 * @param foundDeviceId
	 * @param driveLetter
	 */
	public void registerUSBDrive(int foundDeviceId, String driveLetter)
	{
		USBMetaData foundData = null;
		for(USBMetaData currentData : usbDataList)
		{
			if(currentData.getHash() ==  foundDeviceId)
			{
				foundData = currentData;
				usbRegistrationText.setText("Usb drive has already been registered.");
			}
		}
		
		if (foundData == null)
		{
			usbDataList.add(new USBMetaData(driveLetter, foundDeviceId));
			try 
			{
				FileManager.writeUSBConfig(usbDataList);
			} 
			catch (Exception e) 
			{
				showError(e);
			}
			usbRegistrationText.setText("USB Stick Nr: " + foundDeviceId + " has been registered");
		}
	}
	
	/**
	 * Constructor
	 * Sets the stage and adds a listener to detect if the content of the text area has changed
	 * @param _myStage main stage 
	 * @param _encryptionType currently selected {@link EncryptionType}
	 * @param _selectedMode currently selected {@link EncryptionMode}
	 * @param _selectedPadding currently selected {@link PaddingType}
	 */
	public TextEditor(Stage _myStage, ComboBox<EncryptionType> encryptionDropDown, ComboBox<EncryptionMode> encryptionModeDropDown,  ComboBox<PaddingType> paddingDropDown, ComboBox<HashFunction> hashFunctionDropDown, ComboBox<KeyLength> keylengthDropDown, Text usbRegistrationText, PasswordField  passwordArea)
	{
		this.myStage = _myStage;
		updateTitle(defaultName);
		this.setPrefRowCount(999999);
		this.encryptionTypeBox = encryptionDropDown;
		this.paddingTypeBox = paddingDropDown;
		this.encryptionModeBox = encryptionModeDropDown;
		this.hashFunctionModeBox = hashFunctionDropDown;
		this.usbRegistrationText = usbRegistrationText;
		this.keyLengthBox = keylengthDropDown;
		this.passwordField = passwordArea;
		
		this.currentFileData = new MetaData(paddingDropDown.getValue(), encryptionDropDown.getValue(), encryptionModeDropDown.getValue(), hashFunctionDropDown.getValue(), keylengthDropDown.getValue());
		
		//Disable these dropdown menus since the (at start) selected encryption is 'none'
		paddingDropDown.setDisable(true);
		encryptionModeDropDown.setDisable(true);
		keyLengthBox.setDisable(true);
		
		
		encryptionDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	currentFileData.setEncryptionType(encryptionDropDown.getValue());
            	
            	if(encryptionDropDown.getValue() != null)
            	{
	            	keyLengthBox.getItems().clear();
	            	keyLengthBox.getItems().addAll(KeyLength.getFittingKeyLength(encryptionDropDown.getValue()));
	            	keyLengthBox.setValue(KeyLength.getFittingKeyLength(encryptionDropDown.getValue())[0]);
            	}
            	
            	if(encryptionDropDown.getValue() == EncryptionType.none)
            	{
            		paddingDropDown.setDisable(true);
            		encryptionModeDropDown.setDisable(true);
            		keylengthDropDown.setDisable(true);
            	}
            	else if(encryptionDropDown.getValue() == EncryptionType.ARC4)
            	{
            		paddingDropDown.setDisable(true);
            		encryptionModeDropDown.setDisable(true);
            		keylengthDropDown.setDisable(false);
            	}
            	else
            	{
            		paddingDropDown.setDisable(false);
            		encryptionModeDropDown.setDisable(false);
            		keyLengthBox.setDisable(false);
            	}
            	
            	if(encryptionDropDown.getValue() == EncryptionType.DES)
            	{
            		encryptionModeDropDown.getItems().remove(EncryptionMode.GCM);
            	}
            	else
            	{
            		if(!encryptionModeDropDown.getItems().contains(EncryptionMode.GCM))
            		{
            			encryptionModeDropDown.getItems().add(EncryptionMode.GCM);
            		}
            	}
            }
        });
		
		encryptionModeDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	
            	if(encryptionModeDropDown.getValue() == EncryptionMode.GCM)
            	{
            		paddingDropDown.setValue(PaddingType.NoPadding);
            		paddingDropDown.setDisable(true);
            	}
            	else if(paddingDropDown.isDisable())
            	{
            		paddingDropDown.setDisable(false);
            	}
            	
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

		
		keylengthDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	currentFileData.setKeyLength(keyLengthBox.getValue());
            }
        });
		
		
		try 
		{
			usbDataList = FileManager.loadUSBConfig();
		} 
		catch (Exception e) 
		{	
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
		
		passwordField.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) 
		    {
		    	currentFileData.setPassword(passwordField.getText());
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
	
	/**
	 * Starts the usb detection
	 */
	public void startUSBDetection()
	{
		detectionThread = new USBDetection(this);
		detectionThread.start();
	}
	
	/**
	 * Stops the usb detection
	 */
	public void stopUSBDetection()
	{
		if(detectionThread != null)
		{
			if(detectionThread.isAlive())
			{
				detectionThread.setRunning(false);
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
