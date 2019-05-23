package main.java.zenit.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import main.java.zenit.console.ConsoleController;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.filesystem.WorkspaceHandler;
import main.java.zenit.javacodecompiler.DebugError;
import main.java.zenit.javacodecompiler.DebugErrorBuffer;
import main.java.zenit.javacodecompiler.JavaSourceCodeCompiler;
import main.java.zenit.javacodecompiler.ProcessBuffer;
import main.java.zenit.settingspanel.SettingsPanelController;
import main.java.zenit.settingspanel.ThemeCustomizable;
import main.java.zenit.searchinfile.Search;
import main.java.zenit.ui.tree.FileTree;
import main.java.zenit.ui.tree.FileTreeItem;
import main.java.zenit.ui.tree.TreeClickListener;
import main.java.zenit.ui.tree.TreeContextMenu;
import main.java.zenit.ui.FileTab;
import main.java.zenit.zencodearea.ZenCodeArea;

/**
 * The controller part of the main GUI.
 * 
 * @author Pontus Laos, Oskar Molander, Alexander Libot
 *
 */
public class MainController extends VBox implements ThemeCustomizable {
	private Stage stage;
	private FileController fileController;
	private int zenCodeAreasTextSize;
	private String zenCodeAreasFontFamily;
	private String activeStylesheet;
	private LinkedList<ZenCodeArea> activeZenCodeAreas;
	private File customThemeCSS;
	private boolean isDarkMode = false;

	@FXML
	private AnchorPane consolePane;

	@FXML
	private MenuItem newFile;

	@FXML
	private MenuItem newProject;

	@FXML
	private MenuItem openFile;

	@FXML
	private MenuItem saveFile;

	@FXML
	private MenuItem importProject;

	@FXML
	private MenuItem changeWorkspace;

	@FXML
	private CheckMenuItem cmiDarkMode;

	@FXML
	private TabPane tabPane;

	@FXML
	private TreeView<String> treeView;

	@FXML
	private Button btnRun;

	@FXML
	private Button btnStop;

	@FXML
	private ConsoleController consoleController;

	private Label statusBarLeftLabel;

	@FXML
	private Label statusBarRightLabel;

	/**
	 * Loads a file Main.fxml, sets this MainController as its Controller, and loads
	 * it.
	 */
	public MainController(Stage s) {
		this.stage = s;
		this.zenCodeAreasTextSize = 12;
		this.zenCodeAreasFontFamily = "Menlo";
		this.activeZenCodeAreas = new LinkedList<ZenCodeArea>();
		this.customThemeCSS = new File("/customtheme/mainCustomTheme.css");

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/ui/Main.fxml"));

			/*
			 * TODO Test if you like this idea. Saves and opens a local File-instance of
			 * your selected workspace. Only prompts when unset and can be changed from
			 * within gui Alex
			 */

			File workspace = null;

			try {
				workspace = WorkspaceHandler.readWorkspace();
			} catch (IOException ex) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Select new workspace folder");
				workspace = directoryChooser.showDialog(stage);
			}

			FileController fileController = new FileController(workspace);
			setFileController(fileController);

			if (workspace != null) {
				// TODO: Log this
				fileController.changeWorkspace(workspace);
			}

			loader.setRoot(this);
			loader.setController(this);
			loader.load();

			Scene scene = new Scene(this);
			scene.getStylesheets().add(getClass().getResource("/zenit/ui/mainStyle.css").toString());

			scene.getStylesheets().add(getClass().getResource("/zenit/ui/keywords.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Zenit");

			initialize();
			stage.show();
			KeyboardShortcuts.setupMain(scene, this);

			this.activeStylesheet = getClass().getResource("/zenit/ui/mainStyle.css").toExternalForm();
			openSettingsPanel();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setter for FileController instance. Used to access the file system.
	 */
	public void setFileController(FileController fileController) {
		this.fileController = fileController;
	}

	/**
	 * Performs initialization steps when the controller is set.
	 */
	public void initialize() {

		btnRun.setPickOnBounds(true);
		btnRun.setOnAction(event -> compileAndRun());
		initTree();
	}

	/**
	 * Creates a new SettingsPanel.
	 * 
	 * @author Sigge Labor
	 */
	public void openSettingsPanel() {
		new SettingsPanelController(this, zenCodeAreasTextSize, zenCodeAreasFontFamily);
	}

	/**
	 * Sets the zenCodeAreasTextSize to a new value.
	 * 
	 * @author Sigge Labor
	 */
	public synchronized void setFontSize(int newFontSize) {
		zenCodeAreasTextSize = newFontSize;
		updateZenCodeAreasAppearance();
	}

	/**
	 * Sets the zenCodeAreasFontFamily to a new value.
	 * 
	 * @author Sigge Labor.
	 */
	public synchronized void setFontFamily(String newFontFamily) {
		zenCodeAreasFontFamily = newFontFamily;
		updateZenCodeAreasAppearance();
	}

	/**
	 * Updates the appearance (text size and font family) of all active
	 * ZenCodeAreas.
	 * 
	 * @author Sigge Labor.
	 */
	public void updateZenCodeAreasAppearance() {
		for (int i = 0; i < activeZenCodeAreas.size(); i++) {
			activeZenCodeAreas.get(i).updateAppearance(zenCodeAreasFontFamily, zenCodeAreasTextSize);
		}
	}

	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}

