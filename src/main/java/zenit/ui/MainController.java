package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.zenit.ConsoleRedirect;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.filesystem.WorkspaceHandler;
import main.java.zenit.javacodecompiler.JavaSourceCodeCompiler;
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
public class MainController extends VBox {
	private Stage stage;
	private FileController fileController;

	@FXML
	private TextArea taConsole;

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

	/**
	 * Loads a file Main.fxml, sets this MainController as its Controller, and loads it. 
	 */
	public MainController(Stage s) {
		this.stage = s;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/ui/Main.fxml"));

			/*
			 * TODO Test if you like this idea. Saves and opens a local File-instance of your 
			 * selected workspace. Only prompts when unset and can be changed from within gui
			 * Alex
			 */
			File workspace = WorkspaceHandler.readWorkspace();
			FileController fileController = new FileController(workspace);

			setFileController(fileController);
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
		new ConsoleRedirect(taConsole);		
		btnRun.setPickOnBounds(true);
		initTree();
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
	
	public void commentsShortcutsTrigger() {
	FileTab selectedTab = getSelectedTab();
		
		if (selectedTab != null) {
			selectedTab.commentsShortcutsTrigger();
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
		FileTab tab = getSelectedTab();
		File file = tab.getFile();

		if (file == null) {
			file = chooseFile();
		}

		boolean didWrite = fileController.writeFile(file, tab.getFileText());

		if (didWrite) {
			FileTreeItem<String> newNode = new FileTreeItem<String>(file, file.getName(), 0);

			if (!treeView.getRoot().getChildren().stream()
					.anyMatch(n -> n.getValue().equals(newNode.getFile().getName()))) {
				treeView.getRoot().getChildren().add(newNode);
			}

			tab.update(file);
		} else {
			System.out.println("Did not write.");
		}

		return didWrite;
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
		String newName = DialogBoxes.inputDialog(null, "New name", "Rename file", "Enter a new name", "new name");
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
	 * Opens a input dialog to choose project name and then creates a new project
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
		File file = getSelectedTab().getFile();
		File projectFile = getMetadataFile(file);
		saveFile(null);

		try {
			JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler();
			if (file != null && projectFile != null) {
				compiler.compileAndRunJavaFileInPackage(file, projectFile);
			} else if (file != null) {
				compiler.compileAndRunJavaFileWithoutPackage(file, file.getParent());
			}
		} catch (Exception e) {
			e.printStackTrace();

			// TODO: handle exception
		}
	}
	
	/**
	 * Switches between dark- and light mode depending on what is selected in the application's
	 * 'Dark Mode'-checkbox.
	 * @param event
	 * @author Pontus Laos
	 */
	@FXML
	private void darkModeChanged(Event event) {
		boolean isDarkMode = cmiDarkMode.isSelected();
		var stylesheets = stage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/mainStyle.css").toExternalForm();
		var darkModeKeywords = ZenCodeArea.class.getResource("/zenit/ui/keywords.css").toExternalForm();
		var lightModeKeywords = ZenCodeArea.class.getResource("/zenit/ui/keywords-lm.css").toExternalForm();
		
		if (isDarkMode) {
			stylesheets.add(darkMode);
			
			if (stylesheets.contains(lightModeKeywords)) {
				stylesheets.remove(lightModeKeywords);
			}
			stylesheets.add(darkModeKeywords);
		} else {
			// Currently the Light Mode is the default CSS.
			stylesheets.remove(darkMode);
			
			if (stylesheets.contains(darkModeKeywords)) {
				stylesheets.remove(darkModeKeywords);
			}
			stylesheets.add(lightModeKeywords);
		}
	}

	public static File getMetadataFile(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File entry : files) {
				if (entry.getName().equals(".metadata")) {
					return entry.getParentFile();
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
		FileTab tab = new FileTab();
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
	 * Clears the text from console window.
	 */
	@FXML
	private void clearConsole() {
		taConsole.clear();
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
	 * Tries to import a folder.
	 * Displays a directory chooser and copies the selected folder into the current workspace
	 * using {@link main.java.zenit.filesystem.FileController#importProject(File) importProject(File)}
	 * Displays an error or information dialog to display the result.
	 */
	@FXML
	public void importProject() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select project to import");
		File source = directoryChooser.showDialog(stage);
		
		if (source != null) {
			File target = fileController.importProject(source);
			if (target == null) {
				DialogBoxes.errorDialog("Import failed", "Couldn't import project", 
						"An error occured while trying to import project");
			} else {
				FileTree.createParentNode((FileTreeItem<String>) treeView.getRoot(), target);
				DialogBoxes.informationDialog("Import complete", "Project is imported to workspace");
			}
		}
	}

	/**
	 * If there isn't a comment at the start of the line the method comments 
	 * and if there is a comment the method removes it.
	 * 
	 * @author Fredrik Eklundh
	 */
	public void commentAndUncomment() {

		ZenCodeArea zenCodeArea = getSelectedTab().getZenCodeArea();
		
//		String toReplace = zenCodeArea.getText(whereToReplace, whereToReplace + 2);
//		
//		System.out.println("carretPos" + carretPos);
//		System.out.println("carretCol" + carretColumn);
//		System.out.println("whereToReplace" + whereToReplace);
//		
//		System.out.println(toReplace);
//		System.out.println("row number " + rowNumber);
//		System.out.println("paragraph length " + paragraphLength);
//		
//		if(paragraphLength == 0) {
//			zenCodeArea.replaceText(whereToReplace, whereToReplace, "//");
//			zenCodeArea.moveTo(carretPos + 2);
//			
//		}else if(paragraphLength == 1) {
//			if(zenCodeArea.getText(whereToReplace, whereToReplace + 1).equals ("	")) {
//			zenCodeArea.replaceText(whereToReplace, whereToReplace + 1, "//      ");
//			zenCodeArea.moveTo(carretPos + 7);
//			
//			}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 1).equals("}")) {
//				zenCodeArea.replaceText(whereToReplace, whereToReplace + 1, "//}");
//				zenCodeArea.moveTo(carretPos + 2);
//			}
//			
//		}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("  ")) {
//			System.out.println("yes");
//			zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//");
//			zenCodeArea.moveTo(carretPos);
//			
//		}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("	p")) {
//			System.out.println("1 tab och p");
//			zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//      p");
//			zenCodeArea.moveTo(carretPos + 7);
//			
//		}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("	}")) {
//			System.out.println("1 tab och }");
//			zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//      }");
//			zenCodeArea.moveTo(carretPos + 7);
//			
//		}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("	@")) {
//			System.out.println("1 tab och @");
//			zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//      @");
//			zenCodeArea.moveTo(carretPos + 7);
//			
//		}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("		")) {
//			System.out.println("2 tabs 2 platser");
//			zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//              ");
//			zenCodeArea.moveTo(carretPos + 14);
//			

		
		int carretPos = zenCodeArea.getCaretPosition();
		
		int carretColumn = zenCodeArea.getCaretColumn();
		
		int length = zenCodeArea.getLength();
		
		int whereToReplace = carretPos - carretColumn;
		
		int rowNumber = zenCodeArea.getCurrentParagraph();
		
		int paragraphLength = zenCodeArea.getParagraphLength(rowNumber);
		
		List<Integer> whereToReplaceList = new ArrayList<>();
		
		IndexRange zen = zenCodeArea.getSelection();
		
		int endOfSelection = zen.getEnd();
		int startOfSelection = zen.getStart();
		
		boolean normal = true;
		
		int n = 1;
		
		whereToReplaceList.add(whereToReplace);
		
		if (carretPos == endOfSelection && whereToReplace > startOfSelection) {
			normal = true;
			do {
				
				whereToReplace = whereToReplace - 1 - zenCodeArea.getParagraphLength(rowNumber - n);
				n++;
				whereToReplaceList.add(whereToReplace);
				
			}while (whereToReplace > startOfSelection);
		}
		
		if (carretPos == startOfSelection && whereToReplace + paragraphLength < endOfSelection) {
			normal = false;
			do {
				
				whereToReplace = whereToReplace + 1 + zenCodeArea.getParagraphLength(rowNumber + n - 1);
				n++;
				whereToReplaceList.add(whereToReplace);
				
			}while (whereToReplace + zenCodeArea.getParagraphLength(rowNumber + n - 1) < endOfSelection);

		}

		
		if (normal == true) {
			
			int stepsToMove = 0;
			
			for (int i = 0; i < n; i++) {
				whereToReplace = whereToReplaceList.get(i);
				
				if (carretPos > length - 3) {
					zenCodeArea.insertText(carretPos, "	  ");
//					zenCodeArea.moveTo(carretPos);
				}
				
				if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ")) {

					if (zenCodeArea.getText(whereToReplace, whereToReplace + 4).equals("// *")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						stepsToMove = stepsToMove - 2;
					}else {
						zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "  ");
//						zenCodeArea.moveTo(carretPos);
					}
					
				}else if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ") == false) {
					
					if (zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("//")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						
						if (whereToReplace == carretPos) {
//							zenCodeArea.moveTo(carretPos);
							
						}else if (whereToReplace + 1 == carretPos) {
//							zenCodeArea.moveTo(carretPos - 1);
							stepsToMove--;
							
						}else {
							stepsToMove = stepsToMove - 2;
//							zenCodeArea.moveTo(carretPos - 2);
						}
					
					}else if(zenCodeArea.getText(whereToReplace, whereToReplace + 4).equals("    ")) {
						zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//");
						
					}else {
						zenCodeArea.insertText(whereToReplace, "//");
						stepsToMove = stepsToMove + 2;
					}		
				}	
			}
			
			zenCodeArea.moveTo(carretPos + stepsToMove);
		}
		
		if (normal == false) {
			
			for (int i = whereToReplaceList.size() - 1; i >= 0; i--) {
				whereToReplace = whereToReplaceList.get(i);
				
				if (carretPos > length - 3) {
					zenCodeArea.insertText(carretPos, "	  ");
					zenCodeArea.moveTo(carretPos);
				}
				
				if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ")) {

					if (zenCodeArea.getText(whereToReplace, whereToReplace + 4).equals("// *")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						
					}else {
						zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "  ");
						zenCodeArea.moveTo(carretPos);
					}
					
				}else if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ") == false) {
					
					if (zenCodeArea.getText(whereToReplace, whereToReplace + 2).equals("//")) {
						zenCodeArea.deleteText(whereToReplace, whereToReplace + 2);
						
						if (whereToReplace == carretPos) {
							zenCodeArea.moveTo(carretPos);
							
						}else if (whereToReplace + 1 == carretPos) {
							zenCodeArea.moveTo(carretPos - 1);
							
						}else {
							zenCodeArea.moveTo(carretPos - 2);
						}
							
					}else {
						zenCodeArea.insertText(whereToReplace, "//");
						zenCodeArea.moveTo(carretPos + 2);
					}		
				}	
			}
		}
	}
}
