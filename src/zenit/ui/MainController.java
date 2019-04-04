package zenit.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import zenit.ConsoleRedirect;
import zenit.console.ConsoleArea;
import zenit.filesystem.FileController;
import zenit.javacodeCompiler.JavaSourceCodeCompiler;
import zenit.textFlow.ZenCodeArea;
import zenit.ui.tree.FileTree;
import zenit.ui.tree.FileTreeItem;
import zenit.ui.tree.TreeClickListener;
import zenit.ui.tree.TreeContextMenu;

/**
 * The controller part of the main GUI.
 * @author Pontus Laos, Oskar Molander, Alexander Libot
 *
 */
public class MainController {
	private Stage stage;
	private HashMap<Tab, File> currentlySelectedFiles;
	private FileController fileController;

	@FXML
	private ConsoleArea caConsole;

	@FXML
	private MenuItem newFile;
	
	@FXML
	private MenuItem newProject;

	@FXML
	private MenuItem openFile;

	@FXML
	private MenuItem saveFile;
	
	@FXML
	private MenuItem changeWorkspace;

	@FXML
	private TabPane tabPane;

	@FXML
	private TreeView<String> treeView;
	
	@FXML 
	private FontIcon iconRun;

	@FXML 
	private FontIcon iconStop;
	
	@FXML
	private Button btnRun;
	
	@FXML 
	private Button btnStop;

	/**
	 * Setter for FileController instance. Used to access the file system.
	 */
	public void setFileController(FileController fileController) {
		this.fileController = fileController;
	}
	
	/**
	 * Performs initialization steps when the controller is set.
	 * @param stage The stage to run the initialization on.
	 */
	public void initialize(Stage stage) {
		this.stage = stage;
		currentlySelectedFiles = new HashMap<>();

		new ConsoleRedirect(caConsole);
		
		initTree();
	}
	