	public ZenCodeArea createNewZenCodeArea() {
		ZenCodeArea zenCodeArea = new ZenCodeArea(zenCodeAreasTextSize, zenCodeAreasFontFamily);
		activeZenCodeAreas.add(zenCodeArea);
		return zenCodeArea;
	}

	/**
	 * Initializes the {@link javafx.scene.control.TreeView TreeView}. Creates a
	 * root node from the workspace-file in the fileController class. Calls
	 * FileTree-method to add all files in the workspace folder to the tree. Creates
	 * a TreeContextMenu for displaying when right clicking nodes in the tree and an
	 * event handler for clicking nodes in the tree.
	 */
	private void initTree() {
		FileTreeItem<String> rootItem = new FileTreeItem<String>(fileController.getWorkspace(), "workspace",
				FileTreeItem.WORKSPACE);
		File workspace = fileController.getWorkspace();
		if (workspace != null) {
			FileTree.createNodes(rootItem, workspace);
		}
		treeView.setRoot(rootItem);
		treeView.setShowRoot(false);
		TreeContextMenu tcm = new TreeContextMenu(this, treeView);
		TreeClickListener tcl = new TreeClickListener(this, treeView);
		treeView.setContextMenu(tcm);
		treeView.setOnMouseClicked(tcl);
	}

	/**
	 * Input name from dialog box and creates a new file in specified parent folder.
	 * 
	 * @param parent   The parent folder of the file to be created.
	 * @param typeCode The type of code snippet that should be implemented in the
	 *                 file. Use constants from
	 *                 {@link main.java.zenit.filesystem.helpers.CodeSnippets
	 *                 CodeSnippets} class.
	 * @return The File if created, otherwise null.
	 */
	public File createFile(File parent, int typeCode) {
		File file = null;
		String className = DialogBoxes.inputDialog(null, "New file", "Create new file", "Enter new file name",
				"File name");
		if (className != null) {
			String filepath = parent.getPath() + "/" + className;
			file = new File(filepath);

			file = fileController.createFile(file, typeCode);

			openFile(file);
		}
		return file;
	}

	/**
	 * If a tab is open, attempt to call its shortcutsTrigger-method.
	 */
	public void shortcutsTrigger() {
		FileTab selectedTab = getSelectedTab();

		if (selectedTab != null) {
			selectedTab.shortcutsTrigger();
		}
	}

	/**
	 * If a tab is open, attempt to call its commentShortcutsTrigger-method.
	 */
	public void commentsShortcutsTrigger() {
		FileTab selectedTab = getSelectedTab();

		if (selectedTab != null) {
			selectedTab.commentsShortcutsTrigger();
		}
	}

	public void navigateToCorrectTabIndex() {
		FileTab selectedTab = getSelectedTab();

		if (selectedTab != null) {
			selectedTab.navigateToCorrectTabIndex();
		}
	}

	/**
	 * Grabs the text from the currently selected Tab and writes it to the currently
	 * selected file. If no file selected, opens a file chooser for selection of
	 * file to overwrite.
	 * 
	 * @param event
	 */
	@FXML
	public boolean saveFile(Event event) {
		return saveFile(true);
	}

	private boolean saveFile(boolean backgroundCompile) {
		FileTab tab = getSelectedTab();
		File file = tab.getFile();

		if (file == null) {
			file = chooseFile();
		}

		boolean didWrite = fileController.writeFile(file, tab.getFileText());

		if (didWrite) {
			tab.update(file);

			if (backgroundCompile) {
				backgroundCompiling(file);
			}
		} else {
			System.out.println("Did not write.");
		}

		return didWrite;
	}

