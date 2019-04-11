package main.java.zenit.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.resources.zenit.ui.Foo;

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
		
		/*
		 * TODO Test if you like this idea. Saves and opens a local File-instance of your 
		 * selected workspace. Only prompts when unset and can be changed from within gui
		 * Alex
		 */
//		File workspace = WorkspaceHandler.readWorkspace();
		
//		FileController fileController = new FileController(workspace);
		System.out.println(getClass().getResource("/zenit/ui/Main.fxml"));
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/ui/Main.fxml"));
		System.out.print(System.getProperty("user.dir"));
		MainController controller = new MainController();
//		controller.setFileController(fileController);
		
		loader.setController(controller);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/css/mainStyle.css").toString());

		scene.getStylesheets().add(getClass().getResource("/css/keywords.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Zenit");
		
		controller.initialize(stage);
		stage.show();
		KeyboardShortcuts.setupMain(scene, controller);
	}
	
	/**
	 * Calls Platform.exit() as well as System.exit().
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
