package view;

import enums.EncryptionMode;
import enums.EncryptionType;
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
import javafx.scene.control.TabPane;
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
			MenuItem configItem = new MenuItem("Config");
			MenuItem registerUsbItem = new MenuItem("Register USB drive");
		encryptionMenu.getItems().addAll(encryptionsItem, configItem, registerUsbItem);
		
		//Adds all menu items to the "file" menu
		menuBar.getMenus().addAll(fileMenu, encryptionMenu);
		
		//Sets up keyboard shortcuts
		menuNewItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		menuOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		menuSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		//Label for the encryption dropdown
		Label encryptionLabel = new Label("Encryption:  ");
			ComboBox<EncryptionType> encryptionDropDown = new ComboBox<EncryptionType> ();
			encryptionDropDown.setValue(EncryptionType.none);
		encryptionDropDown.getItems().addAll(EncryptionType.values());
		
		
		Label modeLabel = new Label("Mode:  ");
			ComboBox<EncryptionMode> modeDropDown = new ComboBox<EncryptionMode> ();
			modeDropDown.setValue(EncryptionMode.ECB);
		modeDropDown.getItems().addAll(EncryptionMode.values());
		
		
		Label paddingLabel = new Label("Padding:  ");
			ComboBox<PaddingType> paddingDropDown = new ComboBox<PaddingType> ();
			paddingDropDown.getItems().addAll(PaddingType.values());
		paddingDropDown.setValue(PaddingType.NoPadding);
		
		//Adds all dropdown menus and labels to a GridPane
		GridPane encryptionGridPane = new GridPane();
		encryptionGridPane.add(encryptionLabel, 0, 0);
		encryptionGridPane.add(encryptionDropDown, 0, 1);
		encryptionGridPane.add(modeLabel, 1, 0);
		encryptionGridPane.add(modeDropDown, 1, 1);
		encryptionGridPane.add(paddingLabel, 2, 0);
		encryptionGridPane.add(paddingDropDown, 2, 1);
		
		encryptionGridPane.setHgap(10);
		encryptionGridPane.setAlignment(Pos.CENTER);
		encryptionGridPane.setPadding(new Insets(10, 0, 0, 0));
		
		//Adds button(s) below the dropdown menus 
		GridPane encryptionButtonsBox = new GridPane();
			Button closeButton = new Button("Close");
			encryptionButtonsBox.add(closeButton, 1, 0);
		encryptionButtonsBox.setPadding(new Insets(10, 10, 10, 10));
		encryptionButtonsBox.setHgap(10);
		encryptionButtonsBox.setAlignment(Pos.CENTER_RIGHT);
		
		editor = new TextEditor(primaryStage, encryptionDropDown, modeDropDown, paddingDropDown);
		
		//Sets the general layout of the scene
		VBox layoutMainWindow = new VBox(menuBar,tabPane, editor);
        layoutMainWindow.setFillWidth(true);
		Scene mainWindow = new Scene(layoutMainWindow, 600, 400);

//Config Stage ---------------------------------------------------------------------
		Stage configStage = new Stage();
		
		GridPane configLayout = new GridPane();
		configLayout.setAlignment(Pos.CENTER);
		configLayout.setHgap(15);
		configLayout.setVgap(3);
		configLayout.setGridLinesVisible(true);
		
		Scene configWindow = new Scene(configLayout, 450, 180);
		configStage.setScene(configWindow);
		configStage.setTitle("Encryption Config");
		configStage.getIcons().add(new Image("gear-256.png"));
		
		
//USB Registration Stage ---------------------------------------------------------------------
		Stage usbRegistrationStage = new Stage();
		
		GridPane usbRegistrationLayout = new GridPane();
		usbRegistrationLayout.add(new Text("Plug in the usb stick to register"), 0, 0);
		usbRegistrationLayout.setAlignment(Pos.CENTER);
		usbRegistrationLayout.setHgap(15);
		usbRegistrationLayout.setVgap(3);

		Scene usbRegistrationWindow = new Scene(usbRegistrationLayout, 450, 180);
		usbRegistrationStage.setScene(usbRegistrationWindow);
		usbRegistrationStage.setTitle("Adding a new USB drive");
		usbRegistrationStage.getIcons().add(new Image("gear-256.png"));
		
		
				
//Event Handler -----------------------------------------------------------------------------------------------
		
		registerUsbItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	usbRegistrationStage.show();
            }
        });
		
		usbRegistrationStage.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
            	System.out.println("Window is being shown");
            }
        });
		
		usbRegistrationStage.setOnHidden(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
            	System.out.println("Window is being Hidden");
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
		
		configItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	configLayout.getChildren().clear();
            	editor.writeListInGrid(configLayout);
            	configStage.show();
            }
        });

		closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	encryptionOptionStage.close();
            }
        });
		
		VBox optionGeneralLayout = new VBox(encryptionGridPane, encryptionButtonsBox);
		
		Scene encryptionOptionWindow = new Scene(optionGeneralLayout, 370, 130);
		encryptionOptionStage.setScene(encryptionOptionWindow);
		encryptionOptionStage.setTitle("Encryption Options");
		encryptionOptionStage.getIcons().add(new Image("gear-256.png"));
		
		
		primaryStage.getIcons().add(new Image("Lock.png"));
		primaryStage.setScene(mainWindow);
        primaryStage.show();
		
	}
	
}