	/**
	 * Compiles a file in the background.
	 * 
	 * @param file
	 */
	private void backgroundCompiling(File file) {
		File metadataFile = getMetadataFile(file);

		try {
			if (file != null) {
				DebugErrorBuffer buffer = new DebugErrorBuffer();
				JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler(file, metadataFile, true, buffer, this);
				compiler.startCompile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void errorHandler(DebugErrorBuffer buffer) {
		DebugError error;
		while (!buffer.isEmpty()) {
			error = buffer.get();

			getSelectedTab().setStyle(error.getRow(), error.getColumn(), "underline");
		}
	}

	/**
	 * Opens a file chooser and returns the selected file.
	 */
	private File chooseFile() {
		FileChooser fileChooser = new FileChooser();
		File workspace = fileController.getWorkspace();
		if (workspace != null) {
			fileChooser.setInitialDirectory(fileController.getWorkspace());
		}
		return fileChooser.showSaveDialog(stage);
	}

	/**
	 * Adds a new tab to the TabPane.
	 * 
	 * @param event
	 */
	@FXML
	public void newFile(Event event) {
		addTab();
	}

	/**
	 * Opens a file chooser and tries to read the file's name and content to the
	 * currently selected tab.
	 * 
	 * @param event
	 */
	@FXML
	public void openFile(Event event) {
		try {
			FileChooser fileChooser = new FileChooser();
			File workspace = fileController.getWorkspace();
			if (workspace != null) {
				fileChooser.setInitialDirectory(fileController.getWorkspace());
			}
			File file = fileChooser.showOpenDialog(stage);

			if (file != null) {
				openFile(file);
			}

		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Tries to open the content of a file into a new tab using the FileController
	 * instance. If tab containing file-content is already open, switches to that
	 * tab.
	 * 
	 * @param file The file which content to be opened.
	 */
	public void openFile(File file) {
		if (file != null && getTabFromFile(file) == null) {

			FileTab selectedTab = addTab();
			selectedTab.setFile(file, true);

			selectedTab.setText(file.getName());
		} else if (file != null && getTabFromFile(file) != null) { // Tab already open
			tabPane.getSelectionModel().select(getTabFromFile(file));
		}
	}

	/**
	 * Method for renaming a file or folder. Opens a new input dialog for input of
	 * new name. Renames the tab text if file is in an open tab.
	 * 
	 * @param file The file to rename.
	 * @return
	 */
	public File renameFile(File file) {
		File newFile = null;
		int prefixPosition = file.getName().lastIndexOf('.');

		String newName = DialogBoxes.inputDialog(null, "New name", "Rename file", "Enter a new name", file.getName(), 0,
				prefixPosition);
		if (newName != null) {
			newFile = fileController.renameFile(file, newName);
			var tabs = tabPane.getTabs();
			for (Tab tab : tabs) {
				FileTab fileTab = (FileTab) tab;
				if (fileTab.getText().equals(file.getName())) {
					fileTab.setText(newName);
					fileTab.setFile(newFile, false);
					break;
				}
			}
		}
		return newFile;
	}

	/**
	 * Tries to delete a file or folder.
	 * 
	 * @param file The file to be deleted.
	 */
	public void deleteFile(File file) {
		fileController.deleteFile(file);
		// TODO Remove open tab aswell
	}

	/**
	 * Opens an input dialog to choose project name and then creates a new project
	 * with that name in the selected workspace folder
	 * 
	 * @param event
	 */
	@FXML
	public void newProject(Event event) {
		String projectName = DialogBoxes.inputDialog(null, "New project", "Create new project",
				"Enter a new projectname", "Project name");
		if (projectName != null) {
			File newProject = fileController.createProject(projectName);
			if (newProject != null) {
				FileTree.createParentNode((FileTreeItem<String>) treeView.getRoot(), newProject);
			}
		}
	}

	/**
	 * Opens an input dialog to choose package name and then creates a new package
	 * with that name in the selected folder (usually src).
	 * 
	 * @param parent Folder to create package in.
	 * @return The created package if created, otherwise null.
	 */
	public File newPackage(File parent) {

		String packageName = DialogBoxes.inputDialog(null, "New package", "Create new package",
				"Enter new package name", "Package name");
		if (packageName != null) {
			String filepath = parent.getPath() + "/" + packageName;
			File packageFile = new File(filepath);

			boolean success = fileController.createPackage(packageFile);

			if (success) {
				return packageFile;
			}
		}
		return null;
	}

	/**
	 * If the file of the current tab is a .java file if will be compiled, into the
	 * same folder/directory, and the executed with only java standard lib.
	 */
	public void compileAndRun() {
		if (getSelectedTab() != null) {
			File file = getSelectedTab().getFile();
			File metadataFile = getMetadataFile(file);
			saveFile(false);

			consoleController.newConsole(); // TODO: Maybe but in a better place ?

			try {
				ProcessBuffer buffer = new ProcessBuffer();
				JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler(file, metadataFile, false, buffer, this);
				compiler.startCompileAndRun();
				Process process = buffer.get();
				if (process != null && process.isAlive()) {
					// TODO Create new console tab from here.
				}

			} catch (Exception e) {
				e.printStackTrace();

				// TODO: handle exception
			}

		}
	}

	public void updateStatusLeft(String text) {
		statusBarLeftLabel.setText(text);
	}

	public void updateStatusRight(String text) {
		statusBarRightLabel.setText(text);
	}

	/**
	 * Finds the metadata file for the project of a file.
	 * 
	 * @param file File within project to find metadata file in.
	 * @return The found metadata file, null if not found.
	 */
	public static File getMetadataFile(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File entry : files) {
				if (entry.getName().equals(".metadata")) {
					return entry;
				}
			}
		}
		File parent = file.getParentFile();
		if (parent == null) {
			return null;
		}
		return getMetadataFile(parent);
	}

	/**
	 * Creates a new tab with a {@link main.java.zenit.zencodearea.ZenCodeArea
	 * ZenCodeArea} filling it, adds it to the TabPane, and focuses on it.
	 * 
	 * @param onClick The Runnable to run when the tab should be closed.
	 * @return The new Tab.
	 */
	public FileTab addTab() {
		FileTab tab = new FileTab(createNewZenCodeArea());
		tab.setOnCloseRequest(event -> closeTab(event));
		tabPane.getTabs().add(tab);

		var selectionModel = tabPane.getSelectionModel();
		selectionModel.select(tab);

		return tab;
	}

	/**
	 * Gets the currently selected tab, and removes it from the TabPane. If the file
	 * has been modified, a dialog is shown asking if the user wants to save or not,
	 * or abort.
	 */
	public void closeTab(Event event) {
		FileTab selectedTab = getSelectedTab();

		if (selectedTab.getFile() != null && selectedTab.hasChanged()) {
			int response = selectedTab.showConfirmDialog();

			switch (response) {
			case 1:
				tabPane.getTabs().remove(selectedTab);
				break;
			case 2:
				saveFile(null);
				tabPane.getTabs().remove(selectedTab);
				break;
			default:
				if (event != null) {
					event.consume();
				}
				return;
			}
		} else if (selectedTab.hasChanged()) {
			boolean didSave = saveFile(null);

			if (didSave) {
				Platform.runLater(() -> tabPane.getTabs().remove(selectedTab));
			}
		} else {
			tabPane.getTabs().remove(selectedTab);
		}
	}

	/**
	 * Changes the workspace to another folder and restarts the program.
	 */
	@FXML
	public void changeWorkspace() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select new workspace folder");
		File workspace = directoryChooser.showDialog(stage);
		if (workspace != null) {
			boolean success = fileController.changeWorkspace(workspace);
			if (success) {
				stage.close();
				try {
					new TestUI().start(stage);
				} catch (IOException ex) {
					System.err.println("MainController.changeWorkspace: IOException: " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * Gets the currently selected tab on the tab pane.
	 * 
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

	/**
	 * Gets the FileTab in the TabPane that is associated with the specified File.
	 * 
	 * @param file The File to search for.
	 * @return The FileTab instance that holds the File, or null if no tab does.
	 */
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

	/**
	 * Tries to import a folder. Displays a directory chooser and copies the
	 * selected folder into the current workspace using
	 * {@link main.java.zenit.filesystem.FileController#importProject(File)
	 * importProject(File)} Displays an error or information dialog to display the
	 * result.
	 */
	@FXML
	public void importProject() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select project to import");
		File source = directoryChooser.showDialog(stage);

		if (source != null) {
			try {
				File target = fileController.importProject(source);
				FileTree.createParentNode((FileTreeItem<String>) treeView.getRoot(), target);
				DialogBoxes.informationDialog("Import complete", "Project is imported to workspace");
			} catch (IOException ex) {
				DialogBoxes.errorDialog("Import failed", "Couldn't import project", ex.getMessage());
			}
		}
	}

	/**
	 * @return the path to the stages custom theme stylesheet.
	 */
	public File getCustomThemeCSS() {
		return this.customThemeCSS;
	}

	public void search() {

		FileTab selectedTab = getSelectedTab();
		ZenCodeArea zenCodeArea = selectedTab.getZenCodeArea();
		File file = selectedTab.getFile();

		if (selectedTab != null) {
			new Search(zenCodeArea, file, isDarkMode);
		}
	}

	@Override
	public String getActiveStylesheet() {
		// TODO Auto-generated method stub
		return activeStylesheet;
	}
}
