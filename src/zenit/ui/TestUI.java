package zenit.ui;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zenit.filesystem.FileController;
import zenit.filesystem.WorkspaceHandler;

import zenit.textFlow.ZenCodeArea;
import zenit.ui.MainController.Search;

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
		
		FileController fileController = new FileController(workspace);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		MainController controller = new MainController();
		Search search = controller.new Search();
		controller.setFileController(fileController);
	
		
		loader.setController(controller);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("mainStyle.css").toString());

		scene.getStylesheets().add(ZenCodeArea.class.getResource("keywords.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Zenit");
		
		controller.initialize(stage);
		stage.show();
		
		KeyboardShortcuts.setupMain(scene, controller, search);
	}
	
	/**
	 * Runs the UI.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
