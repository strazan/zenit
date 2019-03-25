package zenit.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
import zenit.ConsoleRedirect;

/**
 * The controller part of the main GUI.
 * @author Pontus Laos, Oskar Molander
 *
 */
public class MainController {
	private Stage stage;
	
	@FXML
	private TextArea taConsole;
	
	@FXML
	private MenuItem openFile;
	
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
		new ConsoleRedirect(taConsole);
	}
	
	
	/**
	 * Opens a file dialog and tries to read the file's 
	 * name and content to the currently selected tab.
	 * @param event
	 */
	@FXML
	public void openFile(Event event) {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(stage);
		
		try {
			Tab selectedTab = getSelectedTab();
			AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
			TextArea textArea = (TextArea) anchorPane.getChildren().get(0);
	
			selectedTab.setText(selectedFile.getName());
			textArea.setText(readFile(selectedFile));
		} catch (NullPointerException ex) {
			// TODO: handle exceptions
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
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
		) {
			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}
			
			return builder.toString();
		} catch (FileNotFoundException ex) {
			// TODO: give the user feedback that the file could not be found
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO: handle IO exception
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the currently selected tab on the tab pane.
	 * @return The Tab that is currently selected. Null if none was found.
	 */
	private Tab getSelectedTab() {
		ObservableList<Tab> tabs = tabPane.getTabs();

		for (Tab tab : tabs) {
			if (tab.isSelected()) {
				return tab;
			}
		}
		
		return null;
	}
}
