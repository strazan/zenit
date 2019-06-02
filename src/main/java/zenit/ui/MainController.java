package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import java.util.LinkedList;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import main.java.zenit.Zenit;
import main.java.zenit.console.ConsoleArea;
import main.java.zenit.console.ConsoleController;
import main.java.zenit.filesystem.FileController; // Aggregation
import main.java.zenit.filesystem.ProjectFile;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.WorkspaceHandler;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.javacodecompiler.DebugError;
import main.java.zenit.javacodecompiler.DebugErrorBuffer;
import main.java.zenit.javacodecompiler.JavaSourceCodeCompiler;
import main.java.zenit.javacodecompiler.ProcessBuffer;
import main.java.zenit.settingspanel.SettingsPanelController;
import main.java.zenit.settingspanel.ThemeCustomizable; // Implements
import main.java.zenit.searchinfile.Search;
import main.java.zenit.ui.tree.FileTree;
import main.java.zenit.ui.tree.FileTreeItem;
import main.java.zenit.ui.tree.TreeClickListener;
import main.java.zenit.ui.tree.TreeContextMenu;
import main.java.zenit.util.Tuple;
import main.java.zenit.ui.FileTab;
import main.java.zenit.ui.projectinfo.ProjectMetadataController;
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

	private ProjectMetadataController pmc;
	
	private int zenCodeAreasTextSize;
	
	private String zenCodeAreasFontFamily;
	private String activeStylesheet;
	
	private LinkedList<ZenCodeArea> activeZenCodeAreas;
	
	private File customThemeCSS;
	
	private boolean isDarkMode = true;
	
	private Process process;
	
	private Tuple<File, String> deletedFile = new Tuple<>();

	@FXML
	private AnchorPane consolePane;
		
	@FXML 
	private SplitPane splitPane;

	@FXML
	private MenuItem newTab;
	
	@FXML 
	private MenuItem newFile;
	
	@FXML 
	private MenuItem newFolder;

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
	private MenuItem JREVersions;
	
	@FXML
	private CheckMenuItem cmiDarkMode;
	
	@FXML
	private MenuItem undo;
	
	@FXML
	private MenuItem redo;
	
	@FXML
	private MenuItem delete;

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

	@FXML
	private Label statusBarLeftLabel;

	@FXML
	private Label statusBarRightLabel;
	
	@FXML
	private FXMLLoader loader;
	

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
			stage.setTitle("Zenit - " + workspace.getPath());

			initialize();
			
			stage.show();
			KeyboardShortcuts.setupMain(scene, this);

			this.activeStylesheet = getClass().getResource("/zenit/ui/mainStyle.css").toExternalForm();
			
			stage.setOnCloseRequest(event -> quit());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FXMLLoader getFXMLLoader() {
		return loader;
	}
	
	/**
	 * Setter for FileController instance. Used to access the file system.
	 */
	public void setFileController(FileController fileController) {
		this.fileController = fileController;
	}
	
	public FileTreeItem<String> getSelectedFileTreeItem() {
		return (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
	}
	
	public void deleteFileFromTreeView() {
		var selectedItem = getSelectedFileTreeItem();
		
		if (selectedItem != null) {
			deleteFile(selectedItem.getFile());
			selectedItem.getParent().getChildren().remove(selectedItem);
		}
	}

	/**
	 * Performs initialization steps when the controller is set.
	 */
	public void initialize() {
		
		statusBarLeftLabel.setText("");
		statusBarRightLabel.setText("");

		btnRun.setPickOnBounds(true);
		btnRun.setOnAction(event -> compileAndRun());
		btnStop.setOnAction(event -> terminate());
		initTree();
		consoleController.setMainController(this);
	}

	/**
	 * Creates a new SettingsPanel.
	 * 
	 * @author Sigge Labor
	 */
	public void openSettingsPanel() {
		new SettingsPanelController(this, zenCodeAreasTextSize, zenCodeAreasFontFamily, consoleController);
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
		
		rootItem.getChildren().sort((o1,o2)->{
			FileTreeItem<String> t1 = (FileTreeItem<String>) o1;
			FileTreeItem<String> t2 = (FileTreeItem<String>) o2;
			return (t1.getValue().compareTo(t2.getValue()));
		});
	}

	/**
	 * Input name from dialog box and creates a new file in specified parent folder.
	 * 
	 * @param parent The parent folder of the file to be created.
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
	 * Runs {@link #saveFile(boolean)} with parameter true.
	 */
	@FXML
	public boolean saveFile(Event event) {
		return saveFile(true);
	}

	/**
	 * Grabs the text from the currently selected Tab and writes it to the currently
	 * selected file. If no file selected, opens a file chooser for selection of
	 * file to overwrite.
	 * @param backgroundCompile {@code true} if code should compile in background upon save
	 * @return {@code true} if written to file, otherwise {@code false}
	 */
	private boolean saveFile(boolean backgroundCompile) {
		FileTab tab = getSelectedTab();
		
		if (tab == null) {
			return false;
		}
		
		File file = tab.getFile();

		if (file == null) {
			file = chooseFile();
		}

		boolean didWrite = fileController.writeFile(file, tab.getFileText());

		if (didWrite) {
			tab.update(file);
			FileTree.createParentNode((FileTreeItem<String>) treeView.getRoot(), file);
			
			if (backgroundCompile) {
				backgroundCompiling(file);
			}
		} else {
			System.out.println("Did not write.");
		}

		return didWrite;
	}

	/**
	 * Saves an arbitrary string to a file.
	 * @param backgroundCompile Whether or not the saving should initiate a compiling process.
	 * @param file The file to save the text to.
	 * @param text The text to write to the file.
	 * @return True if the file was written to, else false.
	 * @author Pontus Laos
	 */
	private boolean saveFile(boolean backgroundCompile, File file, String text) {
		if (file == null) {
			return saveFile(backgroundCompile);
		}

		boolean didWrite = fileController.writeFile(file, text);
		
		if (didWrite) {
			FileTreeItem<String> root = FileTree.getTreeItemFromFile((FileTreeItem<String>) treeView.getRoot(), file.getParentFile());
			System.out.println(root);
			FileTree.createParentNode(root, file);
			treeView.refresh();
			treeView.layout();
			
			if (backgroundCompile) {
				backgroundCompiling(file);
			}
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

	/**
	 * Collects errors from buffer and displays them in code area
	 * @param buffer Buffer to collect errors from
	 */
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
	public void newTab(Event event) {
		addTab();
	}
	
	@FXML
	public void newFile() {
		NewFileController nfc = new NewFileController(fileController.getWorkspace(), isDarkMode);
		nfc.start();
		File newFile = nfc.getNewFile();

		if (newFile != null) {
			initTree();
			openFile(newFile);
		}
	}
	
	@FXML
	public void newFolder() {
		new NewFolderController(fileController.getWorkspace(), isDarkMode).start();
		initTree();
	}
	
	@FXML
	public void quit() {
		System.exit(0);
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
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt", "*.java");
			fileChooser.getExtensionFilters().add(extFilter);
			
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

			if (supportedFileFormat(file)) {
			
			FileTab selectedTab = addTab();
			selectedTab.setFile(file, true);

			selectedTab.setText(file.getName());
			} else {
				String fileType = file.getName().substring(file.getName().lastIndexOf('.'));
				DialogBoxes.errorDialog("Not supported", "File type not supported by Zenit", 
						"The file type " + fileType + " is not yet supported by this application.");
			}
		} else if (file != null && getTabFromFile(file) != null) { // Tab already open
			tabPane.getSelectionModel().select(getTabFromFile(file));
		}
	}
	
	/**
	 * Checks if the file format of the file parameter is supported.
	 * @param file File to check
	 * @return {@code true} if file format is supported, otherwise {@code false}
	 */
	private boolean supportedFileFormat(File file) {
		boolean supported = false;
		
		String fileName = file.getName();
		int periodIndex = fileName.lastIndexOf('.');
		if (periodIndex >= 0) {
			String fileType = fileName.substring(periodIndex);
		
		switch (fileType) {
		case ".java":
		case ".txt": supported = true; break;
		default: 
		
		}
		
		} else if (file.isFile()){
			supported = true;
		}
		
		return supported;

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
//		deletedTexts.put(file, FileController.readFile(file));
//		
//		fileHistory.add(0, file);
//		historyIndex++;
//		System.out.println(historyIndex);
		
		deletedFile.set(file, FileController.readFile(file));
		
		fileController.deleteFile(file);
		
		var tabs = tabPane.getTabs();
		
		if (tabs != null) {
			for (var tab : tabs) {
				var fileTab = (FileTab) tab;
				
				if (fileTab != null && fileTab.getFile().equals(file)) {
					Platform.runLater(() -> closeTab(null));
					return;
				}
			}
		}
	}
	
	/**
	 * Attempts to undo the latest delete invocation.
	 */
	public void undoDeleteFile() {
		if (!treeView.isFocused()) {
			System.out.println("not focused");
			return;
		}
		
		if (deletedFile.fst() != null && !deletedFile.fst().exists()) {
			try {
				deletedFile.fst().createNewFile();
				fileController.writeFile(deletedFile.fst(), deletedFile.snd());
				saveFile(false, deletedFile.fst(), deletedFile.snd());
			} catch (IOException ex) {}
		}
	}
	
	public void redoDeleteFile() {
		if (!treeView.isFocused()) {
			System.out.println("not focused");
			return;
		}
		
		if (deletedFile.fst() != null && deletedFile.fst().exists()) {
			deleteFile(deletedFile.fst());
			FileTree.removeFromFile((FileTreeItem<String>) treeView.getRoot(), deletedFile.fst());
		}
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
	
	@FXML
	public void undo(Event event) {
		FileTab selectedTab = getSelectedTab();
		ZenCodeArea zenCodeArea = selectedTab == null ? null : selectedTab.getZenCodeArea();
		
		if (treeView.isFocused()) {
			undoDeleteFile();
		} else if (zenCodeArea != null && zenCodeArea.isFocused()) {
			if (zenCodeArea.isUndoAvailable()) {
				zenCodeArea.undo();
			}
		}
	}
	
	@FXML
	public void redo(Event event) {
		FileTab selectedTab = getSelectedTab();
		ZenCodeArea zenCodeArea = selectedTab == null ? null : selectedTab.getZenCodeArea();
		
		if (treeView.isFocused()) {
			redoDeleteFile();
		} else if (zenCodeArea != null && zenCodeArea.isFocused()) {
			if (zenCodeArea.isRedoAvailable()) {
				zenCodeArea.redo();
			}
		}
	}
	
	@FXML
	public void delete(Event event) {
		deleteFileFromTreeView();
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
	
	public void compileAndRun(File file) {
		File metadataFile = getMetadataFile(file);
		ConsoleArea consoleArea;
		
	
		if(isDarkMode) {
			consoleArea = new ConsoleArea(file.getName(), null, "-fx-background-color:#444");
		}
		else {
			consoleArea = new ConsoleArea(file.getName(), null, "-fx-background-color:#989898");
		}
		consoleArea.setFileName(file.getName());
		consoleController.newConsole(consoleArea);
		openConsoleComponent();
		
		try {
			ProcessBuffer buffer = new ProcessBuffer();
			JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler(file, metadataFile,
					false, buffer, this);
			compiler.startCompileAndRun();
			process = buffer.get();
			
			
			if (process != null && metadataFile != null) {
				//If process compiled, add to RunnableClass
				ProjectFile projectFile = new ProjectFile(metadataFile.getParent());
				String src = projectFile.getSrc().getPath();
				String filePath = file.getPath().replaceAll(Matcher.quoteReplacement(src + 
						File.separator), "");
				RunnableClass rc = new RunnableClass(filePath);
				Metadata metadata = new Metadata(metadataFile);
				if (metadata.addRunnableClass(rc)) {
					metadata.encode();
				}
				
				
				consoleArea.setProcess(process);
				if(consoleArea.getProcess().isAlive()) {
					consoleArea.setID(consoleArea.getFileName() + " <Running>");
					
				}
				else {
					consoleArea.setID(consoleArea.getFileName()+ " <Terminated>");
				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// TODO: handle exception
		}
	
	
	}

	/**
	 * If the file of the current tab is a .java file if will be compiled, into the
	 * same folder/directory, and the executed with only java standard lib.
	 */
	@FXML
	public void compileAndRun() {
		if (getSelectedTab() != null) {
			File file = getSelectedTab().getFile();
			saveFile(false);
			compileAndRun(file);
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
				if (entry.getName().equals(".metadata") && entry.isFile()) {
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
		FileTab tab = new FileTab(createNewZenCodeArea(), this);
		tab.setOnCloseRequest(event -> closeTab(event));
		tabPane.getTabs().add(tab);

		var selectionModel = tabPane.getSelectionModel();
		selectionModel.select(tab);
		
		updateStatusRight("");

		return tab;
	}

	/**
	 * Gets the currently selected tab, and removes it from the TabPane. If the file
	 * has been modified, a dialog is shown asking if the user wants to save or not,
	 * or abort.
	 */
	@FXML
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
		
		updateStatusRight("");
	}

	/**
	 * Changes the workspace to another folder and restarts the program.
	 */
	@FXML
	public void changeWorkspace() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		directoryChooser.setTitle("Select new workspace folder");
		File workspace = directoryChooser.showDialog(stage);
		if (workspace != null) {
			stage.close();
			boolean success = fileController.changeWorkspace(workspace);
			if (success) {	
				try {
//					new TestUI().start(stage);
					new Zenit().start(stage);
				} catch (Exception ex) {
					System.err.println("MainController.changeWorkspace: IOException: " + ex.getMessage());
				}
			} else {
				stage.show();
				DialogBoxes.errorDialog("Can't change workspace", "", "");
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
		
	/**
	 * Opens the search panel if there is a selected tab.
	 */
	@FXML
	public void search() {
		FileTab selectedTab = getSelectedTab();

		if (selectedTab != null) {
			ZenCodeArea zenCodeArea = selectedTab.getZenCodeArea();
			File file = selectedTab.getFile();
			new Search(zenCodeArea, file, isDarkMode, this);
		}
	}



	@Override
	public String getActiveStylesheet() {
		// TODO Auto-generated method stub
		return activeStylesheet;
	}
	
	/** 
	 * If there isn't a comment at the start of the line the method comments 
	 * and if there is a comment the method removes it.
	 * 
	 * @author Fredrik Eklundh
	 */
	public void commentAndUncomment() {

		ZenCodeArea zenCodeArea = getSelectedTab().getZenCodeArea();
		
		int caretPos = zenCodeArea.getCaretPosition();
		
		int caretColumn = zenCodeArea.getCaretColumn();
		
		int length = zenCodeArea.getLength();
		
		int whereToReplaceFirstLine = caretPos - caretColumn;
		
		int rowNumber = zenCodeArea.getCurrentParagraph();
		
		int paragraphLength = zenCodeArea.getParagraphLength(rowNumber);
		
		List<Integer> whereToReplaceList = new ArrayList<>();
		
		IndexRange zen = zenCodeArea.getSelection();
		
		int endOfSelection = zen.getEnd();
		
		int startOfSelection = zen.getStart();
		
		boolean topDown = true;
		
		int n = 1;
		
		int whereToReplace = whereToReplaceFirstLine;
		
		whereToReplaceList.add(whereToReplaceFirstLine);
		
		//If the selection starts at least one row above the end of the selection 
		if (caretPos == endOfSelection && whereToReplaceFirstLine > startOfSelection) {
			topDown = true;
			do {
				
				whereToReplace = whereToReplace - 1 - zenCodeArea.getParagraphLength(rowNumber - n);
				n++;
				whereToReplaceList.add(whereToReplace);
				
			}while (whereToReplace > startOfSelection);
		}
		
		//If the selection starts at least one row below the end of the selection 
		if (caretPos == startOfSelection && whereToReplace + paragraphLength < endOfSelection) {
			topDown = false;
			do {
				
				whereToReplace = whereToReplace + 1 + zenCodeArea.getParagraphLength(rowNumber + n - 1);
				n++;
				whereToReplaceList.add(whereToReplace);
				
			}while (whereToReplace + zenCodeArea.getParagraphLength(rowNumber + n - 1) < endOfSelection);

		}

		boolean[] addComment = new boolean[whereToReplaceList.size()];
		
		//Comment or uncomment from the top and down then moves the caret to the "new" right position
		if (topDown == true) {
			
			int stepsToMove = 0;
			
			for (int i = 0; i < n; i++) {
				whereToReplace = whereToReplaceList.get(i);
				
				if (caretPos > length - 3) {
					zenCodeArea.insertText(caretPos, "	  ");
				}
				
				if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ")) {

					if (zenCodeArea.getText(whereToReplace, whereToReplace + 4).equals("// *")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						stepsToMove = stepsToMove - 2;
						addComment[i] = false;
						
					}else {
						zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "  ");
						addComment[i] = false;
					}
					
				}else if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ") == false) {
					
					if (zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("//")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						addComment[i] = false;
						
						if (whereToReplace == caretPos) {
							
						}else if (whereToReplace + 1 == caretPos) {
							stepsToMove--;
							
						}else {
							stepsToMove = stepsToMove - 2;
						}
					
					}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 4).equals("    ")) {
						zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//");
						addComment[i] = false;
						
					}else {
						zenCodeArea.insertText(whereToReplace, "//");
						stepsToMove = stepsToMove + 2;
						addComment[i] = true;
					}		
				}	
			}
			
			if(whereToReplaceList.size() < 2) {
				zenCodeArea.moveTo(caretPos + stepsToMove);
				
			}else if (addComment[0] && addComment[n - 1]) {
				zenCodeArea.selectRange(startOfSelection + 2, endOfSelection + stepsToMove);

			}else if (addComment[0] && addComment[n - 1] == false) {
				zenCodeArea.selectRange(startOfSelection + 2, endOfSelection + stepsToMove);
				
			}else if (addComment[0] == false && addComment[n - 1]) {
				zenCodeArea.selectRange(startOfSelection - 2, endOfSelection + stepsToMove + 2);
				
			}else {
				zenCodeArea.selectRange(startOfSelection - 2, endOfSelection + stepsToMove);
			}
		}	
		//Comment or uncomment from below and up
		if (topDown == false) {
			
			for (int i = whereToReplaceList.size() - 1; i >= 0; i--) {
				whereToReplace = whereToReplaceList.get(i);
				
				if (caretPos > length - 3) {
					zenCodeArea.insertText(caretPos, "	  ");
					zenCodeArea.moveTo(caretPos);
				}
				
				if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ")) {

					if (zenCodeArea.getText(whereToReplace, whereToReplace + 4).equals("// *")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						addComment[i] = false;
						
					}else {
						zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "  ");
						zenCodeArea.moveTo(caretPos);
						addComment[i] = false;
					}
					
				}else if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ") == false) {
					
					if (zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("//")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						addComment[i] = false;
						
						if (whereToReplace == caretPos) {
							zenCodeArea.moveTo(caretPos);
							
						}else if (whereToReplace + 1 == caretPos) {
							zenCodeArea.moveTo(caretPos - 1);
							
						}else {
							zenCodeArea.moveTo(caretPos - 2);
						}
							
					}else {
						zenCodeArea.insertText(whereToReplace, "//");
						zenCodeArea.moveTo(caretPos + 2);
						addComment[i] = true;
					}		
				}	

			if (addComment[0] && addComment[whereToReplaceList.size() - 1]) {
				zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
				endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) + 2,
				rowNumber, caretColumn + 2);
				
			}else if(addComment[0] && addComment[whereToReplaceList.size() - 1] == false) {
				zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
				endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) - 2,
				rowNumber, caretColumn + 2);
				
			}else if(addComment[0] == false && addComment[whereToReplaceList.size() - 1]) {
				zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
				endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) + 2,
				rowNumber, caretColumn - 2);
				
			}else {
				zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
				endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) - 2,
				rowNumber, caretColumn - 2);
				}
			}	
		}
	}
	
	/**
	 * Let's user choose .jar and .zip files and adds them to projects lib-folder and creates 
	 * build paths. Show dialog box if import failed or not.
	 * @param projectFile Project to import jar-files to
	 */
	public void chooseAndImportLibraries(ProjectFile projectFile) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select jar file to import");
		
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Libraries", "*.jar", "*.zip");
		fileChooser.getExtensionFilters().add(filter);

		List<File> jarFiles = fileChooser.showOpenMultipleDialog(stage);
		
		if (jarFiles != null) {
			boolean success = fileController.addInternalLibraries(jarFiles, projectFile);
			if (success) {
				DialogBoxes.informationDialog("Import complete", "Jar file(s) have successfully been imported to workspace");
			} else {
				DialogBoxes.errorDialog("Import failed", "Couldn't import jar file(s)", "An error occured while trying to import jar file(s)");
			}
		}
	}
	
	/**
	 * Opens up the project settings for specified project
	 * @param projectFile Project to open settings for
	 */
	public void showProjectProperties(ProjectFile projectFile) {
		pmc = new ProjectMetadataController(fileController, projectFile, isDarkMode, this);
		pmc.start();
	}
	
	public void openJREVersions() {
		JREVersionsController jvc = new JREVersionsController(true);
		jvc.start();
	}
	
	@FXML
	private void terminate() {
		if (process != null ) {
			process.destroy();
			
		}
	}
	
	public boolean isDarkmode() {
		return isDarkMode;
	}
	
	public void setDarkmode(boolean isDarkmode) {
		this.isDarkMode = isDarkmode;
	}
	
	
	
	
	public void closeConsoleComponent() {
		
		splitPane.setDividerPosition(0, 1.0);
	    consolePane.setMinHeight(0.0);
		consolePane.setVisible(false);
		consolePane.setDisable(true);
		splitPane.resize(splitPane.getWidth() + 2, splitPane.getHeight() + 2);
		
		Node divider = splitPane.lookup(".split-pane-divider");
	    if(divider!=null){
	        divider.setStyle("-fx-padding: 0");
	    }
		
	}
	
	public void openConsoleComponent() {
		
		consolePane.setVisible(true);
		consolePane.setDisable(false);
		splitPane.setDividerPosition(0, 0.85);
		consolePane.setMinHeight(34.0);
		
		Node divider = splitPane.lookup(".split-pane-divider");

		if(divider != null) {
			divider.setStyle("-fx-padding: 1");
		}
		splitPane.resize(splitPane.getWidth() + 2 , splitPane.getHeight() + 2);
		
		
		
	}
	
	
	
}
