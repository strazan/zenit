package main.java.zenit.terminal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class TerminalController extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Terminal.fxml"));
		
		Parent root = loader.load();
		loader.setController(this);
		loader.load();

		Scene scene = new Scene(root);
//		scene.getStylesheets().add(getClass().getResource("/zenit/ui/mainStyle.css").toString());
//		scene.getStylesheets().add(getClass().getResource("/zenit/ui/keywords.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Zenit");

		initialize();
		stage.show();
		
	}
	
	public void initialize() {
		
		addTerminalTab();
	}
	
	private Tab addTerminalTab() {
		
		
//		TerminalBuilder builder = new TerminalBuilder();
//		TerminalTab terminalTab = new TerminalTab();
		
		return null;
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
