package zenit.ui;

import java.io.File;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.skin.TabPaneSkin;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import zenit.FileHandler;

/**
 * A Tab extension that holds a File.
 * @author Pontus Laos
 *
 */
public class FileTab extends Tab {
	private File initialFile;
	private File file;
	private String initialTitle;
	
	private TextArea textArea;
	
	private boolean hasChanged;
	
	/**
	 * Constructs a new FileTab without a file, setting the title to "Untitled".
	 */
	public FileTab() {
		textArea = new TextArea();
		initialTitle = "Untitled";
		
		initializeUI();
	}
	
	/**
	 * Initializes the UI. Attaches an AnchorPane with a TextArea that fills it, 
	 * and sets additional data.
	 */
	private void initializeUI() {
		AnchorPane anchorPane = new AnchorPane();	
		AnchorPane.setTopAnchor(textArea, 0.0);
		AnchorPane.setRightAnchor(textArea, 0.0);
		AnchorPane.setBottomAnchor(textArea, 0.0);
		AnchorPane.setLeftAnchor(textArea, 0.0);

		/**
		 * Returns the text in the Tab's TextArea.
		 * @return A String that holds the text in the Tab.
		 */
//		public String getFileText() {
//			return textArea.getText();
//		}
		anchorPane.getChildren().add(textArea);
		textArea.setStyle("-fx-font-family: monospace");
		
		setContent(anchorPane);
		setOnCloseRequest(event -> {
	        if (hasChanged) {
	        	showConfirmDialog(event);
	        }
		});
		
		setText(initialTitle);
		
		textArea.textProperty().addListener((observable, oldText, newText) -> {
			String initialFileContent = FileHandler.readFile(initialFile);
			
			hasChanged = !initialFileContent.equals(newText);
			updateUI();
		});
		
		Platform.runLater(textArea::requestFocus);
	}
	
	public void close() {
		getOnCloseRequest().handle(new Event(this, this, Tab.TAB_CLOSE_REQUEST_EVENT));
	}
	
	/**
	 * Updates the title of the tab.
	 */
	private void updateUI() {
		if (hasChanged) {
			setText(initialTitle + " *");
		}
		else {
			setText(initialTitle);
		}
	}
	
	/**
	 * Sets the file to the given one, and updates the UI.
	 * @param file The File to set.
	 */
	private void update(File file) {
		setFile(file, false);
		hasChanged = false;
		updateUI();
	}
	
	/**
	 * Returns the attached File.
	 * @return The File.
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Sets the File to the given one.
	 * @param file The File to set.
	 * @param shouldSetContent Whether or not to read the File and 
	 * set the Tab's text content to it.
	 */
	public void setFile(File file, boolean shouldSetContent) {
		this.initialFile = file;
		this.file = file;
		this.initialTitle = file == null ? "Untitled" : file.getName();

		setText(initialTitle);
		
		if (shouldSetContent && file != null) {
			setFileText(FileHandler.readFile(file));
		}
	}
	
	/**
	 * Sets the given String to the TextArea.
	 * @param text The String to write in the TextArea.
	 */
	public void setFileText(String text) {
		textArea.setText(text);
	}
	
	/**
	 * Calls the onCloseRequest method from the given source.
	 * @param source The source of the event.
	 */
//	public void close(Object source) {
//		TabEntity e = new TabEntity();
//		Event event = new Event(source, this, null);
//        if (hasChanged) {
//        	showConfirmDialog(event);
//        } else {
//        	Event.fireEvent(this, new Event(Tab.CLOSED_EVENT));
//        }
//    }
		
	/**
	 * Shows a confirm dialog and performs a corresponding action to whether the user 
	 * chose OK, Cancel, or No.
	 * @param event The event.
	 */
	private void showConfirmDialog(Event event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		alert.setTitle("Save?");
		alert.setHeaderText("The file has been modified. Would you like to save?");
		alert.setContentText("Save?");
		
		ButtonType okButton = new ButtonType("Yes", ButtonData.OK_DONE);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(cancelButton, okButton, noButton);
		alert.showAndWait().ifPresent(result -> {
			if (result == cancelButton) {
				event.consume();
				return;
			} else if (result == okButton) {
				boolean saved = save();
				if (!saved) event.consume();
			}
		});
	}
	
	/**
	 * Attempts to save the file. If it does not exist, a save dialog is shown.
	 */
	public boolean save() {
		String text = textArea.getText();
		
		if (file != null) {
			FileHandler.writeTextFile(file, text);
			update(file);
			return true;
		}
		
		File newFile = new FileChooser().showSaveDialog(null);
		
		if (newFile != null) {
			FileHandler.writeTextFile(newFile, text);
			update(newFile);
			return true;
		} else {
			return false;
		}
	}
}
