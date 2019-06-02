package main.java.zenit.settingspanel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.ToggleSwitch;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.java.zenit.console.ConsoleController;
import main.java.zenit.ui.MainController;
import main.java.zenit.zencodearea.ZenCodeArea;

/**
 * Controller class for the NewTextSize window,
 * @author Sigge Labor
 *
 */
public class SettingsPanelController extends AnchorPane implements ThemeCustomizable{

	private int oldSize;
	private String oldFont;
	private File customThemeCSS;
	private LinkedList<String> addedCSSLines;
	private List<ThemeCustomizable> stages;
	
	private Stage window;
	private MainController mainController;
	private CustomCSSThemeHandler themeHandler;
	
	private boolean isCustomTheme = false;
	private boolean isDarkMode = true;

	private String settingsPanelDarkMode = getClass().getResource(
		"/zenit/settingspanel/settingspanelDarkMode.css").toExternalForm();
	private String settingsPanelLightMode = getClass().getResource(
		"/zenit/settingspanel/settingspanelLightMode.css").toExternalForm();
	
	private enum OS {	
		MACOS, WINDOWS, LINUX
	}
	private OS operatingSystem;

	@FXML
	private TextField fldNewSize;

	@FXML
	private Slider sldrNewSize;
	
	@FXML
	private Label lblOldTextSize;
	
	@FXML
	private Label lblOldFont;
	
	@FXML
	private Label lblCurrentJavaHome;
	
	@FXML
	private Label newJavaHome;
	
	@FXML
	private Label lblTxtAppeaSize;
	
	@FXML
	private ChoiceBox<String> chcbxNewFont;
	
	@FXML
	private Button btnTextAppearance;
	
	@FXML
	private Button btnJavaHome;
	
	@FXML
	private Button btnSupport;
	
	@FXML
	private Button btnTheme;
	
	@FXML
	private Button btnCustomCSS;
	
	@FXML
	private Button btnCustomTheme;
	
	@FXML
	private Hyperlink linkOpenInGitHub;
	
	@FXML
	private Hyperlink linkSubmitIssue;
	
	@FXML
	private Hyperlink linkDownloadSource;
	
	@FXML
	private ToggleSwitch toggleDarkMode;
	
	@FXML
	private ToggleSwitch toggleSwitchCustomTheme;
	
	@FXML
	private ListView listViewAddedCSS;
	
	@FXML
	private ColorPicker colorPickerPrimaryColor;
	
	@FXML
	private ColorPicker colorPickerPrimaryTint;
	
	@FXML
	private ColorPicker colorPickerSecondaryColor;
	
	@FXML
	private ColorPicker colorPickerSecondaryTint;
	
	@FXML
	private AnchorPane pnlTextAppearance;
	
	@FXML
	private AnchorPane pnlJavaHome;
	
	@FXML
	private AnchorPane pnlSupport;
	
	@FXML
	private AnchorPane pnlTheme;
	
	@FXML
	private AnchorPane pnlCustomCSS;
	
	@FXML
	private AnchorPane pnlCustomTheme;
	
	private ConsoleController consoleController;
	
	

	/**
	 * constructs a controller for the TextSizeWindow. 
	 * @param codeArea the ZenCodeArea that will be modified.
	 */
	
	public SettingsPanelController(MainController mainController, int oldFontSize, String oldFontFamily, ConsoleController consoleController) {
		this.mainController = mainController;
		this.consoleController = consoleController;
		oldSize = oldFontSize;
		oldFont = oldFontFamily;
		addedCSSLines = new LinkedList<String>();
		
		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/zenit/settingspanel/SettingsPanel.fxml"
		));
		
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		window = new Stage();
		Scene scene = new Scene(this);
		
		window.setScene(scene);
		window.setTitle("Preferences");
		initialize();
