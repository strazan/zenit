package zenit.ui;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import zenit.filesystem.FileController;
import zenit.filesystem.WorkspaceHandler;

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
		File workspace = WorkspaceHandler.readWorkspace();
		
		if (workspace == null) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Choose workspace");
			workspace = directoryChooser.showDialog(stage);
			WorkspaceHandler.createWorkspace(workspace);
		}
		
		FileController fileController = new FileController(workspace);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		MainController controller = new MainController();
		controller.setFileController(fileController);
	
		
		loader.setController(controller);
		
		Scene scene = new Scene(loader.load());
		controller.initialize(stage);
		stage.setScene(scene);
		stage.setTitle("Zenit");
		
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
