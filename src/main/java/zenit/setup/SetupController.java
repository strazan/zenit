package main.java.zenit.setup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import main.java.zenit.Zenit;
import main.java.zenit.filesystem.WorkspaceHandler;
import main.java.zenit.filesystem.jreversions.JREVersions;
import main.java.zenit.ui.DialogBoxes;

/**
 * Controller for the setup window of the application.
 * Let's user choose a workspace, add and remove JDKs and set a default JDK.
 * @author Alexander Libot
 *
 */
public class SetupController extends AnchorPane {
	
	private Stage stage;
	
	private File workspaceDat = new File("res/workspace/workspace.dat");
	private File JDKDat = new File("res/JDK/JDK.dat");
	private File defaultJDKDat = new File ("res/JDK/DefaultJDK.dat");
	
	private File workspaceFile;
	
	private final ToggleGroup tgGroup;
	
	private RadioButtonListener rbListener;
	
	@FXML ListView<String> jdkList;
	@FXML TextField workspacePath;
	@FXML RadioButton rb1;
	@FXML RadioButton rb2;
	@FXML ImageView logo;
	
	/**
	 * Creates a new controller for the setup page.
	 * Start graphics by calling {@link #start()}
	 */
	public SetupController() {
		//Init final variable
		tgGroup = new ToggleGroup();
		
		//Init dat files
		workspaceDat = new File("res/workspace/workspace.dat");
		JDKDat = new File("res/JDK/JDK.dat");
		defaultJDKDat = new File ("res/JDK/DefaultJDK.dat");
	}
	
	/**
	 * Initializes and displays the setup window graphics.
	 */
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/setup/Setup.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			//set up stage
			stage = new Stage();
			stage.setResizable(false);
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			
			//Init graphical components
			initialize();
			
