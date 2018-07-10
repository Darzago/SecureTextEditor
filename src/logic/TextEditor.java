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
		//stores the found usbdata
		USBMetaData foundData = null;
		
		//Loop through all known usbdata
		for(USBMetaData currentData : usbDataList)
		{
			//if the device is already known
			if(currentData.getHash() ==  foundDeviceId)
			{
				//save the already registered usb sick in the 'foundData' variable to return it later
				foundData = currentData;
				//Display a text that informs the user that this usb stick is already registered
				usbRegistrationText.setText("Usb drive has already been registered.");
			}
		}
		
		//If the usb stick is not known
		if (foundData == null)
		{
			//add it to the lisst of known devices
			usbDataList.add(new USBMetaData(driveLetter, foundDeviceId));
			try 
			{
				//Save the list/config of all known usb stick because the list changed
				FileManager.writeUSBConfig(usbDataList);
			} 
			catch (Exception e) 
			{
				showError(e);
			}
			//Display a registration text
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
		//Used to expand the textarea to the maximum size
		this.setPrefRowCount(999999);
		
		//Set the fields of the editor
		this.myStage = _myStage;
		this.encryptionTypeBox = encryptionDropDown;
		this.paddingTypeBox = paddingDropDown;
		this.encryptionModeBox = encryptionModeDropDown;
		this.hashFunctionModeBox = hashFunctionDropDown;
		this.usbRegistrationText = usbRegistrationText;
		this.keyLengthBox = keylengthDropDown;
		this.passwordField = passwordArea;
		
		//Set the displayed file name to the default name 
		updateTitle(defaultName);
		
		//Create a 'default' metadata
		this.currentFileData = new MetaData(paddingDropDown.getValue(), encryptionDropDown.getValue(), encryptionModeDropDown.getValue(), hashFunctionDropDown.getValue(), keylengthDropDown.getValue());
		
		//Disable these dropdown menus since the (at start) selected encryption is 'none'
		paddingDropDown.setDisable(true);
		encryptionModeDropDown.setDisable(true);
		keyLengthBox.setDisable(true);
		
		//TODO own method?
		//Create the Eventhandler that is called when the value of the menu changes. Implements the dropdown logic of the encryptiontype dropdown menu
		encryptionDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	
            	//Since the selected value changed, set the changed flag
            	textHasChanged = true;
            	
            	//Set the encryption type of the current metadata
            	currentFileData.setEncryptionType(encryptionDropDown.getValue());
            	
            	//Set the content of the keylength drpodown box depending on the selected encryption type
	            keyLengthBox.getItems().clear();
	            
	            //TODO find out error origin
	            //Fixes an error
	            if(encryptionDropDown.getValue() != null)
	            {
		            keyLengthBox.getItems().addAll(KeyLength.getFittingKeyLength(currentFileData.getEncryptionType()));
		            keyLengthBox.setValue(KeyLength.getFittingKeyLength(encryptionDropDown.getValue())[0]);
	            }
            	
            	//if the type is none
            	if(encryptionDropDown.getValue() == EncryptionType.none)
            	{
            		//Disable padding, mode, and keylength menus
            		paddingDropDown.setDisable(true);
            		encryptionModeDropDown.setDisable(true);
            		keylengthDropDown.setDisable(true);
            	}
            	//If it is ARC4
            	else if(encryptionDropDown.getValue() == EncryptionType.ARC4)
            	{
            		//Disable padding and mode, enable keylength menus
            		paddingDropDown.setDisable(true);
            		encryptionModeDropDown.setDisable(true);
            		keylengthDropDown.setDisable(false);
            	}
            	//if it is anything else
            	else
            	{
            		//enable all other dropdown menus
            		paddingDropDown.setDisable(false);
            		encryptionModeDropDown.setDisable(false);
            		keyLengthBox.setDisable(false);
            	}
            	
            	//if the encryption is DES
            	if(encryptionDropDown.getValue() == EncryptionType.DES)
            	{
            		//Remove the GCM Mode from the list of available modes, since the GCM mode does not work with DES
            		encryptionModeDropDown.getItems().remove(EncryptionMode.GCM);
            	}
            	//If the encryption is not DES
            	else
            	{
            		//And the mode menu does not contain the GCM mode
            		if(!encryptionModeDropDown.getItems().contains(EncryptionMode.GCM))
            		{
            			//add the GCM mode to the mode options
            			encryptionModeDropDown.getItems().add(EncryptionMode.GCM);
            		}
            	}
            }
        });
		
		//Eventhandler that adds logic to the Encryprion mode menu
		encryptionModeDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	
            	//Since the selected value changed, set the changed flag
            	textHasChanged = true;
            	
            	//If the selected mode is GCM
            	if(encryptionModeDropDown.getValue() == EncryptionMode.GCM)
            	{
            		//Set the padding to noPadding and Disable the padding menu, because GCM only works with NoPadding
            		paddingDropDown.setValue(PaddingType.NoPadding);
            		paddingDropDown.setDisable(true);
            	}
            	//otherwise of the padding menu is disabled
            	else if(paddingDropDown.isDisable())
            	{
            		//Enable the padding menu
            		paddingDropDown.setDisable(false);
            	}
            	
            	//Set the encryption mode of the current metadata
            	currentFileData.setEncryptionMode(encryptionModeDropDown.getValue());
            }
        });
		
		//Create the Eventhandler for the padding menu that is called when the value of the menu changes.
		paddingDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	
            	//Since the selected value changed, set the changed flag
            	textHasChanged = true;
            	
            	//Set the padding of the current metadata
            	currentFileData.setPaddingType(paddingDropDown.getValue());
            }
        });
		
		//Create the Eventhandler for the hash function menu that is called when the value of the menu changes.
		hashFunctionDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	
            	//Since the selected value changed, set the changed flag
            	textHasChanged = true;
            	
            	//Set the hash function of the current metadata
            	currentFileData.setHashFunction(hashFunctionDropDown.getValue());
            }
        });

		//Create the Eventhandler for the keylength value that is called when the value of the menu changes.
		keylengthDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	
            	//Since the selected value changed, set the changed flag
            	textHasChanged = true;
            	
            	//Set the keylength of the current metadata
            	currentFileData.setKeyLength(keyLengthBox.getValue());
            }
        });
		
		
		try 
		{
			//Load the usb config/the list of all known usb filed
			usbDataList = FileManager.loadUSBConfig();
		} 
		catch (Exception e) 
		{	
			showError(e);
		}
		
		//creates a reference to 'this' Texteditor to refer to it inside the change listener
		TextEditor copy = this;
		
		//Adds a listener to the text area that is called whenever the text changes
		this.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
		    	//If the 'textChanged' flag is not set yet
		    	if(!copy.textHasChanged)
		    	{
		    		//appends an asterisk to show that the text has been changed
		    		copy.myStage.setTitle(title + "*" + documentName);
		    		
		    		//Set the flag since the text has changed
		    		copy.textHasChanged = true;
		    	}		    	
		    }
		});
		
		//Eventhandler that saves the content of the password area to the current metadata if it is changed
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
		//display an alert and wait for a response
		Optional<ButtonType> result = saveQuitAlert.showAndWait();
		
		//If the 'ok' button was pressed
		if(result.get() == ButtonType.OK)
		{
			return true;
		}
		else
			//If the 'cancel' button was pressed
		if(result.get() == ButtonType.CANCEL)
		{
			return false;
		}
		return false;
	}
	
	/**
	 * Starts the usb detection thread
	 */
	public void startUSBDetection()
	{
		detectionThread = new USBDetection(this);
		detectionThread.start();
	}
	
	/**
	 * Stops the usb detection thread
	 */
	public void stopUSBDetection()
	{
		if(detectionThread != null)
		{
			//If the thread is running
			if(detectionThread.isAlive())
			{
				//set the running flag of the thread to false to make it die
				detectionThread.setRunning(false);
			}
		}
	}
	
	/**
	 * Displays an error message
	 * @param e Exception that caused the error
	 */
    private void showError(Exception e) {
    	
    	//Create and modify an alert message
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
        
        //display the alert and wait for a response
        alert.showAndWait();
    }
	
	
}
