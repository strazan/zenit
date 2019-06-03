package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NewFileController extends AnchorPane {
	
	private Stage stage;
	private boolean darkmode;
	
	@FXML private ImageView logo;
	@FXML private AnchorPane header;
	@FXML private ListView<String> filepath;
	@FXML private ComboBox<String> fileEnding;
	@FXML private TextField tfName;
	
    private double xOffset = 0;
    private double yOffset = 0;
    
    private File workspace;
    private File newFile;
	
	
	public NewFileController(File workspace, boolean darkmode) {
		this.workspace = workspace;
		this.darkmode = darkmode;
	}
	
	/**
	 * Opens new Project Info window.
	 */
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/NewFile.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			//set up stage
			stage = new Stage();
			stage.setResizable(false);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(scene);
			
			initialize();
			ifDarkModeChanged(darkmode);
			stage.showAndWait();
				
		} catch (IOException e) {
			
		}

	}

	private void initialize() {
		
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
		logo.setFitWidth(45);
		
		filepath.getItems().clear();
		filepath.getItems().add(workspace.getPath());
		filepath.getSelectionModel().selectFirst();
		
		fileEnding.getItems().add(".txt");
		fileEnding.getItems().add(".java");
		fileEnding.getSelectionModel().select(".java");
		
	    header.setOnMousePressed(new EventHandler<MouseEvent>() {
	    	   @Override
	    	   public void handle(MouseEvent event) {
	    	       xOffset = event.getSceneX();
	    	       yOffset = event.getSceneY();
	    	   }
	    	});

	    	//move around here
	    header.setOnMouseDragged(new EventHandler<MouseEvent>() {
	    	   @Override
	    	   public void handle(MouseEvent event) {
	    	       stage.setX(event.getScreenX() - xOffset);
	    	       stage.setY(event.getScreenY() - yOffset);
	    	   }
	    	});
	}
	
	@FXML
	private void create() {
		String filename = tfName.getText();
		
		if (!filename.equals("")) {
			filename += fileEnding.getSelectionModel().getSelectedItem();
			
			String filePath = this.filepath.getSelectionModel().getSelectedItem() + File.separator
					+ filename;
			newFile = new File(filePath);
			try {
				if (!newFile.createNewFile()) {
					DialogBoxes.errorDialog("File name already exist", "", "A file with the name "
				+ filename + " already exist. Please input a different name.");
					newFile = null;
				}
			} catch (IOException e) {
				DialogBoxes.errorDialog("Couldn't create new file", "", "Couldn't create new file");
				newFile = null;
			}
			
			stage.close();
		} else {
			DialogBoxes.errorDialog("No name selected", "", "No name has been given to the new file"
					+ ". Please input a new name to create file.");
		}
		
	}
	
	@FXML
	private void cancel() {
		stage.close();
	}
	
	@FXML
	private void browse() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(workspace);
		dc.setTitle("Select directory to create new file in");
		File chosen = dc.showDialog(stage);
		
		if (chosen != null) {
			filepath.getItems().clear();
			filepath.getItems().add(chosen.getPath());
			filepath.getSelectionModel().selectFirst();
		}
	}
	
	
	public void ifDarkModeChanged(boolean isDarkMode) {
		var stylesheets = stage.getScene().getStylesheets();
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
	
	public File getNewFile() {
		return newFile;
	}

}