	/**
	 * Initializes the treeview. Creates a root node from the workspace-file in the fileController
	 * class. Calls FileTree-method to add all files in the workspace folder to the tree. Creates a
	 * TreeContextMenu for displaying when right clicking nodes in the tree and an event handler
	 * for clicking nodes in the tree.
	 */
	private void initTree() {
		FileTreeItem<String> rootItem = new FileTreeItem<String>(fileController.getWorkspace(), "Workspace");
		File workspace = fileController.getWorkspace();
		if (workspace != null) {
			zenit.ui.tree.FileTree.createNodes(rootItem, workspace);
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
	 * @param parent The parent folder of the file to be created.
	 * @param typeCode The type of code snippet that should be implemented in the file.
	 * Use constants from {@link zenit.filesystem.helpers.CodeSnippets CodeSnippets} class.
	 * @return The File if created, otherwise null.
	 */
	public File createFile(File parent, int typeCode) {
		File file = null;
		String className = DialogBoxes.inputDialog(null, "New file", "Create new file", 
				"Enter new file name", "File name");
		if (className != null) {
			String filepath = parent.getPath() + "/" + className;
			file = new File(filepath);
			
			file = fileController.createFile(file, typeCode);
			
			openFile(file);
		}
		return file;
	}

	/**
	 * Grabs the text from the currently selected Tab and writes it to the currently selected file.
	 * If no file selected, opens a file chooser for selection of file to overwrite.
	 * @param event
	 */
	@FXML
	public void saveFile(Event event) {
		try {
			Tab selectedTab = getSelectedTab();
			AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
			ZenCodeArea zenCodeArea = (ZenCodeArea) anchorPane.getChildren().get(0);
			String content = zenCodeArea.getText();
			
			if (currentlySelectedFiles.containsKey(selectedTab)) {
				fileController.writeFile(currentlySelectedFiles.get(selectedTab).getAbsoluteFile(), content);
			} else {
				File newFile = chooseFile();
				newFile = fileController.createFile(newFile, content);
				currentlySelectedFiles.put(selectedTab, newFile);
				FileTreeItem<String> newNode = new FileTreeItem<String>(newFile, newFile.getName());
				treeView.getRoot().getChildren().add(newNode);
				Tab tab = getSelectedTab();
				tab.setText(newFile.getName());
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();
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
	 * @param event
	 */
	@FXML
	public void newFile(Event event) {
		addTab();
	}

	/**
	 * Opens a file chooser and tries to read the file's 
	 * name and content to the currently selected tab.
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
	 * Tries to open the content of a file into a new tab using the FileController instance.
	 * If tab containing file-content is already open, switches to that tab.
	 * @param file The file which content to be opened.
	 */
	public void openFile(File file) {
		if (file != null && !currentlySelectedFiles.containsValue(file)) {
			
			Tab selectedTab = addTab();
			AnchorPane anchorPane = (AnchorPane) selectedTab.getContent();
			ZenCodeArea zenCodeArea = (ZenCodeArea) anchorPane.getChildren().get(0);

			currentlySelectedFiles.put(selectedTab, file);

			String fileContent = fileController.readFileContent(file);

			selectedTab.setText(file.getName());
			zenCodeArea.replaceText(fileContent);
		} else if (file != null && currentlySelectedFiles.containsValue(file)) { //Tab already open
			var tabs = tabPane.getTabs();
			File tempFile;
			for (Tab tab : tabs) {
				 tempFile = currentlySelectedFiles.get(tab);
				if (file.equals(tempFile)) {
					tabPane.getSelectionModel().select(tab);
					break;
				}
			}
		}
	}
	
	/**
	 * Method for renaming a file or folder. Opens a new input dialog for input of new name.
	 * Renames the tab text if file is in an open tab.
	 * @param file The file to rename.
	 * @return
	 */
	public File renameFile(File file) {
		File newFile = null;
		String newName = DialogBoxes.inputDialog(null, "New name", "Rename file", "Enter a new name", "new name");
		if (newName != null) {
			newFile = fileController.renameFile(file, newName);
			var tabs = tabPane.getTabs();
			for (Tab tab : tabs) {
				if (tab.getText().equals(file.getName())) {
					tab.setText(newName);
					currentlySelectedFiles.put(tab, newFile);
					break;
				}
			}
		}
		return newFile;
	}
	
	/**
	 * Tries to delete a file or folder.
	 * @param file The file to be deleted.
	 */
	public void deleteFile(File file) {
		fileController.deleteFile(file);
		//TODO Remove open tab aswell
	}
	
	/**
	 * Opens a input dialog to choose project name and then creates a new project with that name
	 * in the selected workspace folder
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
	 * If the file of the current tab is a .java file if will be compiled, into the same
	 * folder/directory, and the executed with only java standard lib.
	 */
	public void compileAndRun() {
		File file = currentlySelectedFiles.get(getSelectedTab());
		//saveFile(null);
		try {
			if (file != null) {
				JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler();
				compiler.compileAndRunJavaFileWithoutPackage(file, file.getParent());
//				compiler.compileAndRunJavaFileInPackage(file, file.getParent());
			}
		} catch (Exception e){
			 System.out.println("print");
			e.printStackTrace();
			
			// TODO: handle exception
		}
	}

	/**
	 * Creates a new tab with a {@link zenit.textFlow.ZenCodeArea ZenCodeArea}  filling it, adds it to the TabPane, and focuses on it.
	 * @param onClick The Runnable to run when the tab should be closed.
	 * @return The new Tab.
	 */
	public Tab addTab() {
		Tab tab = new Tab("Untitled");
		AnchorPane anchorPane = new AnchorPane();
		ZenCodeArea zenCodeArea = new ZenCodeArea();

		anchorPane.getChildren().add(zenCodeArea);
		tab.setContent(anchorPane);
//		zenCodeArea.setStyle("-fx-font-family: monospace");

		AnchorPane.setTopAnchor(zenCodeArea, 0.0);
		AnchorPane.setRightAnchor(zenCodeArea, 0.0);
		AnchorPane.setBottomAnchor(zenCodeArea, 0.0);
		AnchorPane.setLeftAnchor(zenCodeArea, 0.0);

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
	
	/**
	 * Clears the text from console window.
	 */
	@FXML
	private void clearConsole() {
		caConsole.clear();
	}
	
	
	/**
	 * Fire this on mouse events (Mouse exit, enter, click etc) for buttons and other components.
	 * Call this function on for example btnRun. ( btnRun onMouseExit="#onMouseEvent" )
	 * @param MouseEvent 
	 */
	
	@FXML
	private void onMouseEvent(MouseEvent e) {
	
		Button sourceButton = (Button) e.getSource();
		
		if(e.getEventType() == MouseEvent.MOUSE_ENTERED) {
			if(sourceButton.equals(btnRun)) {
				lightenIconColor(iconRun);
			}
			else if(sourceButton.equals(btnStop)) {
				lightenIconColor(iconStop);
			}
		}
		else if(e.getEventType() == MouseEvent.MOUSE_EXITED) {
			if(sourceButton.equals(btnRun)) {
				iconRun.setIconColor(Color.GREEN);
			}
			else if(sourceButton.equals(btnStop)) {
				iconStop.setIconColor( Color.DARKRED);
			}
		}
	}
	
	
	/*
	 * Takes a fontIcon and sets the iconColor to a brighter colour
	 * @param FontIcon
	 */
	private void lightenIconColor(FontIcon icon) {	
		icon.setIconColor( ((Color) icon.getIconColor()).brighter());
	}
}