			//display stage
			stage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes graphical components
	 */
	private void initialize() {
		
		//Set dark mode
		var stylesheets = stage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css")
				.toExternalForm();
		stylesheets.add(darkMode);
		
		//Load logo
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png")
				.toExternalForm()));
		logo.setFitWidth(55);
		
		//Load OS default JDKs if none are saved
		if (!JDKDat.exists()) {
			JREVersions.createNew();
		}
		
		//Load in set workspace if it exist
		if (workspaceDat.exists()) {	
			try {
				workspaceFile = WorkspaceHandler.readWorkspace();
				workspacePath.setText(workspaceFile.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		initRadioButtons();
		
		updateList();
	}
	
	/**
	 * Setup the radio buttons and put them in their default state
	 */
	private void initRadioButtons() {
		rb1.setToggleGroup(tgGroup);
		rb2.setToggleGroup(tgGroup);
		
		if (workspaceFile == null) {
			rb2.setSelected(true);
		} else {
			rb1.setSelected(true);
		}
		
		rbListener = new RadioButtonListener();
		tgGroup.selectedToggleProperty().addListener(rbListener);
	}
	
	/**
	 * Updates the JDK list by reading from file. Add JDK to res/JDK.dat before triggering
	 * Sorts the list.
	 */
	private void updateList() {
		//Init list of JDKs
		List<String> JDKs = JREVersions.readString();
		
		//Try to read the default JDK
		File defaultJDKFile = JREVersions.getDefaultJDKFile();
		if (defaultJDKFile != null) {
			String defaultJDKName = defaultJDKFile.getName();
			
			if (defaultJDKName != null && JDKs.contains(defaultJDKName)) {
				JDKs.remove(defaultJDKName);
				defaultJDKName += " [default]";
				JDKs.add(defaultJDKName);
			}
		}
	
		//Add to list
		jdkList.getItems().clear();
		jdkList.getItems().addAll(JDKs);
		
		//Sort
		jdkList.getItems().sort((o1,o2)->{
			return o1.compareTo(o2);
		});
	}
	
	/**
	 * 
	 */
	@FXML
	private void browse() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose a workspace");
		dc.setInitialDirectory(new File(System.getProperty("user.home")));
		File newWorkspace = dc.showDialog(stage);
		
		if (newWorkspace != null) {
			workspaceFile = newWorkspace;
			workspacePath.setText(workspaceFile.getPath());
			toggleRadiobutton(true);
		} else if (newWorkspace == null && workspacePath.getText().equals("")){
			toggleRadiobutton(false);
		}
	}
	
	@FXML
	private void addJDK() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose JDK to add");
		dc.setInitialDirectory(JREVersions.getJVMDirectory());
		File newJDK = dc.showDialog(stage);
		
		if (newJDK != null) {
			if (jdkList.getItems().contains(newJDK.getName())) {
				DialogBoxes.errorDialog("JDK already exist in list", "", "A JDK with that name"
						+ " already exist in the list.");
			} else {
				if (JREVersions.append(newJDK)) {
					updateList();
				} else {
					DialogBoxes.errorDialog("Not a valid JDK folder", "", "The chosen folder is not"
							+ " a valid JDK folder, must contain a java and javac executable");
				}
			}
		}
	}
	
	@FXML
	private void removeJDK() {
		String removeJDKName = jdkList.getSelectionModel().getSelectedItem();
		
		if (removeJDKName != null) {
			
			if (removeJDKName.endsWith(" [default]")) {
				DialogBoxes.errorDialog("Can't remove default JDK", "", "Can't remove the default "
						+ "JDK, choose another default JDK to remove this one");
				return;
			} else if (jdkList.getItems().size() == 1){
				int choice = DialogBoxes.twoChoiceDialog("Remove the last JDK from list", "",
						"There is only one JDK remaining in the list. Are you sure you want to "
						+ "remove it? At least one JDK is needed to run Zenit", "Yes, remove", 
						
						"No, keep it");
				if (choice == 0 || choice == 2) {
					return;
				}
			}
			
			String removeJDKPath = JREVersions.getFullPathFromName(removeJDKName);
			File removeJDKFile = new File(removeJDKPath);
			JREVersions.remove(removeJDKFile);
		
			updateList();
		} else {
			DialogBoxes.errorDialog("Choose JDK to remove", "", "Choose a JDK from the list to "
					+ "remove it");
		}	
	}
	
	@FXML
	private void setDefaultJDK() {
		String defaultJDKName = jdkList.getSelectionModel().getSelectedItem();
		
		if (defaultJDKName != null) {
			String defaultJDKPath = JREVersions.getFullPathFromName(defaultJDKName);
			File deaultJDKFile = new File(defaultJDKPath);
			JREVersions.setDefaultJDKFile(deaultJDKFile);
		
			updateList();
		} else {
			DialogBoxes.errorDialog("Choose JDK to make default", "", "Choose a JDK from the list to "
					+ "make it the default");
		}
	}
	
	@FXML
	private void quit() {
		System.exit(0);
	}
	
	@FXML
	private void done() {
		
		//Check if workspace input text has been updated since save
		boolean notSavedWorkspace = true;
		
		while (notSavedWorkspace) {
			if (tgGroup.getSelectedToggle().equals(rb1) && workspaceFile != null && 
					!workspaceFile.getPath().equals(workspacePath.getText())) {
				int choice = DialogBoxes.twoChoiceDialog("Save changes to workspace", "",
						"The changes to workspace has not been saved, would you like: " + 
						workspacePath.getText() + " to be your workspace?", "Yes", "No");
				if (choice == 1) {
					onEnter();
				} else {
					notSavedWorkspace = false;
				}
			} else {
				notSavedWorkspace = false;
			}
		}
		
		//Check if default workspace is selected
		if (tgGroup.getSelectedToggle().equals(rb2)) {
			String userPath = System.getProperty("user.home");
			String documentsPath = getDocumentsPath();
			File defaultWorkspace = new File(userPath + File.separator + documentsPath +
					"Zenit" + File.separator + "Default Workspace");
			if (!defaultWorkspace.exists()) {
				defaultWorkspace.mkdirs();
			}
			workspaceFile = defaultWorkspace;
		}
		
		if (!workspaceFile.exists() || !JDKDat.exists() || !defaultJDKDat.exists()) {
			DialogBoxes.errorDialog("Missing files", "", "Please enter the required information to"
					+ " launch Zenit");
		} else {
			WorkspaceHandler.createWorkspace(workspaceFile);
			stage.close();
		}
	}
	
	@FXML
	private void onEnter() {
		String input = workspacePath.getText();
		File file = new File(input);
		
		if (file.exists() && file.isDirectory()) {
			workspaceFile = file;
			toggleRadiobutton(true);
		} else if (file.exists() && !file.isDirectory()) {
			DialogBoxes.errorDialog("File selected", "", "You have selected a file as workspace. A"
					+ " workspace must be a directory");
			toggleRadiobutton(false);
		} else if (!file.exists()) {
			int choice = DialogBoxes.twoChoiceDialog("Folder doesn't exist", "", "The folder " + 
					input + " doesn't exist. Would you like to create it?", "Yes, create folder",
					"No, don't create folder");
			
			if (choice == 1) {
				if (file.mkdir()) {
					DialogBoxes.informationDialog("Folder created", "The folder " + input + 
							" is now created.");
					workspaceFile = file;
					toggleRadiobutton(true);
				} else {
					DialogBoxes.errorDialog("Folder couldn't be created", "", "The folder " +
							input + " couldn't be created. You can only create a folder in an"
									+ " existing folder");
					toggleRadiobutton(false);
				}
			} else {
				toggleRadiobutton(false);
			}
		}
	}
	
	/**
	 * Sets the path to document folder depending on current OS
	 * @return path from OS.home to documents folder
	 */
	private String getDocumentsPath() {
		String OS = Zenit.OS;
		if (OS.equals("Mac OS X")) {
			return "documents" + File.separator;
		} else if (OS.startsWith("Windows")) {
			return "Documents";
		} else if (OS.equals("Linux")) {
			return "Documents";
		} else {
			return null;
		}
	}
	
	/**
	 * Used to toggle a radio button without triggering the changed event
	 * @param toggleOwn {@code true} if "Own workspace" should be selected,
	 * otherwise {@code false}
	 */
	private void toggleRadiobutton(boolean toggleOwn) {
		tgGroup.selectedToggleProperty().removeListener(rbListener);
		if (toggleOwn) {
			rb2.setSelected(false);
			rb1.setSelected(true);
		} else {
			rb1.setSelected(false);
			rb2.setSelected(true);
		}
		tgGroup.selectedToggleProperty().addListener(rbListener);
	}
	
	private class RadioButtonListener implements ChangeListener<Toggle> {

		@Override
		public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (tgGroup.getSelectedToggle().equals(rb1)) {
            	if (workspacePath.getText().equals("")) {
            		browse();
            	} else {
            		onEnter();
            	}
            }
		}
	}
	
}
