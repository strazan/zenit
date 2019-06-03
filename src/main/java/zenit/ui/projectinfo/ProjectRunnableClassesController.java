package main.java.zenit.ui.projectinfo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import main.java.zenit.filesystem.FileController;
import main.java.zenit.filesystem.ProjectFile;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.ui.DialogBoxes;

public class ProjectRunnableClassesController extends AnchorPane {
	
	private Stage stage;
	private ProjectFile projectFile;
	private boolean darkmode;
	private FileController fc;
	
	@FXML private ImageView logo;
	@FXML private TreeView<String> treeView;
	@FXML private AnchorPane header;
	
    private double xOffset = 0;
    private double yOffset = 0;
	
	
	public ProjectRunnableClassesController(ProjectFile projectFile, boolean darkmode, FileController fc) {
		this.projectFile = projectFile;
		this.darkmode = darkmode;
		this.fc = fc;
	}
	
	/**
	 * Opens new Project Info window.
	 */
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/projectInfo/ProjectRunnableClasses.fxml"));
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
		logo.setFitWidth(55);
		
		RunnableClassTreeItem<String> root = new RunnableClassTreeItem<String>(
				projectFile.getSrc().getName(), projectFile.getSrc(), false);
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		populateTree(projectFile.getSrc());
		
		root.getChildren().sort((o1,o2)->{
			RunnableClassTreeItem<String> t1 = (RunnableClassTreeItem<String>) o1;
			RunnableClassTreeItem<String> t2 = (RunnableClassTreeItem<String>) o2;
			return (t1.getValue().compareTo(t2.getValue()));
		});
		
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
	
	private void populateTree(File root) {
		File[] children = root.listFiles();
		for (File file : children) {
			addNode(file, treeView.getRoot());
		}
	}
	
	private void addNode(File file, TreeItem<String> parent) {
		String name = file.getName();
		boolean runnable = false;
		if (file.getName().endsWith(".java") && fc.containMainMethod(file)) {
			name += " [runnable]";
			runnable = true;
		} else if (file.getName().endsWith(".java")) {
			name += " [not runnable]";
			runnable = false;
		}
		RunnableClassTreeItem<String> node = new RunnableClassTreeItem<String>(name, file, runnable);
		parent.getChildren().add(node);
		
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File f : children) {
				addNode(f, node);
			}
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
	
	@FXML
	private void add() {
		RunnableClassTreeItem<String> item = (RunnableClassTreeItem<String>) 
				treeView.getSelectionModel().getSelectedItem();
		
		if (item.isRunnable()) {
			Metadata metadata = new Metadata(projectFile.getMetadata());
			String src = projectFile.getSrc().getPath();
			String filePath = item.getFile().getPath();
			filePath = filePath.replaceFirst(Matcher.quoteReplacement(src + File.separator), "");
			RunnableClass rc = new RunnableClass(filePath);
			metadata.addRunnableClass(rc);
			metadata.encode();
			close();
		} else {
			DialogBoxes.errorDialog("Class not runnable", "", "Select a class that is runnable "
					+ "(contains a main-method");
		}
		
	}
	
	@FXML
	private void close() {
		stage.close();
	}

}
