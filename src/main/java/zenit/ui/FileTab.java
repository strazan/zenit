package main.java.zenit.ui;

import java.io.File;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.zencodearea.ZenCodeArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * A Tab extension that holds a File.
 * @author Pontus Laos
 *
 */
public class FileTab extends Tab {
	private File initialFile;
	private File file;
	private String initialTitle;
	
	private ZenCodeArea zenCodeArea;
	
	private boolean hasChanged;
	
	/**
	 * Constructs a new FileTab without a file, setting the title to "Untitled".
	 */
	public FileTab() {
		zenCodeArea = new ZenCodeArea();
		initialTitle = "Untitled";
		
		initializeUI();
	}
	
	/**
	 * Initializes the UI. Attaches an AnchorPane with a TextArea that fills it, 
	 * and sets additional data.
	 */
	private void initializeUI() {
		AnchorPane anchorPane = new AnchorPane();	
		AnchorPane.setTopAnchor(zenCodeArea, 0.0);
		AnchorPane.setRightAnchor(zenCodeArea, 0.0);
		AnchorPane.setBottomAnchor(zenCodeArea, 0.0);
		AnchorPane.setLeftAnchor(zenCodeArea, 0.0);

		anchorPane.getChildren().add(zenCodeArea);
		
		setContent(anchorPane);
		setText(initialTitle);
		
		zenCodeArea.textProperty().addListener((observable, oldText, newText) -> {
			String initialFileContent = FileController.readFile(initialFile);
			
			hasChanged = !initialFileContent.equals(newText);
			updateUI();
		});
		setStyle("-fx-background-color: #444;");
		setStyle("-fx-stroke: #fff;");
		
		Platform.runLater(zenCodeArea::requestFocus);
	}
	
	/**
	 * Checks if the caret is after any given shortcut string, in which case it is replaced by 
	 * the 'full' string, and the caret is moved to a suitable position.
	 * @author Pontus Laos, Sigge Labor 
	 */
	public void shortcutsTrigger() {
		if (file == null) {
			return;
		}
		
		String text = zenCodeArea.getText();
		int caretPosition = zenCodeArea.getCaretPosition();
		
		if (caretPosition >= 6 && text.substring(caretPosition - 6, caretPosition).equals("sysout")) {
			zenCodeArea.replaceText(caretPosition - 6, caretPosition, "System.out.println();");
			zenCodeArea.moveTo(caretPosition + 13);
		}
		else if (caretPosition >= 4 && text.substring(caretPosition - 4, caretPosition).equals("main")) {
			zenCodeArea.replaceText(caretPosition - 4, caretPosition, "public static void main(String[] args) {\n \n}");
			zenCodeArea.moveTo(caretPosition + 37);
		}
		else if (caretPosition >= 2 && text.substring(caretPosition - 2, caretPosition).equals("pv")) {
			zenCodeArea.replaceText(caretPosition - 2, caretPosition, "public void ");
			zenCodeArea.moveTo(caretPosition + 10);
		}
	}
	
	/**
	 * Updates the title of the tab.
	 */
	private void updateUI() {
		if (hasChanged) {
			setText(initialTitle + " *");
		} else {
			setText(initialTitle);
		}
	}
	
	/**
	 * Sets the file to the given one, and updates the UI.
	 * @param file The File to set.
	 */
	public void update(File file) {
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
	 * Returns the text in the Tab's TextArea.
	 * @return A String that holds the text in the Tab.
	 */
	public String getFileText() {
		return zenCodeArea.getText();
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
			setFileText(FileController.readFile(file));
		}
	}
	
	/**
	 * Sets the given String to the TextArea.
	 * @param text The String to write in the TextArea.
	 */
	public void setFileText(String text) {
		zenCodeArea.replaceText(text);
	}
	
	/**
	 * Indicates whether the file has been modified or not.
	 * @return True if the file has been modified, else false.
	 */
	public boolean hasChanged() {
		return hasChanged;
	}
		
	/**
	 * Shows a confirm dialog and performs a corresponding action to whether the user 
	 * chose OK, Cancel, or No.
	 * @param event The event.
	 */
	public int showConfirmDialog() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		alert.setTitle("Save?");
		alert.setHeaderText("The file has been modified. Would you like to save?");
		alert.setContentText("Save?");
		
		ButtonType okButton = new ButtonType("Yes", ButtonData.OK_DONE);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(cancelButton, okButton, noButton);
		
		var wrapper = new Object() { int response; };
		
		alert.showAndWait().ifPresent(result -> {
			if (result == cancelButton) {
				wrapper.response = 0;
			} else if (result == noButton) {
				wrapper.response = 1;
			} else if (result == okButton) {
				wrapper.response = 2;
			}
		});
		
		return wrapper.response;
	}
}
