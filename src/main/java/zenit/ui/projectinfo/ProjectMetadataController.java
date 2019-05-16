package main.java.zenit.ui.projectinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.filesystem.MetadataFileHandler;
import main.java.zenit.ui.DialogBoxes;
import main.java.zenit.zencodearea.ZenCodeArea;

public class ProjectMetadataController extends AnchorPane {

	private Stage propertyStage;

	private FileController fileController;

	private File projectFile;
	
	private boolean darkmode;

	private String directory;
	private String sourcepath;
	private String JREVersion;
	private String programArguments;
	private String VMArguments;

	private ArrayList<String> internalLibraries;
	private ArrayList<String> externalLibraries;

	private FileChooser.ExtensionFilter libraryFilter = new FileChooser.ExtensionFilter("Libraries", "*.jar", "*.zip");

	@FXML
	private Text directoryPath;
	@FXML
	private Text sourcepathPath;

	@FXML
	private ListView<String> internalLibrariesList;
	@FXML
	private ListView<String> externalLibrariesList;
	@FXML
	private ListView<String> runnableClasses;

	@FXML
	private TextArea taProgramArguments;
	@FXML
	private TextArea taVMArguments;

	@FXML
	private ComboBox<String> JREVersions;

	@FXML
	private Button addInternalLibrary;
	@FXML
	private Button removeInternalLibrary;
	@FXML
	private Button addExternalLibrary;
	@FXML
	private Button removeExternalLibrary;
	@FXML
	private Button save;
	@FXML
	private Button run;

	public ProjectMetadataController(FileController fc, File projectFile, boolean darkmode) {
		this.projectFile = projectFile;
		fileController = fc;
		this.darkmode = darkmode;
	}

	private void initialize() {

		if (directory != null) {
			directoryPath.setText(directory);
		}
		if (sourcepath != null) {
			sourcepathPath.setText(sourcepath);
		}

		updateLists();

		if (JREVersion != null) {
			JREVersions.getItems().add(JREVersion);
			JREVersions.getSelectionModel().select(JREVersion);
		}
		if (programArguments != null) {
			taProgramArguments.setText(programArguments);
		}
		if (VMArguments != null) {
			taVMArguments.setText(VMArguments);
		}

	}

