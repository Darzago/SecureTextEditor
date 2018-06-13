package view;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import enums.PaddingType;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.TextEditor;


/**
 * Builds the scene tree and creates event handler
 * @author Joel
 *
 */
public class JavaFxMainClass extends Application{
	
	TextEditor editor;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Stage encryptionOptionStage = new Stage();
		

		
		TabPane tabPane = new TabPane();
		MenuBar menuBar = new MenuBar();
		
		//Event Handlers of all menu options within the file menu
		Menu fileMenu = new Menu("File");
			MenuItem menuNewItem = new MenuItem("New");
			MenuItem menuOpenItem = new MenuItem("Open");
			MenuItem menuSaveItem = new MenuItem("Save");
			MenuItem menuSaveAsItem = new MenuItem("Save As");
		fileMenu.getItems().addAll(menuNewItem, menuOpenItem, menuSaveItem, menuSaveAsItem);
		
		//Menu for encryption options
		Menu encryptionMenu = new Menu("Encryption");
			MenuItem encryptionsItem = new MenuItem("Options");
			MenuItem registerUsbItem = new MenuItem("Register USB drive");
		encryptionMenu.getItems().addAll(encryptionsItem, registerUsbItem);
		
		//Adds all menu items to the "file" menu
		menuBar.getMenus().addAll(fileMenu, encryptionMenu);
		
		//Sets up keyboard shortcuts
		menuNewItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		menuOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		menuSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		//Options Menu	 ---------------------------------------------------------------------------------------
		
				//Label for the encryption dropdown
						Label operationLabel = new Label("Operation Mode:  ");
							ComboBox<OperationMode> operationDropDown = new ComboBox<OperationMode> ();
							operationDropDown.setValue(OperationMode.Symmetric);
						operationDropDown.getItems().addAll(OperationMode.values());
						
						//Label for the encryption dropdown
						Label encryptionLabel = new Label("Encryption:  ");
							ComboBox<EncryptionType> encryptionDropDown = new ComboBox<EncryptionType> ();
							encryptionDropDown.setValue(EncryptionType.none);
						encryptionDropDown.getItems().addAll(EncryptionType.getValuesByOperation(OperationMode.Symmetric));
						
						
						Label modeLabel = new Label("Mode:  ");
							ComboBox<EncryptionMode> modeDropDown = new ComboBox<EncryptionMode> ();
							modeDropDown.setValue(EncryptionMode.ECB);
						modeDropDown.getItems().addAll(EncryptionMode.values());
						
						
						Label paddingLabel = new Label("Padding:  ");
							ComboBox<PaddingType> paddingDropDown = new ComboBox<PaddingType> ();
							paddingDropDown.getItems().addAll(PaddingType.values());
						paddingDropDown.setValue(PaddingType.NoPadding);
						
						Label hashFunctionLabel = new Label("Hash Function:");
							ComboBox<HashFunction> hashFunctionDropDown = new ComboBox<HashFunction> ();
							hashFunctionDropDown.setValue(HashFunction.NONE);
						hashFunctionDropDown.getItems().addAll(HashFunction.values());
						
						//Adds all dropdown menus and labels to a GridPane
						GridPane encryptionGridPane = new GridPane();
						encryptionGridPane.add(operationLabel, 0, 0);
						encryptionGridPane.add(operationDropDown, 0, 1);
						encryptionGridPane.add(encryptionLabel, 0, 2);
						encryptionGridPane.add(encryptionDropDown, 0, 3);
						encryptionGridPane.add(modeLabel, 1, 2);
						encryptionGridPane.add(modeDropDown, 1, 3);
						encryptionGridPane.add(paddingLabel, 2, 2);
						encryptionGridPane.add(paddingDropDown, 2, 3);
						encryptionGridPane.add(hashFunctionLabel, 3, 2);
						encryptionGridPane.add(hashFunctionDropDown, 3, 3);
						encryptionGridPane.setHgap(10);
						encryptionGridPane.setVgap(5);
						encryptionGridPane.setAlignment(Pos.TOP_LEFT);
						encryptionGridPane.setPadding(new Insets(10, 10, 10, 10));
						
						Label keyLengthLabel = new Label("Key Length: ");
							ComboBox<KeyLength> keyLengthDropDown = new ComboBox<KeyLength> ();
							keyLengthDropDown.setValue(KeyLength.x1024);
						keyLengthDropDown.getItems().addAll(KeyLength.values());
						
						Label passwordLabel = new Label("Password: ");
						PasswordField  passwordArea = new PasswordField();
						
						//TODO Übergabe von keylengthdropdown, password field
						
						//TODO UMBAUEN DER haupt GRIDPANE JE NACH BLUB MANNN YAY LÖSUNG ENDLICH
						
						//Adds button(s) below the dropdown menus 
						GridPane encryptionButtonsBox = new GridPane();
							Button closeButton = new Button("Close");
							encryptionButtonsBox.add(closeButton, 1, 0);
						encryptionButtonsBox.setPadding(new Insets(10, 10, 10, 10));
						encryptionButtonsBox.setHgap(10);
						
						encryptionButtonsBox.setAlignment(Pos.CENTER_RIGHT);
				
				VBox optionGeneralLayout = new VBox(encryptionGridPane, encryptionButtonsBox);
				
				Scene encryptionOptionWindow = new Scene(optionGeneralLayout, 600, 200);
				encryptionOptionStage.setScene(encryptionOptionWindow);
				encryptionOptionStage.setTitle("Encryption Options");
				encryptionOptionStage.getIcons().add(new Image("gear-256.png"));
				
		
//USB Registration Stage ---------------------------------------------------------------------
		Stage usbRegistrationStage = new Stage();
		
