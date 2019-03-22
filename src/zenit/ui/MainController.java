package zenit.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javacodeCompiler.JavaSourceCodeCompiler;

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
	private MenuItem newFile;

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
	 * Adds a new tab to the TabPane.
	 * @param event
	 */
	@FXML
	public void newFile(Event event) {
		addTab();
	}

	/**
	 * Opens a file dialog and tries to read the file's 
	 * name and content to the currently selected tab.
	 * @param event
	 */
	@FXML
	public void openFile(Event event) {
		FileChooser fileChooser = new FileChooser();
		//TODO: Add filters

		try {
			File file = fileChooser.showOpenDialog(stage);

			if (file != null) {
				Tab selectedTab = addTab();
				AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
				TextArea textArea = (TextArea) anchorPane.getChildren().get(0);

				currentlySelectedFiles.put(selectedTab, file);
				selectedTab.setText(currentlySelectedFiles.get(selectedTab).getName());
				textArea.setText(readFile(currentlySelectedFiles.get(selectedTab)));
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();

			// TODO: handle exception
		}
	}

	/**
	 * If the file of the current tab is a .java file if will be compiled, into the same
	 * folder/directory, and the executed with only java standard lib.
	 */
	public void compileAndRun() {
		File file = currentlySelectedFiles.get(getSelectedTab());
		saveFile(null);
		try {
			if (file != null) {
				JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler();
				compiler.compileAndRunJavaFileWithoutPackage(file, file.getParent());
			}
		}
		catch (Exception e){
			e.printStackTrace();
			
			// TODO: handle exception
		}
	}

	/**
	 * Creates a new tab with a TextArea filling it, adds it to the TabPane, and focuses on it.
	 * @param onClick The Runnable to run when the tab should be closed.
	 * @return The new Tab.
	 */
	public Tab addTab() {
		Tab tab = new Tab("Untitled");
		AnchorPane anchorPane = new AnchorPane();
		TextArea textArea = new TextArea();

		anchorPane.getChildren().add(textArea);
		tab.setContent(anchorPane);
		textArea.setStyle("-fx-font-family: monospace");

		AnchorPane.setTopAnchor(textArea, 0.0);
		AnchorPane.setRightAnchor(textArea, 0.0);
		AnchorPane.setBottomAnchor(textArea, 0.0);
		AnchorPane.setLeftAnchor(textArea, 0.0);

		tab.setOnCloseRequest(event -> defaultCloseTabOperation());

		tabPane.getTabs().add(tab);

		var selectionModel = tabPane.getSelectionModel();
		selectionModel.select(tab);

		return tab;
	}

	/**
	 * From https://code.makery.ch/blog/javafx-dialogs-official/
	 */
	public void defaultCloseTabOperation() {
		Alert alert = new Alert(AlertType.CONFIRMATION);

		alert.setTitle("Close this tab?");
		alert.setHeaderText("Look, a Confirmation Dialog");
		alert.setContentText("Are you ok with this?");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK) {
			closeTab();
		} else {
			System.out.println("Canceling");
		}
	}

	/**
	 * Gets the currently selected tab, and removes it from the TabPane.
	 */
	public void closeTab() {
		Tab selectedTab = getSelectedTab();
		var tabs = tabPane.getTabs();

		if (tabs.indexOf(selectedTab) >= 0) {
			tabs.remove(selectedTab);
		}
	}

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
			else {
				currentlySelectedFiles.put(selectedTab, createFile(textArea.getText()));
			}

		} catch (NullPointerException ex) {
			ex.printStackTrace();

			// TODO: handle exception
		}
	}

	/**
	 * Creates a new file and writes the given text to it.
	 * @param text 
	 * @return
	 */
	private File createFile(String text) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(stage);

		if (file == null) {
			return null;
		}

		boolean ok = writeTextFile(file, text);

		return ok ? file : null;
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
	private boolean writeTextFile(File file, String text) {
		try (
				var fileWriter = new FileWriter(file);
				var bufferedWriter = new BufferedWriter(fileWriter);
				) {
			bufferedWriter.write(text);
		} catch (IOException ex) {
			ex.printStackTrace();

			return false;
		}
		return true;
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
