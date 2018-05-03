import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFxMainClass extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		TextEditor editor = new TextEditor(primaryStage);
		
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
		
		//Adds all menu items to the "file" menu
		menuBar.getMenus().addAll(fileMenu);
		
		//Sets up keyboard shortcuts
		menuNewItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		menuOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		menuSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		//Sets the general layout of the scene
		VBox layout = new VBox(menuBar,tabPane, editor);
        layout.setFillWidth(true);
		Scene scene = new Scene(layout, 600, 400);
		
		
		primaryStage.getIcons().add(new Image("Lock.png"));
		primaryStage.setScene(scene);
        primaryStage.show();
		
	}

}