		GridPane usbRegistrationLayout = new GridPane();
		Text usbText = new Text("Plug in the usb stick to register");
		usbRegistrationLayout.add(usbText, 0, 0);
		usbRegistrationLayout.setAlignment(Pos.CENTER);
		usbRegistrationLayout.setHgap(15);
		usbRegistrationLayout.setVgap(3);

		Scene usbRegistrationWindow = new Scene(usbRegistrationLayout, 450, 180);
		usbRegistrationStage.setScene(usbRegistrationWindow);
		usbRegistrationStage.setTitle("Adding a new USB drive");
		usbRegistrationStage.getIcons().add(new Image("gear-256.png"));
		
//Main Stage	 ---------------------------------------------------------------------------------------		
		
		editor = new TextEditor(primaryStage, encryptionDropDown, modeDropDown, paddingDropDown, hashFunctionDropDown, usbText);
		
		//Sets the general layout of the scene
		VBox layoutMainWindow = new VBox(menuBar,tabPane, editor);
        layoutMainWindow.setFillWidth(true);
		Scene mainWindow = new Scene(layoutMainWindow, 600, 400);
		


		
//Event Handler -----------------------------------------------------------------------------------------------
		
		registerUsbItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	usbRegistrationStage.show();
            }
        });
		
		usbRegistrationStage.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
            	System.out.println("Window is being shown");
            	editor.startUSBDetection();
            }
        });
		
		usbRegistrationStage.setOnHidden(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
            	System.out.println("Window is being Hidden");
            	editor.stopUSBDetection();
            }
        });
		
		menuNewItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	editor.newFileDialogue();
            }
        });
		
		menuOpenItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	editor.openFileDialogue();
            }
        });
		
		
		menuSaveItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	editor.saveFile();
            }
        });
		
		menuSaveAsItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	editor.saveFileAs();
            }
        });
		
		encryptionsItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	//Open options window
            	encryptionOptionStage.show();
            }
        });
		

		closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	encryptionOptionStage.close();
            }
        });
		
		
		operationDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	//Switch between several layout boxes
            	switch(operationDropDown.getValue())
            	{
            	//TODO
				case Asymmetric:
					encryptionGridPane.getChildren().clear();

					encryptionGridPane.add(operationLabel, 0, 0);
					encryptionGridPane.add(operationDropDown, 0, 1);
					encryptionGridPane.add(encryptionLabel, 0, 2);
					encryptionGridPane.add(encryptionDropDown, 0, 3);
					encryptionGridPane.add(keyLengthLabel, 1, 2);
					encryptionGridPane.add(keyLengthDropDown, 1, 3);
					
					encryptionDropDown.getItems().clear();
					encryptionDropDown.setValue(EncryptionType.RSA);
					encryptionDropDown.getItems().addAll(EncryptionType.getValuesByOperation(OperationMode.Asymmetric));
					
					operationDropDown.hide();
					encryptionDropDown.autosize();
					break;
				case Passwordbased:
					encryptionGridPane.getChildren().clear();
					
					encryptionGridPane.add(operationLabel, 0, 0);
					encryptionGridPane.add(operationDropDown, 0, 1);
					encryptionGridPane.add(encryptionLabel, 0, 2);
					encryptionGridPane.add(encryptionDropDown, 0, 3);
					encryptionGridPane.add(passwordLabel, 1, 2);
					encryptionGridPane.add(passwordArea, 1, 3);
					
					encryptionDropDown.getItems().clear();
					encryptionDropDown.setValue(EncryptionType.PBEWithMD5AndDES);
					encryptionDropDown.getItems().addAll(EncryptionType.getValuesByOperation(OperationMode.Passwordbased));
					operationDropDown.hide();
					encryptionDropDown.autosize();
					
					
					break;
				case Symmetric:
					encryptionGridPane.getChildren().clear();
					
					encryptionGridPane.add(operationLabel, 0, 0);
					encryptionGridPane.add(operationDropDown, 0, 1);
					encryptionGridPane.add(encryptionLabel, 0, 2);
					encryptionGridPane.add(encryptionDropDown, 0, 3);
					encryptionGridPane.add(modeLabel, 1, 2);
					encryptionGridPane.add(modeDropDown, 1, 3);
					encryptionGridPane.add(paddingLabel, 2, 2);
					encryptionGridPane.add(paddingDropDown, 2, 3);
					encryptionGridPane.add(hashFunctionLabel, 3, 2);
					encryptionGridPane.add(hashFunctionDropDown, 3, 3);
					
					encryptionDropDown.getItems().clear();
					encryptionDropDown.setValue(EncryptionType.none);
					encryptionDropDown.getItems().addAll(EncryptionType.getValuesByOperation(OperationMode.Symmetric));
					
				
					//Fixes a bug that caused the combobox to become untargetable
					operationDropDown.hide();
					encryptionDropDown.autosize();

					break;
				default:
					break;
            	
            	}
            }
        });
		
		//TODO doesent get called in the beginning
		keyLengthDropDown.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	/*
            	keyLengthDropDown.getItems().clear();
            	keyLengthDropDown.setValue(KeyLength.getFittingKeyLength(encryptionDropDown.getValue())[0]);
            	keyLengthDropDown.getItems().addAll(KeyLength.getFittingKeyLength(encryptionDropDown.getValue()));
            	*/
            }
        });
		
		primaryStage.getIcons().add(new Image("Lock.png"));
		primaryStage.setScene(mainWindow);
        primaryStage.show();

	}
	
}
