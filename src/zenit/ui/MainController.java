package zenit.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The controller part of the main GUI.
 * @author Pontus Laos, Oskar Molander
 *
 */
public class MainController {
	private Stage stage;
	private HashMap<Tab, File> currentlySelectedFiles;
	
	@FXML
	private TextArea taConsole;
	
	@FXML
	private MenuItem openFile;
	
	@FXML
	private MenuItem saveFile;
	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private TreeView treeView;
	
	/**
	 * Performs initialization steps when the controller is set.
	 * @param stage The stage to run the initialization on.
	 */
	public void initialize(Stage stage) {
		this.stage = stage;
		currentlySelectedFiles = new HashMap<>();
	}
	
	/**
	 * Opens a file dialog and tries to read the file's 
	 * name and content to the currently selected tab.
	 * @param event
	 */
	@FXML
	public void openFile(Event event) {
		FileChooser fileChooser = new FileChooser();

		try {
			Tab selectedTab = getSelectedTab();
			AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
			TextArea textArea = (TextArea) anchorPane.getChildren().get(0);

			currentlySelectedFiles.put(selectedTab, fileChooser.showOpenDialog(stage));
			
			selectedTab.setText(currentlySelectedFiles.get(selectedTab).getName());
			textArea.setText(readFile(currentlySelectedFiles.get(selectedTab)));
		} catch (NullPointerException ex) {
			ex.printStackTrace();

			// TODO: handle exception
		}
	}
	
	/**
	 * Opens a FileChooser dialog, and creates a new Tab if newTab is true.
	 * @param newTab Whether or not to create a new tab.
	 */
	public void openFile(boolean newTab) {
		if (newTab) {
			Tab tab = new Tab();
			AnchorPane anchorPane = new AnchorPane();
			TextArea textArea = new TextArea();
			
			anchorPane.getChildren().add(textArea);
			tab.setContent(anchorPane);
			
			AnchorPane.setTopAnchor(textArea, 0.0);
			AnchorPane.setRightAnchor(textArea, 0.0);
			AnchorPane.setBottomAnchor(textArea, 0.0);
			AnchorPane.setLeftAnchor(textArea, 0.0);
			
			tabPane.getTabs().add(tab);
			
			var selectionModel = tabPane.getSelectionModel();
			selectionModel.select(tab);
		}
		
		openFile(null);
	}
	
	public static void saveFile() {}
	
	/**
	 * Gets the text from the currently selected Tab and writes it to the currently selected file.
	 * @param event
	 */
	@FXML
	public void saveFile(Event event) {
		try {
			Tab selectedTab = getSelectedTab();
			AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
			TextArea textArea = (TextArea) anchorPane.getChildren().get(0);
			
			if (currentlySelectedFiles.containsKey(selectedTab)) {
				writeTextFile(currentlySelectedFiles.get(selectedTab).getAbsoluteFile(), textArea.getText());				
			}
			
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			
			// TODO: handle exception
		}
	}
	
	/**
	 * Reads a file line by line.
	 * @param file The File to read.
	 * @return A String containing all the lines of the File content. 
	 * Null if the file could not be read.
	 */
	private String readFile(File file) {
		try (
			var fileReader = new FileReader(file);
			var bufferedReader = new BufferedReader(fileReader);
		) {
			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}
			
			return builder.toString();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();

			// TODO: give the user feedback that the file could not be found
		} catch (IOException ex) {
			ex.printStackTrace();
			
			// TODO: handle IO exception
		}
		return null;
	}
	
	/**
	 * Writes the given text to the given file. Only used for text files.
	 * @param file The File to write to.
	 * @param text The text to write to the File.
	 */
	private void writeTextFile(File file, String text) {
		try (
			var fileWriter = new FileWriter(file);
			var bufferedWriter = new BufferedWriter(fileWriter);
		) {
			bufferedWriter.write(text);
		} catch (IOException ex) {
			ex.printStackTrace();

			// TODO: handle IO exception
		}
	}
	
	/**
	 * Gets the currently selected tab on the tab pane.
	 * @return The Tab that is currently selected. Null if none was found.
	 */
	private Tab getSelectedTab() {
		var tabs = tabPane.getTabs();

		for (Tab tab : tabs) {
			if (tab.isSelected()) {
				return tab;
			}
		}
		
		return null;
	}
}
