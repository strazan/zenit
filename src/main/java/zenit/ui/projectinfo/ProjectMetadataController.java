package main.java.zenit.ui.projectinfo;

import java.io.File;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.filesystem.ProjectFile;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.filesystem.metadata.MetadataVerifier;
import main.java.zenit.ui.DialogBoxes;

/**
 * Window containing run and compile information about a project. Also ability to modify that
 * information.
 * 
 * @author Alexander
 *
 */
public class ProjectMetadataController extends AnchorPane {

	private Stage propertyStage;

	private FileController fileController;

	private ProjectFile projectFile;
	private Metadata metadata;
	
	private boolean darkmode;
	private boolean taUpdated = false;

	private RunnableClass[] runnableClasses;
	private RunnableClass selectedRunnableClass;
	
	private FileChooser.ExtensionFilter libraryFilter = new FileChooser.ExtensionFilter("Libraries", "*.jar", "*.zip");

	@FXML
	private ListView<String> directoryPathList;
	@FXML
	private ListView<String> sourcepathList;
	@FXML
	private ListView<String> internalLibrariesList;
	@FXML
	private ListView<String> externalLibrariesList;
	@FXML
	private ListView<String> runnableClassesList;

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

	/**
	 * Sets up new object. Use {@link #start()} to open window.
	 * @param fc FileController object
	 * @param projectFile The project to display information about
	 * @param darkmode {@code true} if dark mode is enabled
	 */
	public ProjectMetadataController(FileController fc, ProjectFile projectFile, boolean darkmode) {
		this.projectFile = projectFile;
		fileController = fc;
		this.darkmode = darkmode;
	}
	
	/**
	 * Opens new Project Info window.
	 */
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/projectInfo/ProjectMetadata.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			//set up stage
			propertyStage = new Stage();
			propertyStage.setResizable(false);
			propertyStage.setTitle(projectFile.getName() + " metadata preferences");
			propertyStage.setScene(scene);
			
			//gets metadata file
			File metadataFile = projectFile.getMetadata();
			if (metadataFile != null) {
				metadata = new Metadata(metadataFile);
			} else {
				metadata = null;
			}
			
			//Verifies metadata
			int verification = MetadataVerifier.verify(metadata);
			
