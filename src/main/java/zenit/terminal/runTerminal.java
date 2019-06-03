package main.java.zenit.terminal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class runTerminal extends Application {

	@Override
	public void start(Stage stage) {
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/terminal/Terminal.fxml"));
			
			
			TerminalController controller = new TerminalController();
			loader.setController(controller);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Zenit");
	
			stage.show();
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
