package zenit.ui;

import java.io.File;
import java.io.IOException;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
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
	
	public FileTab() {
		textArea = new TextArea();
		initialTitle = "Untitled";
		
		initializeUI();
	}
	
	private void initializeUI() {
		AnchorPane anchorPane = new AnchorPane();	
		AnchorPane.setTopAnchor(textArea, 0.0);
		AnchorPane.setRightAnchor(textArea, 0.0);
		AnchorPane.setBottomAnchor(textArea, 0.0);
		AnchorPane.setLeftAnchor(textArea, 0.0);
		
		anchorPane.getChildren().add(textArea);
		textArea.setStyle("-fx-font-family: monospace");
		
		setContent(anchorPane);
		setOnCloseRequest(this::onCloseRequest);
		
		setText(initialTitle);
		
		textArea.textProperty().addListener((observable, oldText, newText) -> {
			String initialFileContent = FileHandler.readFile(initialFile);
			
			hasChanged = !initialFileContent.equals(newText);
			updateUI();
		});
		
		textArea.requestFocus();
	}
	
	private void updateUI() {
		if (hasChanged) {
			setText(initialTitle + " *");
		}
		else {
			setText(initialTitle);
		}
	}
	
	private void update(File file) {
		setFile(file, false);
		hasChanged = false;
		updateUI();
	}
	
	public File getFile() {
		return file;
	}
	
	public String getFileText() {
		return textArea.getText();
	}
	
	public void setFile(File file, boolean shouldSetContent) {
		this.initialFile = file;
		this.file = file;
		this.initialTitle = file == null ? "Untitled" : file.getName();

		setText(initialTitle);
		
		if (shouldSetContent && file != null) {
			setFileText(FileHandler.readFile(file));
		}
	}
	
	public void setFileText(String text) {
		textArea.setText(text);
	}

	public boolean hasChanged() {
		return hasChanged;
	}
	
	public void closeTab(Object source) {
        onCloseRequest(new Event(this, this, null));
    }
		
	private void onCloseRequest(Event event) {
		if (hasChanged) {
			showConfirmDialog(event);
		}
	}
	
	private void showConfirmDialog(Event event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		alert.setTitle("Save?");
		alert.setHeaderText("The file has been modified. Would you like to save?");
		alert.setContentText("Save?");
		
		ButtonType okButton = new ButtonType("Yes", ButtonData.OK_DONE);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
		alert.showAndWait().ifPresent(result -> {
//			if (result.getButtonData().isCancelButton()) {
//				System.out.println("cancel");
//			}
//			else if (result == noButton){
//				System.out.println("not cancel");
//			}
			if (result == cancelButton) {
				event.consume();
				return;
			} else if (result == noButton) {
			} else if (result == okButton) {
				try {
					save();
				} catch (IOException ex) {}
			}
		});
	}
	
	public void save() throws IOException {
		String text = getFileText();
		System.out.println(text);
		
		if (file != null) {
			FileHandler.writeTextFile(file, text);
			update(file);
			return;
		}
		
		File newFile = new FileChooser().showSaveDialog(null);
		
		if (newFile != null) {
			FileHandler.writeTextFile(newFile, text);
			update(newFile);
		}
	}
}