			if (verification == MetadataVerifier.VERIFIED) {
				initialize();
				ifDarkModeChanged(darkmode);
				propertyStage.show();
				
			//If no metadata file is found
			} else if (verification == MetadataVerifier.METADATA_FILE_MISSING) {
				int returnCode = ProjectInfoErrorHandling.metadataMissing();
				if (returnCode == 1) {
					metadataFile = projectFile.addMetadata();
					metadata = new Metadata(metadataFile);
					initialize();
					ifDarkModeChanged(darkmode);
					propertyStage.show();
				}	
				
			//If metadata file is outdated
			} else if (verification == MetadataVerifier.METADATA_OUTDATED) {
				int returnCode = ProjectInfoErrorHandling.metadataOutdated();
				if (returnCode == 1) {
					metadata = fileController.updateMetadata(metadataFile);
					initialize();
					ifDarkModeChanged(darkmode);
					propertyStage.show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes variables with data from metadata-file
	 */
	private void initialize() {
		String directory = metadata.getDirectory();
		if (directory != null) {
			updateText(directoryPathList, directory);
		}
		String sourcepath = metadata.getSourcepath();
		if (sourcepath != null) {
			updateText(sourcepathList, sourcepath);
		}

		updateLists();

		String JREVersion = metadata.getJREVersion();
		if (JREVersion != null) {
			JREVersions.getItems().add(JREVersion);
			JREVersions.getSelectionModel().select(JREVersion);
		}
		
		taProgramArguments.setText("<select a runnable class>");
		taProgramArguments.setEditable(false);
		taVMArguments.setText("<select a runnable class>");
		taVMArguments.setEditable(false);
	}

	/**
	 * Updates the list with data from metadata-file
	 */
	private void updateLists() {
		internalLibrariesList.getItems().clear();
		String[] internalLibraries = metadata.getInternalLibraries();
		if (internalLibraries != null) {
			internalLibrariesList.getItems().clear();
			internalLibrariesList.getItems().addAll(internalLibraries);
			internalLibrariesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		
		externalLibrariesList.getItems().clear();
		String[] externalLibraries = metadata.getExternalLibraries();
		if (externalLibraries != null) {
			externalLibrariesList.getItems().addAll(externalLibraries);
			externalLibrariesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		
		runnableClasses = metadata.getRunnableClasses();
		if (runnableClasses != null) {
			for (RunnableClass runnableClass : runnableClasses) {
				runnableClassesList.getItems().add(runnableClass.getPath());
			}
		}	
	}
	
	//Settings
	
	/**
	 * Opens directory chooser to choose a new compile-directory directory
	 */
	@FXML
	private void changeDirectory() {
		int returnValue = DialogBoxes.twoChoiceDialog("Internal directory", "Internal directory",
				"Do you want the new directory to be internal or external?", "Internal", "External");
		boolean internal;
		if (returnValue == 1) {
			internal = true;
		} else if (returnValue == 2) {
			internal = false;
		} else {
			return;
		}
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(projectFile.getBin());
		dc.setTitle("Choose new directory");
		
		File directory = dc.showDialog(propertyStage);
		
		if (directory != null) {
			String directoryPath = fileController.changeDirectory(directory, projectFile, internal);
			updateText(directoryPathList, directoryPath);
		}
	}
	
	/**
	 * Opens directory chooser to choose a new source path directory
	 */
	@FXML
	private void changeSourcepath() {
		int returnValue = DialogBoxes.twoChoiceDialog("Internal sourcepath", "Internal sourcepath",
				"Do you want the new sourcepath to be internal or external?", 
				"Internal", "External");
		boolean internal;
		if (returnValue == 1) {
			internal = true;
		} else if (returnValue == 2) {
			internal = false;
		} else {
			return;
		}
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(projectFile.getSrc());
		dc.setTitle("Choose new sourcepath");
		
		File directory = dc.showDialog(propertyStage);
		
		if (directory != null) {
			String sourcepath = fileController.changeSourcepath(directory, projectFile, internal);
			updateText(sourcepathList, sourcepath);
		}
	}
	
	/**
	 * Changes the JREVersion
	 */
	@FXML
	private void changeJREVersion() {
		System.out.println("Change JRE version");
	}

	/**
	 * Tries to add files selected from file chooser to internal library list, copies files and
	 * adds build paths.
	 */
	@FXML
	private void addInternalLibrary() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(libraryFilter);

		List<File> selectedFiles = fc.showOpenMultipleDialog(propertyStage);
		if (selectedFiles != null) {
			boolean success = fileController.addInternalLibraries(selectedFiles, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getMetadataFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.addInternalLibraryFail();
			}
		}
	}

	/**
	 * Tries to removed selected items in internal library list, removes files and removes 
	 * build paths.
	 */
	@FXML
	private void removeInternalLibrary() {
		List<String> selectedLibraries = internalLibrariesList.getSelectionModel().getSelectedItems();
		
		if (selectedLibraries != null) {
			boolean success = fileController.removeInternalLibraries(selectedLibraries, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getMetadataFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.removeInternalLibraryFail();
			}
		}
	}

	/**
	 * Tries to add files selected from file chooser to external library list and adds build paths.
	 */
	@FXML
	private void addExternalLibrary() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(libraryFilter);

		List<File> selectedFiles = fc.showOpenMultipleDialog(propertyStage);
		if (selectedFiles != null) {
			boolean success = fileController.addExternalLibraries(selectedFiles, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getMetadataFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.addExternalLibraryFail();
			}
		}
	}

	/**
	 * Tries to remove the selected items in external library list and removes build paths.
	 */
	@FXML
	private void removeExternalLibrary() {
		List<String> selectedLibraries = externalLibrariesList.getSelectionModel().getSelectedItems();
		
		if (selectedLibraries != null) {
			boolean success = fileController.removeExternalLibraries(selectedLibraries, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getMetadataFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.removeExternalLibraryFail();
			}
		}
	}
	
	//Advanced settings

	/**
	 * Saves the text in text fields to metadata.
	 */
	@FXML
	private void save() {
		String pa = taProgramArguments.getText();
		String vma = taVMArguments.getText();
		
		selectedRunnableClass.setPaArguments(pa);
		selectedRunnableClass.setVmArguments(vma);
		
		metadata.setRunnableClasses(runnableClasses);
		boolean encoded = metadata.encode();
		
		if (encoded) {
			taUpdated = false;
			DialogBoxes.informationDialog("Arguments saved", "Arguments have been saved");
		} else {
			DialogBoxes.errorDialog(null, "Arguments not saved", "Arguments couldn't be saved");
		}	
	}

	/**
	 * Compiles and runs the selected runnable class
	 */
	@FXML
	private void run() {
		System.out.println("run");
	}
	
	/**
	 * Called when runnable class is changed in list.
	 * Checks if updates are saved, give option to save or discard changes.
	 * Loads program arguments and vm arguments to text fields.
	 */
	@FXML
	private void runnableClassChange() {
		if (taUpdated) {
			int choice = DialogBoxes.twoChoiceDialog("Arguments updated", "Arguments have been updated",
					"Would you like to save the updated arguments?", "Yes, save", "No, discard");
			if (choice == 1) {
				save();
			} else {
				taUpdated = false;
			}
		}
		
		selectedRunnableClass = getSelectedRunnableClass();
		if (selectedRunnableClass != null) {
			taProgramArguments.setText(selectedRunnableClass.getPaArguments());
			taProgramArguments.setEditable(true);
			taVMArguments.setText(selectedRunnableClass.getVmArguments());
			taVMArguments.setEditable(true);
		} else {
			taProgramArguments.setText("<select a runnable class>");
			taProgramArguments.setEditable(false);
			taVMArguments.setText("<select a runnable class>");
			taVMArguments.setEditable(false);
		}
	}
	
	/**
	 * Sets flag to true if called. Used to warn about runnable class switching if an unsaved
	 * update has been made
	 */
	@FXML
	private void argumentsChanged() {
		taUpdated = true;
	}
	
	//Help classes
	
	/**
	 * Returns the currently selected runnable class. If no class is selected returns null.
	 */
	private RunnableClass getSelectedRunnableClass() {
		String selected = runnableClassesList.getSelectionModel().getSelectedItem();
		
		for (RunnableClass runnableClass : runnableClasses) {
			if (runnableClass.getPath().equals(selected)) {
				return runnableClass;
			}
		}
		return null;
	}
	
	/**
	 * Changes css style depending on set light mode.
	 * @param isDarkMode true if dark mode is enabled
	 */
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
	
	private void updateText(ListView<String> list, String string) {
		list.getItems().clear();
		list.getItems().add(string);
	}
}
