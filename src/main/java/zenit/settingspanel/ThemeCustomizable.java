package main.java.zenit.settingspanel;

import java.io.File;

import javafx.stage.Stage;

public interface ThemeCustomizable {
	
	public File getCustomThemeCSS();
	
	public Stage getStage();
	
	public String getActiveStylesheet();

}
