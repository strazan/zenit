package main.java.zenit.settingspanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javafx.scene.paint.Color;
import javafx.stage.Stage;

import main.java.zenit.ui.MainController;

/**
 * A handler for custom css theme.
 * @author siggelabor
 *
 */
public class CustomCSSThemeHandler {
	
	private String Color_PRIMARY ;
	private String Color_PRIMARYTINT;
	private String Color_SECONDARY ; 
	private String Color_SECONDARYTINT ; 

	private MainController mainC;
	private SettingsPanelController settingsC;
	
	private HashMap<Stage, String> stageMap;
	
	private final String key_Primary = "primary";
	private final String key_PrimaryTint = "primtint";
	private final String key_Secondary = "secondary";
	private final String key_SecondaryTint = "secondtint";
	
	private InputStream input = null;
	private OutputStream output = null;

	public CustomCSSThemeHandler(HashMap<Stage, String> stageMap) {
		this.stageMap = stageMap;
	}
	public CustomCSSThemeHandler(MainController maincontroller, SettingsPanelController settingcontroller) {
		this.mainC = maincontroller;
		this.settingsC = settingcontroller;
		openStreams();
		Color_PRIMARY = loadProperty(key_Primary);
		Color_PRIMARYTINT = loadProperty(key_PrimaryTint);
		Color_SECONDARY = loadProperty(key_Secondary);
		Color_SECONDARYTINT = loadProperty(key_SecondaryTint);
	}			
		
