package main.java.zenit.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Class for testing the UI.
 * @author Pontus Laos, Oskar Molander
 *
 */
public class TestUI extends Application {
	@Override
	public void start(Stage stage) {
		new MainController(stage);
	}
	
	/**
	 * Exits the application by calling Platform.exit() as well as System.exit().
	 * @author Pontus Laos
	 */
	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}
	
	/**
	 * Runs the UI.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
