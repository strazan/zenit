package zenit.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import zenit.ConsoleRedirect;

import javacodeCompiler.JavaSourceCodeCompiler;

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
	private MenuItem newFile;

	@FXML
	private MenuItem openFile;

	@FXML
	private MenuItem saveFile;

	@FXML
	private TabPane tabPane;

	@FXML
	private TreeView<File> treeView;

	/**
	 * Performs initialization steps when the controller is set.
	 * @param stage The stage to run the initialization on.
	 */
	public void initialize(Stage stage) {
		this.stage = stage;
		
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
		File file = new FileChooser().showOpenDialog(null);

		if (file == null) {
			return;
		}

		FileTab tab = getTabFromFile(file);
		
		if (tab == null) {
			tab = addTab();
			tab.setFile(file, true);
		}
		
		tabPane.getSelectionModel().select(tab);
	}

	/**
	 * If the file of the current tab is a .java file if will be compiled, into the same
	 * folder/directory, and the executed with only java standard lib.
	 */
	public void compileAndRun() {
		File file = getSelectedTab().getFile();
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
	public FileTab addTab() {
		FileTab tab = new FileTab();
		tabPane.getTabs().add(tab);

		var selectionModel = tabPane.getSelectionModel();
		selectionModel.select(tab);
		
		return tab;
	}

	/**
	 * Gets the currently selected tab, and removes it from the TabPane.
	 */
	public void closeTab() {
		FileTab selectedTab = getSelectedTab();
		selectedTab.closeTab(this);
	}

	/**
	 * Gets the text from the currently selected Tab and writes it to the currently selected file.
	 * @param event
	 */
	@FXML
	public void saveFile(Event event) {
		try {
			FileTab selectedTab = getSelectedTab();

			if (selectedTab != null) {
				selectedTab.save();
			}
		} catch (IOException ex) {
			System.out.println("Could not save file.");
		}
	}

	/**
	 * Gets the currently selected tab on the tab pane.
	 * @return The Tab that is currently selected. Null if none was found.
	 */
	public FileTab getSelectedTab() {
		var tabs = tabPane.getTabs();

		for (Tab tab : tabs) {
			if (tab.isSelected()) {
				return (FileTab) tab;
			}
		}

		return null;
	}
	
	private FileTab getTabFromFile(File file) {
		var tabs = tabPane.getTabs();
		
		for (Tab tab : tabs) {
			FileTab fileTab = (FileTab) tab;
			File tabFile = fileTab.getFile();
			
			if (tabFile != null && file.equals(tabFile)) {
				return fileTab;
			}
		}
		
		return null;
	}
}
