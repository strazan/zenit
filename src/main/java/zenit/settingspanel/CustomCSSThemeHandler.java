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
import java.util.List;
import java.util.Properties;

import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * A handler for custom css theme.
 * @author siggelabor
 *
 */
public class CustomCSSThemeHandler {
	
	private String Color_Primary ;
	private String Color_PrimaryTint;
	private String Color_Secondary ; 
	private String Color_SecondaryTint ; 
	
	private boolean isCustomThemeToggled;

	private List<ThemeCustomizable> stages;
	
	private final String key_Primary = "primary";
	private final String key_PrimaryTint = "primtint";
	private final String key_Secondary = "secondary";
	private final String key_SecondaryTint = "secondtint";
	
	private InputStream input = null;
	private OutputStream output = null;

	/**
	 * Constructs a new CustomCSSThemeHandler
	 * @param customStyleSheets the List containing all ThemeCustomizable (controllers)
	 * of the application.
	 */
	public CustomCSSThemeHandler( List<ThemeCustomizable> stages) {
		this.stages = stages;
		loadAllProperties();
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
		
		if((!color.equals(Color_Primary)) && 
		   (!color.equals(Color_PrimaryTint)) &&
		   (!color.equals(Color_Secondary)) &&
		   (!color.equals(Color_SecondaryTint))
		) {
			return true;
		}
		return false;
	}
	
	/**
	 * Changes the themes of the application. This method will look for all occurrences of current
	 * the chosen colorTheme in the StyleSheet and change them to the new picked color.
	 * @param color new color to apply.
	 * @param colorTheme which 'group' to change.
	 */
	public void changeColor(Color color, CustomColor colorTheme) {
		
		String newColor;
		
		if(isColorValid(newColor = colorToHex(color))) {
			openStreams();
			
			for(int i = 0; i < stages.size(); i++) {
				changeStyleSheet(
					stages.get(i).getStage(), stages.get(i).getCustomThemeCSS(),
						getStringFromEnum(colorTheme), newColor
				);
			}	
			storeColor(newColor, colorTheme);
			closeStreams();
		}	
	}
	
	/**
	 * Uses CustomColor enum to get the right Color, in string format.
	 * 
	 * @param enumColor
	 * @return
	 */
	public String getStringFromEnum(CustomColor enumColor) {
		String toReturn = "";
		
		switch(enumColor) {
		case primaryColor:
			toReturn = Color_Primary;
			break;
			
		case primaryTint:
			toReturn = Color_PrimaryTint;
			break;
			
		case secondaryColor:
			toReturn = Color_Secondary;
			break;
		
		case secondaryTint:
			toReturn = Color_SecondaryTint;
			break;			
		}
		
		return toReturn;
	}
	
	/**
	 * Stores a color to the config.properties file.
	 * 
	 * @param color to be stored 
	 * @param colorTheme
	 */
	public void storeColor(String color, CustomColor colorTheme) {

		switch(colorTheme) {
		case primaryColor:
			Color_Primary = color;
			storeProperty(key_Primary, Color_Primary);
			break;
			
		case primaryTint:
			Color_PrimaryTint = color;
			storeProperty(key_PrimaryTint, Color_PrimaryTint);
			break;
			
		case secondaryColor:
			Color_Secondary = color;	
			storeProperty(key_Secondary, Color_Secondary);
			break;
		
		case secondaryTint:
			Color_SecondaryTint = color;
			storeProperty(key_SecondaryTint, Color_SecondaryTint);
			break;			
		}
	}
	
	/**
	 * Loads all saved properties stored in the config.properties file, and saves them to the
	 * matching instance variable.
	 */
	public void loadAllProperties() {
		openStreams();
		Color_Primary = getProperty(key_Primary);
		closeStreams();
		openStreams();
		Color_PrimaryTint = getProperty(key_PrimaryTint);
		closeStreams();
		openStreams();
		Color_Secondary = getProperty(key_Secondary);
		closeStreams();
		openStreams();
		Color_SecondaryTint = getProperty(key_SecondaryTint);
		closeStreams();
	}
	
	/**
	 * Gets the stored property of given key.
	 * @param key to the property
	 * @return value
	 */
	public String getProperty(String key) {
		
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
			openStreams();
			prop.setProperty(key, value);
			prop.store(output, null);
			closeStreams();
			
		} catch (IOException io) {
			io.printStackTrace();
		}
			
	}
	
	/*
	 * TODO CHANGE THIS SO ITS MORE SAFE LOL
	 */
	
	/**
	 * 
	 * 
	 * @param stage the stage to where the stylesheets are suppose to be added.
	 * @param customThemeCSSfilepath full path
	 * @param regex 'color' to be changed.
	 * @param replacement the ner color
	 * @param localPath the 'workpace dir'
	 */
	public void changeStyleSheet(
		Stage stage, File customThemeCSSfile, String regex, String replacement
	) {
		String fullPath =  System.getProperty("user.dir") + customThemeCSSfile;
		var stylesheets = stage.getScene().getStylesheets();

		if(stylesheets.contains("file:" + fullPath)) {
			stylesheets.remove("file:" + fullPath);
		}
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(fullPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> newLines = new ArrayList<String>();
	
		for(int i = 0; i < lines.size(); i++) {
			String repalcedLine = lines.get(i).replaceAll(regex, replacement);
			newLines.add(repalcedLine);	
		}		

		try {
			String stringNewStylesheet = "";
			for(int i = 0; i < newLines.size(); i++) {
				stringNewStylesheet += newLines.get(i) + "\n";
			}

		    BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
		    writer.write(stringNewStylesheet);
		    writer.close();
			
		    if(isCustomThemeToggled) {
		    	stylesheets = stage.getScene().getStylesheets();
				stylesheets.add("file:" +fullPath);
		    }
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}		
	}
	
	/**
	 * Adds or removes custom theme stylesheets.
	 * 
	 * @param isToggled 
	 */
	public void toggleCustomTheme(boolean isToggled) {	
		if(isToggled) {		
			updateDefaultStylesheets(isToggled);
			for(int i = 0; i < stages.size(); i++) {
				String fullPath = "file:" + System.getProperty("user.dir") + 
					stages.get(i).getCustomThemeCSS();	
				var stylesheets = stages.get(i).getStage().getScene().getStylesheets();
				stylesheets.add(fullPath);
			}
		}
		else {
			for(int i = 0; i < stages.size(); i++) {
				String fullPath = "file:" + System.getProperty("user.dir") + 
					stages.get(i).getCustomThemeCSS();	
				var stylesheets = stages.get(i).getStage().getScene().getStylesheets();
				if(stylesheets.contains(fullPath)) {
					stylesheets.remove(fullPath);
				}
			}
			updateDefaultStylesheets(isToggled);
		}	
		isCustomThemeToggled = isToggled;
	}
	
	/**
	 * Updates all current default stylesheets.
	 */
	public void updateDefaultStylesheets(boolean isCustomTheme) {
			
			for (int i = 0; i < stages.size(); i++) {
				var stylesheets = stages.get(i).getStage().getScene().getStylesheets();
				var activeSheet = stages.get(i).getActiveStylesheet();
				
				if(isCustomTheme) {
					stylesheets.remove(activeSheet);
				}
				else {
					stylesheets.add(activeSheet);
				}
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
