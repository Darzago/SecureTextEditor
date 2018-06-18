package logic;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import enums.PBEType;
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
	List<USBMetaData> usbDataList = new ArrayList<USBMetaData>();
	
	//Metadata of the file the editor currently edits
	private MetaData currentFileData;
	
	private USBDetection detectionThread;
	
	//Dropdown Menus in the encryption option window
	private ComboBox<PaddingType> paddingTypeBox;
	private ComboBox<EncryptionType> encryptionTypeBox;
	private ComboBox<EncryptionMode> encryptionModeBox;
	private ComboBox<HashFunction> hashFunctionModeBox;
	private ComboBox<KeyLength> keyLengthBox;
	private ComboBox<OperationMode> operationBox;
	private ComboBox<PBEType> pbeTypeBox;
	
	private Text usbRegistrationText;
	private PasswordField  passwordField;
	
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
        		
        		loadMetaData(fileToOpen);
        		
        		checkForValidUsbDevice();
        		
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
	 * TODO Move to filemanager
	 * Searches the known metadata for an input file and loads its information into the editor
	 * @param file
	 */
	private void loadMetaData(File file)
	{
		
		try {
			MetaData openedData = new MetaData();
			openedData.setEncryptionType(EncryptionType.valueOf(getAttributeAsString(file, "user:Type")));
			openedData.setEncryptionMode(EncryptionMode.valueOf(getAttributeAsString(file, "user:Mode")));
			openedData.setPaddingType(PaddingType.valueOf(getAttributeAsString(file, "user:Padding")));
			openedData.setHashFunction(HashFunction.valueOf(getAttributeAsString(file, "user:HashF")));
			openedData.setHashValue(new String((byte[])Files.getAttribute(file.toPath(), "user:Hash")));
			openedData.setKeyLength(KeyLength.valueOf(getAttributeAsString(file, "user:keyLength")));
			openedData.setOperationMode(OperationMode.valueOf(getAttributeAsString(file, "user:operationMode")));
			openedData.setPbeType(PBEType.valueOf(getAttributeAsString(file, "user:pbeType")));
			openedData.setiV(getAttributeAsString(file, "user:IV"));
			openedData.setSalt((byte[])Files.getAttribute(file.toPath(), "user:salt"));
			currentFileData = openedData;
			updateOutput(currentFileData);
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	
	private void updateOutput(MetaData metadata)
	{
		this.encryptionTypeBox.setValue(metadata.getEncryptionType());
		this.encryptionModeBox.setValue(metadata.getEncryptionMode());
		this.paddingTypeBox.setValue(metadata.getPaddingType());
		this.hashFunctionModeBox.setValue(metadata.getHashFunction());
		this.keyLengthBox.setValue(metadata.getKeyLength());
		this.operationBox.setValue(metadata.getOperationMode());
		this.pbeTypeBox.setValue(metadata.getPbeType());
	}
	
	private String getAttributeAsString(File file, String attributeName) throws IOException
	{
		return (new String((byte[])Files.getAttribute(file.toPath(), attributeName)));
	}
	
	/**
	 * Saves the current content of the editor in a user specified directory using the filechooser
	 */
	public void saveFileAs()
	{
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(documentName);
			
			//Sets the datatype that is displayed in the filechooser
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
			fileChooser.getExtensionFilters().add(extFilter);
			
			checkForValidUsbDevice();
			
			File fileToSave = fileChooser.showSaveDialog(null);
			
			//if claus eprevents an error if the user pressed on the x of the save dialogue
			if(fileToSave!= null){
				
				//TODO WENN DIE AUSGEWÄHLE VERSCHLÜSSELUNG != NULL IST MUSS EIN USB STICK CONNECTED SEIN (ODER DER OPERATIONSMODUS PBE)
				changeFileOrigin(fileToSave);
				
				FileManager.saveFileInPath(fileToSave, this.getText(), 	currentFileData);

				myStage.setTitle(documentName);
				
				updateTitle(fileToSave.getName());
				
			
			}
		} catch (Exception e) {
			showError(e);
			e.printStackTrace();
		}
	}
	
	private void checkForValidUsbDevice() throws Exception
	{
		USBMetaData usbData = null;
		if(currentFileData.getEncryptionType() != EncryptionType.none)
		{
			
			usbData = getConnectedUsb();
			
			if(usbData == null)
			{
				throw new Exception("No known usb stick connected");
			}
		}
		currentFileData.setUsbData(usbData);
	}
	
	private USBMetaData getConnectedUsb()
	{
		USBMetaData foundUsb = null;
		int[] usbList = USBDetection.getUSBList();
		
		for(USBMetaData currentData : usbDataList)
		{
			int searchedHash = currentData.getHash();
			for(int currentInt : usbList)
			{
				if(searchedHash == currentInt)
				{
					foundUsb = currentData;			
				}
			}
		}
		return foundUsb;
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
				checkForValidUsbDevice();
				
				FileManager.saveFileInPath(fileToWrite, this.getText(), currentFileData);

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
	
	public void setCurrentOperationMode(OperationMode mode)
	{
		this.currentFileData.setOperationMode(mode);
	}
	
	/**
	 * Constructor
	 * Sets the stage and adds a listener to detect if the content of the text area has changed
	 * @param _myStage main stage 
	 * @param _encryptionType currently selected {@link EncryptionType}
	 * @param _selectedMode currently selected {@link EncryptionMode}
	 * @param _selectedPadding currently selected {@link PaddingType}
	 */
	public TextEditor(Stage _myStage, ComboBox<OperationMode> operationModeDropDown, ComboBox<EncryptionType> encryptionDropDown, ComboBox<EncryptionMode> encryptionModeDropDown,  ComboBox<PaddingType> paddingDropDown, ComboBox<HashFunction> hashFunctionDropDown, ComboBox<KeyLength> keylengthDropDown, Text usbRegistrationText, PasswordField  passwordArea, ComboBox<PBEType> pbeTypeDropDown)
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
		this.operationBox = operationModeDropDown;
		this.passwordField = passwordArea;
		this.pbeTypeBox = pbeTypeDropDown;
		
		this.currentFileData = new MetaData(operationModeDropDown.getValue(), paddingDropDown.getValue(), encryptionDropDown.getValue(), encryptionModeDropDown.getValue(), hashFunctionDropDown.getValue(), keylengthDropDown.getValue(), "", pbeTypeDropDown.getValue());
		
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
            	
            	if(encryptionDropDown.getValue() == EncryptionType.none || encryptionDropDown.getValue() == EncryptionType.ARC4)
            	{
            		paddingDropDown.setDisable(true);
            		encryptionModeDropDown.setDisable(true);
            		keyLengthBox.setDisable(true);
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
		
		pbeTypeDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	textHasChanged = true;
            	currentFileData.setPbeType(pbeTypeDropDown.getValue());
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
	
	public void startUSBDetection()
	{
		detectionThread = new USBDetection(this);
		detectionThread.start();
	}
	
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
