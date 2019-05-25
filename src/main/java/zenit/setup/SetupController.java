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

public class SetupController extends AnchorPane {
	
	private Stage stage;
	
	private File workspace = new File("res/workspace/workspace.dat");
	private File JDK = new File("res/JDK/JDK.dat");
	private File defaultJDK = new File ("res/JDK/DefaultJDK.dat");
	
	private File workspaceFile;
	
	private final ToggleGroup tgGroup;
	
	@FXML ListView<String> jdkList;
	@FXML TextField workspacePath;
	@FXML RadioButton rb1;
	@FXML RadioButton rb2;
	@FXML ImageView logo;
	
	public SetupController() {
		tgGroup = new ToggleGroup();
	}
	
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
			
			
			initialize();
			
			stage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initialize() {	
		
		var stylesheets = stage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css").toExternalForm();
		stylesheets.add(darkMode);
		
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
		logo.setFitWidth(55);
		
		if (!JDK.exists()) {
			JREVersions.createNew();
		}
		if (workspace.exists()) {
			
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
	
	private void initRadioButtons() {
		rb1.setToggleGroup(tgGroup);
		rb2.setToggleGroup(tgGroup);
		
		if (workspaceFile == null) {
			rb2.setSelected(true);
		} else {
			rb1.setSelected(true);
		}
		
		
		tgGroup.selectedToggleProperty().addListener(new RadioButtonListener());
	}
	
	private void updateList() {

		List<String> JDKs = JREVersions.readString();
		File defaultJDKFile = JREVersions.getDefaultJDKFile();
		if (defaultJDKFile != null) {
			String defaultJDKName = defaultJDKFile.getName();
			
			if (defaultJDKName != null && JDKs.contains(defaultJDKName)) {
				JDKs.remove(defaultJDKName);
				defaultJDKName += " [default]";
				JDKs.add(defaultJDKName);
			}
		}
	
		jdkList.getItems().clear();
		jdkList.getItems().addAll(JDKs);
		
		jdkList.getItems().sort((o1,o2)->{
			return o1.compareTo(o2);
		});
	}
	
	@FXML
	private void browse() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose a workspace");
		dc.setInitialDirectory(new File(System.getProperty("user.home")));
		File newWorkspace = dc.showDialog(stage);
		
		if (newWorkspace != null) {
			workspace = newWorkspace;
			workspacePath.setText(workspace.getPath());
		}
		
	}
	
	@FXML
	private void addJDK() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose JDK to add");
		dc.setInitialDirectory(JREVersions.getJVMDirectory());
		File newJDK = dc.showDialog(stage);
		

		if (JREVersions.append(newJDK)) {
			updateList();
		} else {
			DialogBoxes.errorDialog("Not a valid JDK folder", "", "The chosen folder is not"
					+ " a valid JDK folder, must contain a java and javac executable");
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
			DialogBoxes.errorDialog("Choose JDK to remove", "", "Choose a JDK from the list to "
					+ "remove it");
		}
	}
	
	@FXML
	private void quit() {
		System.exit(0);
	}
	
	@FXML
	private void done() {
		
		if (tgGroup.getSelectedToggle().equals(rb2)) {
			String userPath = System.getProperty("user.home");
			String documentsPath = getDocumentsPath();
			File defaultWorkspace = new File(userPath + File.separator + documentsPath +
					"Zenit" + File.separator + "Default Workspace");
			if (!defaultWorkspace.exists()) {
				defaultWorkspace.mkdirs();
			}
			workspace = defaultWorkspace;
		}
		
		if (!workspace.exists() || !JDK.exists() || !defaultJDK.exists()) {
			DialogBoxes.errorDialog("Missing files", "", "Please enter the required information to"
					+ " launch Zenit");
		} else {
			WorkspaceHandler.createWorkspace(workspace);
			stage.close();
		}
	}
	
	private String getDocumentsPath() {
		String OS = Zenit.OS;
		if (OS.equals("Mac OS X")) {
			return "documents" + File.separator;
		} else if (OS.equals("Windows")) {
			return "my documents" + File.separator;
		} else if (OS.equals("Linux")) {
			return "";
		} else {
			return null;
		}
	}
	
	private class RadioButtonListener implements ChangeListener<Toggle> {

		@Override
		public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (tgGroup.getSelectedToggle().equals(rb1)) {
            	browse();
            }
		}
	}
}
