package zenit.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * Class for testing the UI.
 * @author Pontus Laos, Oskar Molander
 *
 */
public class TestUI extends Application {
	/**
	 * Loads a file Main.fxml, sets a MainController as its Controller, and loads it.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		MainController controller = new MainController();
	
		controller.initialize(stage);
		loader.setController(controller);
		
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Zenit");
		
		stage.show();
		
		KeyboardShortcuts.add(scene, KeyCode.S, KeyCombination.CONTROL_DOWN, () -> {
			controller.saveFile(null);
		});
		
		KeyboardShortcuts.add(scene, KeyCode.O, KeyCombination.CONTROL_DOWN, () -> {
			controller.openFile(true);
		});
	}
	
	/**
	 * Runs the UI.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
