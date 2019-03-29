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

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

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
import zenit.ConsoleRedirect;
import zenit.FileHandler;
import zenit.javacodeCompiler.JavaSourceCodeCompiler;
import zenit.textFlow.ZenCodeArea;

/**
 * The controller part of the main GUI.
 * @author Pontus Laos, Oskar Molander
 *
 */
public class MainController {
	private Stage stage;
	private HashMap<Tab, File> currentlySelectedFiles;
	private FileHandler fileHandler;

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
		fileHandler = new FileHandler();
		new ConsoleRedirect(taConsole);
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
				ZenCodeArea codeArea = (ZenCodeArea) anchorPane.getChildren().get(0);

				currentlySelectedFiles.put(selectedTab, file);
				
				String fileName = currentlySelectedFiles.get(selectedTab).getName();
				String fileContent = fileHandler.readFile(currentlySelectedFiles.get(selectedTab));

				selectedTab.setText(fileName);
				codeArea.replaceText(fileContent);
				
				
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
		} catch (Exception e){
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
		ZenCodeArea textArea = new ZenCodeArea();
		textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));

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
			ZenCodeArea textArea = (ZenCodeArea) anchorPane.getChildren().get(0);

			if (currentlySelectedFiles.containsKey(selectedTab)) {
				fileHandler.writeTextFile(currentlySelectedFiles.get(selectedTab).getAbsoluteFile(), textArea.getText());				
			} else {
				currentlySelectedFiles.put(selectedTab, createFile(textArea.getText()));
			}

		} catch (NullPointerException ex) {
			ex.printStackTrace();

			// TODO: handle exception
		}
	}

	/**
	 * Creates a new file and writes the given text to it.
	 * @param text The text to write to the new file.
	 * @return The created File if everything goes well, else null.
	 */
	private File createFile(String text) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(stage);

		if (file == null) {
			return null;
		}

		boolean ok = fileHandler.writeTextFile(file, text);

		return ok ? file : null;
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


