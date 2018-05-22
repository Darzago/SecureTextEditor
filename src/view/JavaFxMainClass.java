package view;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.PaddingType;
import javafx.application.*;
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
import javafx.stage.Stage;
import logic.TextEditor;

/**
 * Builds the scene tree and creates event handler
 * @author Joel
 *
 */
public class JavaFxMainClass extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//TODO Undecorated window
		
		Stage encryptionOptionStage = new Stage();
		
		TextEditor editor = new TextEditor(primaryStage, EncryptionType.none, EncryptionMode.ECB , PaddingType.NoPadding);
		
		TabPane tabPane = new TabPane();
		MenuBar menuBar = new MenuBar();
		
		//Event Handlers of all menu options within the file menu
		Menu fileMenu = new Menu("File");
			MenuItem menuNewItem = new MenuItem("New");
			menuNewItem.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	editor.newFileDialogue();
	            }
	        });
			
			MenuItem menuOpenItem = new MenuItem("Open");
			menuOpenItem.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	editor.openFileDialogue();
	            }
	        });
			
			MenuItem menuSaveItem = new MenuItem("Save");
			menuSaveItem.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	editor.saveFile();
	            }
	        });
			MenuItem menuSaveAsItem = new MenuItem("Save As");
			menuSaveAsItem.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	editor.saveFileAs();
	            }
	        });
		fileMenu.getItems().addAll(menuNewItem, menuOpenItem, menuSaveItem, menuSaveAsItem);
		
		//Menu for encryption options
		Menu encryptionMenu = new Menu("Encryption");
			MenuItem encryptions = new MenuItem("Options");
			encryptions.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	//Open options window
	            	encryptionOptionStage.show();
	            }
	        });
		encryptionMenu.getItems().addAll(encryptions);
		
		//Adds all menu items to the "file" menu
		menuBar.getMenus().addAll(fileMenu, encryptionMenu);
		
		//Sets up keyboard shortcuts
		menuNewItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		menuOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		menuSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		//Sets the general layout of the scene
		VBox layoutMainWindow = new VBox(menuBar,tabPane, editor);
        layoutMainWindow.setFillWidth(true);
		Scene mainWindow = new Scene(layoutMainWindow, 600, 400);
		
		//Label for the encryption dropdown
		Label encryptionLabel = new Label("Encryption:  ");
			ComboBox<EncryptionType> encryptionDropDown = new ComboBox<EncryptionType> ();
			
			//Event thet gets called if the value of the dropdown changes/is reassigned
			encryptionDropDown.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//TODO change other dropdown options depending on the encryption
	            	editor.setEncryptionType(encryptionDropDown.getValue());
	            }
	        });
			
			encryptionDropDown.setValue(EncryptionType.none);
		encryptionDropDown.getItems().addAll(EncryptionType.values());
		
		
		Label modeLabel = new Label("Mode:  ");
			ComboBox<EncryptionMode> modeDropDown = new ComboBox<EncryptionMode> ();
			//Event thet gets called if the value of the dropdown changes/is reassigned
			modeDropDown.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	editor.setEncryptionMode(modeDropDown.getValue());
	            }
	        });
			modeDropDown.setValue(EncryptionMode.ECB);
		modeDropDown.getItems().addAll(EncryptionMode.values());
		
		
		Label paddingLabel = new Label("Padding:  ");
			ComboBox<PaddingType> paddingDropDown = new ComboBox<PaddingType> ();
			paddingDropDown.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	editor.setPaddingType(paddingDropDown.getValue());
	            }
	        });
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
			closeButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	encryptionOptionStage.close();
	            }
	        });
			
		encryptionButtonsBox.setPadding(new Insets(10, 10, 10, 10));
		encryptionButtonsBox.setHgap(10);
		encryptionButtonsBox.setAlignment(Pos.CENTER_RIGHT);
		
		
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
