package zenit.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		MainController controller = new MainController();
		
		loader.setController(controller);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Zenit");
		
		controller.initialize(stage);
		stage.show();
		
		KeyboardShortcuts.setupMain(scene, controller);
	}
	
	/**
	 * Runs the UI.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