	/**
	 * Creates new I/O stream to the config.properties file.
	 */
	public void openStreams() {
		try {
			input = new FileInputStream("config.properties");
			output = new FileOutputStream("config.properties",true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes I/O stream for the config.properties file.
	 */
	public void closeStreams() {
		
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	/**
	 * Checks if the color already exists in the application.
	 * @param color
	 * @return
	 */
	private boolean isColorValid(String color) {
		
		if((!color.equals(Color_PRIMARY)) && 
		   (!color.equals(Color_PRIMARYTINT)) &&
		   (!color.equals(Color_SECONDARY)) &&
		   (!color.equals(Color_SECONDARYTINT))
		) {
			return true;
		}
		return false;
	}
	
	public void changeColor(Color color) {
		String newColor;
		
		if(isColorValid(newColor = colorToHex(color))) {
			openStreams();
		}
		
	}
	
	/**
	 * Sets the primary color
	 * @param newPrimaryColor
	 */
	public void setPrimaryColor(Color newPrimaryColor) {
		String newColor; 
		
		if(isColorValid(newColor = colorToHex(newPrimaryColor))) {
			openStreams();
			
			Color_PRIMARY = loadProperty(key_Primary);
			System.out.println(new File(
					"/customtheme/mainCustomTheme.css").getPath());
			
			changeStyleSheet(mainC.getStage(), new File(
					"/customtheme/mainCustomTheme.css").getPath(),  
				Color_PRIMARY, newColor);
			changeStyleSheet(settingsC.getStage(), new File(
				"/customtheme/settingspanelCustomTheme.css").getPath(), 
				Color_PRIMARY, newColor);
			
			Color_PRIMARY = newColor;
			storeProperty(key_Primary, Color_PRIMARY);
			closeStreams();
		}
	}
	
	public void setSecondaryColor(Color newSecondaryColor) {
		String newColor;
		
		if(isColorValid(newColor = colorToHex(newSecondaryColor))) {

			openStreams();
			Color_SECONDARY = loadProperty(key_Secondary);
			
			changeStyleSheet(mainC.getStage(), 
				"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/mainCustomTheme.css", 
				Color_SECONDARY, newColor);
			changeStyleSheet(settingsC.getStage(), 
				"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/settingspanelCustomTheme.css", 
				Color_SECONDARY, newColor);
			
			Color_SECONDARY = newColor;
		
			storeProperty(key_Secondary, Color_SECONDARY);
			closeStreams();
		}
	}
	 
	public void setPrimaryTint(Color newPriamryTint) {
		String newColor;
		
		if(isColorValid(newColor = colorToHex(newPriamryTint))) {

			openStreams();
			Color_PRIMARYTINT = loadProperty(key_PrimaryTint);
			
			changeStyleSheet(mainC.getStage(), 
				"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/mainCustomTheme.css", 
				Color_PRIMARYTINT, newColor);
			changeStyleSheet(settingsC.getStage(), 
				"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/settingspanelCustomTheme.css", 
				Color_PRIMARYTINT, newColor);
			
			Color_PRIMARYTINT = newColor;
		
			storeProperty(key_PrimaryTint, Color_PRIMARYTINT);
			closeStreams();
		}
	}
	
	public void setSecondaryTint(Color newSecondaryTint) {
		String newColor;
		
		if(isColorValid(newColor = colorToHex(newSecondaryTint))) {

			openStreams();
			Color_SECONDARYTINT = loadProperty(key_SecondaryTint);
			
			changeStyleSheet(mainC.getStage(), 
				"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/mainCustomTheme.css", 
				Color_SECONDARYTINT, newColor);
			changeStyleSheet(settingsC.getStage(), 
				"/Users/siggelabor/Documents/zenit/src/main/resources/zenit/settingspanel/customcss/settingspanelCustomTheme.css", 
				Color_SECONDARYTINT, newColor);
			
			Color_SECONDARYTINT = newColor;
		
			storeProperty(key_SecondaryTint, Color_SECONDARYTINT);
			closeStreams();
		}
	}
	/**
	 * Gets the stored property of given key.
	 * @param key to the property
	 * @return value
	 */
	public String loadProperty(String key) {
		
		Properties prop = new Properties();

		String property = "";
		
		try {
			
			prop.load(input);
			property = prop.getProperty(key);

		} catch (IOException ex) {
			ex.printStackTrace();
		
		}
		return property;
	}
	
	/**
	 * Stores a property
	 * @param key
	 * @param value
	 */
	public void storeProperty(String key, String value) {
		Properties prop = new Properties();
		
		try {
			
			prop.setProperty(key, value);
			prop.store(output, null);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
			
	}
	
	/*
	 * TODO CHANGE THIS SO ITS MORE SAFE LOL
	 */
	
	/**
	 * 
	 * @param stage the stage to where the stylesheets are suppose to be added.
	 * @param customThemeCSSfilepath full path
	 * @param regex 'color' to be changed.
	 * @param replacement the ner color
	 * @param localPath the 'workpace dir'
	 */
	public void changeStyleSheet(
		Stage stage, String customThemeCSSfilepath, String regex, String replacement
	) {
		var stylesheets = stage.getScene().getStylesheets();
//		if(stylesheets.contains("file:" + customThemeCSSfilepath)) {
//			stylesheets.remove("file:" + customThemeCSSfilepath);
//
//		}
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(customThemeCSSfilepath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> newLines = new ArrayList<String>();
	System.out.println(regex);
		for(int i = 0; i < lines.size(); i++) {
			String repalcedLine = lines.get(i).replaceAll(regex, replacement);
			newLines.add(repalcedLine);	
		}
		

		try {
			String stringNewStylesheet = "";
			for(int i = 0; i < newLines.size(); i++) {
				stringNewStylesheet += newLines.get(i) + "\n";
			}

		    BufferedWriter writer = new BufferedWriter(new FileWriter(customThemeCSSfilepath));
		    writer.write(stringNewStylesheet);

		    writer.close();
			
		    stylesheets = stage.getScene().getStylesheets();
	
			stylesheets.add("file:" + customThemeCSSfilepath);
		
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}		
	}
	
	/**
	 * Converts a JavaFX Color to hex-format.
	 * @param color to be converted.
	 * @return hex2 the color in hex-format
	 */
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
	    return "#" + hex2;
	}

}