//		scene.getStylesheets().add(getClass().getResource(
//			"/zenit/settingspanel/settingspanelDarkMode.css").toString(
//		));
		
		darkModeChanged(mainController.isDarkmode());

		window.show();
		
		this.customThemeCSS = new File("/customtheme/settingspanelCustomTheme.css");
		
		stages = new  ArrayList<ThemeCustomizable>();
		stages.add(mainController);
		stages.add(this);
	
		themeHandler = new CustomCSSThemeHandler(stages);
	}
	
	/**
	 * Sets the font of the given ZenCodeArea.
	 * @param newFont the font to be applied.
	 */
	public void setNewFont(String newFont) {
		chcbxNewFont.setValue(newFont);
		mainController.setFontFamily(newFont);
	}
	
	/**
	 * Sets the font size of the given ZenCodeArea.
	 * @param newFontSize the font size to be applied.
	 */
	public void setNewFontSize(long newFontSize) {
		long size = newFontSize;
		fldNewSize.textProperty().setValue(String.valueOf(size));
		if(size > 100) {
			size = 100;
		}
		else if(size < 6) {
			size = 6;
		}
		sldrNewSize.setValue(size);
		mainController.setFontSize((int)size);//this.codeArea.setFontSize((int)size);
	}
	
	/**
	 * Moves a panel to the front, and thereby makes it visible.
	 * @param e
	 */
	public void panelToFront(Event e) {
		if(e.getSource() == btnTextAppearance) {
			pnlTextAppearance.toFront();
		}
		else if(e.getSource() == btnJavaHome) {
			pnlJavaHome.toFront();
		}
		else if(e.getSource() == btnSupport) {
			pnlSupport.toFront();
		}
		else if(e.getSource() == btnTheme) {
			pnlTheme.toFront();
		}
		else if(e.getSource() == btnCustomCSS) {
			pnlCustomCSS.toFront();
		}
		else if(e.getSource() == btnCustomTheme) {
			pnlCustomTheme.toFront();
		}
	}
	
	@FXML
	private void setNewJavaHome() {
		/*
		 * TODO REMOVE
		 */
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(window);

		if(selectedDirectory == null){
		     //No Directory selected
		}
		else{
			 ProcessBuilder pb = new ProcessBuilder();
			    Map<String, String> env = pb.environment();
			    env.put("JAVA_HOME", selectedDirectory.getAbsolutePath());
			    try {
					Process p = pb.start();
					Thread.sleep(100);
					newJavaHome.setText(System.getenv("JAVA_HOME"));
					newJavaHome.setStyle("-fx-text-fill: #0B6623;");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	// TODO update the comments below im tired.
	
	/**
	 * Adds the string written in fldCSSLineInput to the setStyle method. till will add the styling
	 * to the application. 
	 */
	@FXML
	private void addCSSLine() {
		
//		String CSSLine = fldCSSLineInput.getText();
//		try {
//			Scene mockScene = new Scene(new Region());
//			mockScene.getRoot().setStyle(CSSLine);
//			
//			String allCusomLinesOfCSS = "";
//			addedCSSLines.addFirst(CSSLine);
//			
//			for(int i = 0; i < addedCSSLines.size(); i++) {
//				allCusomLinesOfCSS += addedCSSLines.get(i);
//			}
//			this.window.getScene().getRoot().setStyle(allCusomLinesOfCSS);
//			
//			updateCustomCSSListView();
//		}
//		
//		catch(Exception e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Updates the listViewAddedCSS to show the correct lines. 
	 */
	@SuppressWarnings("unchecked")
	private void updateCustomCSSListView() {
		listViewAddedCSS.getItems().clear();
		
		for(int i = 0; i < addedCSSLines.size(); i++) {
			listViewAddedCSS.getItems().add(new CustomCSSListItem(addedCSSLines.get(i)));
		}
	}
	
	/**
	 * Calls the openInBrowser method. The URL depends on which button that is clicked.
	 * @param e
	 */
	@FXML
	private void openLinkInBrowserEvent(Event e) {	
		
		if(e.getSource() == linkOpenInGitHub) {
			openInBrowser("https://github.com/strazan/zenit");
		}
		if(e.getSource() == linkSubmitIssue) {
			openInBrowser("https://github.com/strazan/zenit/issues/new");
		}
		if(e.getSource() == linkDownloadSource) {
			openInBrowser("https://github.com/strazan/zenit/archive/develop.zip");
		}	
	}
	
	/**
	 * Opens an URL an the computers default browser. The command varies depending on the users
	 * operating system.
	 * @param url to open
	 */
	private void openInBrowser(String url) {
		Runtime rt = Runtime.getRuntime();
		
		switch(operatingSystem) {
			case LINUX:
			try {
				rt.exec("xdg-open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
				
			case MACOS:
			try {
				rt.exec("open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
				
			case WINDOWS:
			try {
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
			default:
				System.err.println("OS not found. Open link manually in browser:\n" + url );
				break;
		}
	}
	
	/**
	 * Switches between dark- and light mode depending on what is selected in the setting pane's
	 * toggle switch.
	 * @param event
	 * @author Pontus Laos, Sigge Labor
	 */
	private void darkModeChanged(boolean isDarkMode) {
		if(!isCustomTheme) {
			var stylesheets = this.mainController.getStage().getScene().getStylesheets();
			var settingsPanelStylesheets = window.getScene().getStylesheets();
			
			var lightMode = getClass().getResource("/zenit/ui/mainStyle-lm.css").toExternalForm();
			var darkMode = getClass().getResource("/zenit/ui/mainStyle.css").toExternalForm();
			var darkModeKeywords = ZenCodeArea.class.getResource("/zenit/ui/keywords.css").toExternalForm();
			var lightModeKeywords = ZenCodeArea.class.getResource("/zenit/ui/keywords-lm.css").toExternalForm();
			var darkModeConsole = getClass().getResource("/zenit/console/consoleStyle.css").toExternalForm();
			var lightModeConsole = getClass().getResource("/zenit/console/consoleStyleLight.css").toExternalForm();
			
		
			if (isDarkMode) {

				settingsPanelStylesheets.clear();
				settingsPanelStylesheets.add(settingsPanelDarkMode);
				consoleController.getStylesheets().remove(lightModeConsole);
				consoleController.getStylesheets().add(darkModeConsole);
				consoleController.changeAllConsoleAreaColors("-fx-background-color:#444");

				stylesheets.clear();
				stylesheets.add(darkModeKeywords);
				stylesheets.add(darkMode);

			} else {
				settingsPanelStylesheets.clear();
				settingsPanelStylesheets.add(settingsPanelLightMode);
				consoleController.getStylesheets().remove(darkModeConsole);
				consoleController.getStylesheets().add(lightModeConsole);
				consoleController.changeAllConsoleAreaColors("-fx-background-color:#989898");


				stylesheets.clear();
				stylesheets.add(lightModeKeywords);
				stylesheets.add(lightMode);
			}
		}
		this.isDarkMode = isDarkMode;
		mainController.setDarkmode(this.isDarkMode);
	}

	/**
	 * initializing steps. Variables will get their value. ActionListeners added.
	 */
	private void initialize() {
		lblOldTextSize.setText(String.valueOf(oldSize));
		fldNewSize.setText(String.valueOf(oldSize));
		sldrNewSize.setValue(oldSize);
		
		sldrNewSize.valueProperty().addListener(
			(ChangeListener<? super Number>) (arg0, arg1, arg2) -> {
				setNewFontSize(Math.round(sldrNewSize.getValue()));	
		});
		
		fldNewSize.textProperty().addListener((arg0, arg1, arg2) -> {
			try {  
				setNewFontSize(Long.parseLong(fldNewSize.getText()));  
			  } catch(NumberFormatException e){  
			e.printStackTrace();	 
			  }  
		});
		
		List<String> fonts = Font.getFamilies();
		
		for(int i = 0; i < fonts.size(); i++) {
			chcbxNewFont.getItems().add(fonts.get(i));
		}
		chcbxNewFont.setValue(oldFont);
		lblOldFont.setText(oldFont);
		chcbxNewFont.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
			setNewFont(arg2);
		});
		
		lblCurrentJavaHome.setText(System.getenv("JAVA_HOME"));
		
		fldNewSize.setAlignment(Pos.CENTER_RIGHT);
		
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win") >= 0) {
			operatingSystem = OS.WINDOWS;
		}	
		else if(os.indexOf("mac") >= 0) {
			operatingSystem = OS.MACOS;
		}
		else if(os.indexOf("nix") >=0 || os.indexOf("nux") >=0) {
			operatingSystem = OS.LINUX;
		}
		
		toggleDarkMode.setSelected(mainController.isDarkmode());
		toggleDarkMode.selectedProperty().addListener(new ChangeListener <Boolean> () {
            @Override
			public void changed(
				ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
				darkModeChanged(newValue);
			}
        });
		
		toggleSwitchCustomTheme.selectedProperty().addListener(new ChangeListener <Boolean> () {
            @Override
			public void changed(
				ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
				themeHandler.toggleCustomTheme(toggleSwitchCustomTheme.isSelected());
				isCustomTheme = toggleSwitchCustomTheme.isSelected();
				darkModeChanged(toggleDarkMode.isSelected());
			}
        });
		
		listViewAddedCSS.getItems().add(new AnchorPane());

		colorPickerPrimaryColor.setOnAction((event) -> {
		     Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerPrimaryColor.getValue(),
		    		CustomColor.primaryColor);
			     });
		});	
		
		colorPickerPrimaryTint.setOnAction((event) -> {
	    	 Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerPrimaryTint.getValue(),
		    		CustomColor.primaryTint);
		     });
		});	
		
		colorPickerSecondaryColor.setOnAction((event) -> {
	    	 Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerSecondaryColor.getValue(),
				    CustomColor.secondaryColor);
		     });
		});	
		
		colorPickerSecondaryTint.setOnAction((event) -> {
	    	 Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerSecondaryTint.getValue(),
				    CustomColor.secondaryTint);
		     });
		});	
	}
	
	/**
	 * @return this stage
	 */
	public Stage getStage() {
		return this.window;
	}
	
	/**
	 * @return the path to the stages custom theme stylesheet.
	 */
	public File getCustomThemeCSS() {
		return this.customThemeCSS;
	}
	
	/**
	 * @return the path the the active default stylesheet.
	 */
	public String getActiveStylesheet() {
	
		if(isDarkMode) {
			return settingsPanelDarkMode;
		}
		else {
			return settingsPanelLightMode;
		}
	}
}