	private void updateLists() {
		if (internalLibraries != null) {
			internalLibrariesList.getItems().removeAll(internalLibraries);
			internalLibrariesList.getItems().addAll(internalLibraries);
			internalLibrariesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		if (externalLibraries != null) {
			externalLibrariesList.getItems().removeAll(externalLibraries);
			externalLibrariesList.getItems().addAll(externalLibraries);
		}
	}

	public void start() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/projectInfo/ProjectMetadata.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();

			Scene scene = new Scene(root);

			propertyStage = new Stage();
			propertyStage.setResizable(false);
			propertyStage.setTitle(projectFile.getName() + " metadata");
			propertyStage.setScene(scene);

			if (decodeMetadata()) {
				initialize();
				ifDarkModeChanged(darkmode);
				propertyStage.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean decodeMetadata() {
		File metadata = null;
		File[] files = projectFile.listFiles();
		for (File file : files) {
			if (file.getName().equals(".metadata")) {
				metadata = file;
				break;
			}
		}
		//Check if metadata is missing
		if (metadata == null || !metadata.exists()) {
			int choice = DialogBoxes.twoChoiceDialog("Metadata missing", "Metadata missing from project",
					"It seems this is the first time you are using this project in Zenit. Do you"
							+ " want to generate a new metadata file about this project?",
					"Yes, generate", "No, close window");
			if (choice == 0 || choice == 2) {
				return false;
			} else {
				metadata = fileController.generateMetadata(projectFile);
			}
		}
		if (metadata != null) {
			try (BufferedReader br = new BufferedReader(new FileReader(metadata))) {
				String line = br.readLine();
				
				//check if metadata is outdated, missing Zenit Metadata header
				if (line != null && !line.equals("ZENIT METADATA")) {
					int choice = DialogBoxes.twoChoiceDialog("Metadata outdated", "Metadata must be updated",
							"It seems that you must update the metadata file. Do you"
									+ " want to update the metadata file about this project?",
							"Yes, update", "No, close window");
					if (choice == 1) {
						metadata = fileController.updateMetadata(metadata);
						return decodeMetadata();
					} else {
						return false;
					}
					
				//check if metadata is outdated, old version number
				} else if (line != null && line.equals("ZENIT METADATA")) {
					String version = br.readLine();
					String latestVersion = MetadataFileHandler.LATEST_VERSION;
					if (version.equals(latestVersion)) {
						line = br.readLine();
					} else {
						int choice = DialogBoxes.twoChoiceDialog("Metadata outdated", "Metadata must be updated",
								"It seems that you must update the metadata file. Do you"
										+ " want to update the metadata file about this project?",
								"Yes, update", "No, close window");
						if (choice == 1) {
							metadata = fileController.updateMetadata(metadata);
							return decodeMetadata();
						} else {
							return false;
						}
					}
				//check if metadata is corrupted
				} else {
					int choice = DialogBoxes.twoChoiceDialog("Metadata corrupted", "Metadata file is corrupted", 
							"It seems the metadata file for this project is corrupted. Do you want"
							+ " to delete current file and create a new one?", "Yes, create new",
							"No, close window");
					if (choice == 1 ) {
						fileController.deleteFile(metadata);
						metadata = fileController.generateMetadata(projectFile);
						return decodeMetadata();	
					} else {
						return false;
					}
				}

				//reads the different metadata info
				while (line != null) {
					if (line.equals("DIRECTORY")) {
						directory = br.readLine();
					} else if (line.equals("SOURCEPATH")) {
						sourcepath = br.readLine();
					} else if (line.equals("JRE VERSION")) {
						JREVersion = br.readLine();
					} else if (line.equals("PROGRAM ARGUMENTS")) {
						programArguments = br.readLine();
					} else if (line.equals("VM ARGUMENTS")) {
						VMArguments = br.readLine();
					} else if (line.equals("LIBRARIES")) {
						internalLibraries = new ArrayList<String>();
						externalLibraries = new ArrayList<String>();
						while (line != null) {
							line = br.readLine();
							if (line != null) {
								if (internalLibrary(line)) {
									internalLibraries.add(line);
								} else {
									externalLibraries.add(line);
								}
							}
						}
					}
					line = br.readLine();
				}

			} catch (IOException ex) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean internalLibrary(String libraryPath) {
		if (libraryPath.startsWith("lib")) {
			return true;
		} else {
			return false;
		}
	}

	@FXML
	private void addInternalLibrary() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(libraryFilter);

		List<File> selectedFiles = fc.showOpenMultipleDialog(propertyStage);
		if (selectedFiles != null) {
			boolean success = fileController.importJar(selectedFiles, projectFile);
			if (success) {
				decodeMetadata();
				updateLists();
			} else {
				DialogBoxes.errorDialog("Import failed", "Couldn't import internal libraries",
						"Failed to import internal libraries");
			}
		}
	}

	@FXML
	private void removeInternalLibrary() {
		List<String> selectedLibraries = internalLibrariesList.getSelectionModel().getSelectedItems();

		for (String library : selectedLibraries) {
			System.out.println(library);
		}
	}

	@FXML
	private void changeJREVersion() {
		System.out.println("Change JRE version");
	}

	@FXML
	private void addExternalLibrary() {
		System.out.println("Add external library");
	}

	@FXML
	private void removeExternalLibrary() {
		System.out.println("Remove external library");
	}

	@FXML
	private void save() {
		System.out.println("save");
	}

	@FXML
	private void run() {
		System.out.println("run");
	}
	
	public void ifDarkModeChanged(boolean isDarkMode) {
		var stylesheets = propertyStage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css").toExternalForm();
		var lightMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle-lm.css").toExternalForm();
		
		if (isDarkMode) {
			if (stylesheets.contains(lightMode)) {
				stylesheets.remove(lightMode);
			}
			
			stylesheets.add(darkMode);
		} else {
			if (stylesheets.contains(darkMode)) {
				stylesheets.remove(darkMode);
			}
			stylesheets.add(lightMode);
		}	
	}
}
