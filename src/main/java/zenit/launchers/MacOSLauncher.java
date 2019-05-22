package main.java.zenit.launchers;

import javafx.stage.Stage;
import main.java.zenit.ui.MainController;

public class MacOSLauncher {
	
	public MacOSLauncher(Stage stage) {
//		System.out.println("Mac OS");
		
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WikiTeX");

		new MainController(stage);
		
	}

}
