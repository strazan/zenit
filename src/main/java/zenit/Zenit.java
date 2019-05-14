package main.java.zenit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.java.zenit.launchers.MacOSLauncher;
import main.java.zenit.ui.MainController;

/**
 * Used to launch the application
 * @author Alexander Libot
 *
 */
public class Zenit extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		String OS = System.getProperty("os.name");
		if (OS.equals("Mac OS X")) {
			new MacOSLauncher(stage);
		} else {
			new MainController(stage);
		}
	}
	
	@Override
	public void stop() {
		Platform.exit();
		System.exit(0);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
