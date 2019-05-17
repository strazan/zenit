package main.java.zenit.settingspanel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.css.Stylesheet;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.java.zenit.ui.MainController;

public class CusomCSSThemeHandler {
	
	private String Color_PRIMARY = "E6994D";
	private static String Color_SECONDARY;
//	private Stylesheet customStylesheet; 
	private MainController mainC;
	private SettingsPanelController settingsC;
	//private String filename = "";
	
	public CusomCSSThemeHandler(MainController maincontroller, SettingsPanelController settingcontroller) {
		this.mainC = maincontroller;
		this.settingsC = settingcontroller;
				
	}
			
			
	public void setPrimaryColor(Color newPrimaryColor) {
		
		String newColor = colorToHex(newPrimaryColor);
		
		changeStyleSheet(mainC.getStage(), 
			"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/mainCustomTheme.css", 
			 Color_PRIMARY, newColor, "/zenit/settingspanel/customcss/mainCustomTheme.css");
		changeStyleSheet(settingsC.getStage(), 
			"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/settingspanelCustomTheme.css", 
			Color_PRIMARY, newColor, "/zenit/settingspanel/customcss/settingspanelCustomTheme.css");
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Color_PRIMARY = newColor;
	}
	
	public void changeStyleSheet(
		Stage stage, String customThemeCSSfilepath, String regex, String replacement, String localPath
	) {
		
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(customThemeCSSfilepath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> newLines = new ArrayList<String>();
		for(int i = 0; i < lines.size(); i++) {
			newLines.add(lines.get(i).replaceAll(regex, replacement));
		}
		
		File file = new File(customThemeCSSfilepath);
		file.delete();		
		File fileREAL = new File(customThemeCSSfilepath);
		try {
			fileREAL.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			FileWriter fw = new FileWriter(fileREAL,false);
			String stringNewStylesheet = "";
			for(int i = 0; i < newLines.size(); i++) {
				stringNewStylesheet += newLines.get(i) + "\n";
			}
			
			fw.write(stringNewStylesheet);
			fw.flush();
			fw.close();
			
			var stylesheets = stage.getScene().getStylesheets();
			var customSheet = getClass().getResource(localPath).toExternalForm();
			
			stylesheets.add(customSheet);
			
			
//			stylesheets.
			
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			
}
	private String colorToHex(Color color) {
	    String hex1;
	    String hex2;

	    hex1 = Integer.toHexString(color.hashCode()).toUpperCase();

	    switch (hex1.length()) {
	    case 2:
	        hex2 = "000000";
	        break;
	    case 3:
	        hex2 = String.format("00000%s", hex1.substring(0,1));
	        break;
	    case 4:
	        hex2 = String.format("0000%s", hex1.substring(0,2));
	        break;
	    case 5:
	        hex2 = String.format("000%s", hex1.substring(0,3));
	        break;
	    case 6:
	        hex2 = String.format("00%s", hex1.substring(0,4));
	        break;
	    case 7:
	        hex2 = String.format("0%s", hex1.substring(0,5));
	        break;
	    default:
	        hex2 = hex1.substring(0, 6);
	    }
	    return hex2;
	}

}
