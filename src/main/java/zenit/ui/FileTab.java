package main.java.zenit.ui;

import java.io.File;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

import main.java.zenit.filesystem.FileController;
import main.java.zenit.util.StringUtilities;
import main.java.zenit.zencodearea.ZenCodeArea;

/**
 * A Tab extension that holds a File.
 * @author Pontus Laos
 *
 */
public class FileTab extends Tab {
	private File initialFile;
	private File file;
	private String initialTitle;
	private MainController mc;
	
	private ZenCodeArea zenCodeArea;
	
	private boolean hasChanged;
	
	/**
	 * Constructs a new FileTab without a file, setting the title to "Untitled".
	 */
	public FileTab(ZenCodeArea zenCodeArea, MainController mc) {
		this.zenCodeArea = zenCodeArea;
		this.mc = mc;
		initialTitle = "Untitled";
		
		
		zenCodeArea.setOnMouseClicked(new UpdateDetector());
		zenCodeArea.setOnKeyPressed(new UpdateDetector());
		
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
	
	public void setStyle(int row, int column, String style) {
		
		int columnLength = zenCodeArea.getParagraph(row-1).getText().length();
		
		if (column >= columnLength) {
			Platform.runLater(()->
			zenCodeArea.setStyle(row-1,column-1,column,Arrays.asList(style)));
		} else {
			Platform.runLater(()->
			zenCodeArea.setStyle(row-1,column,column+1,Arrays.asList(style)));
		}
		
		
	}
	
	public void addTextPropertyListener(ChangeListener<? super String> listener) {
		zenCodeArea.textProperty().addListener(listener);
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
		
		if (caretPosition >= 6 && text.substring(
			caretPosition - 6, caretPosition).equals("sysout")) 
		{
			zenCodeArea.replaceText(caretPosition - 6, caretPosition, "System.out.println();");
			zenCodeArea.moveTo(caretPosition + 13);
		}
		else if (caretPosition >= 6 && text.substring(
			caretPosition - 6, caretPosition).equals("syserr")) 
		{
			zenCodeArea.replaceText(caretPosition - 6, caretPosition, "System.err.println();");
			zenCodeArea.moveTo(caretPosition + 13);
		}
		else if (caretPosition >= 4 && text.substring(
			caretPosition - 4, caretPosition).equals("main")) 
		{
			zenCodeArea.replaceText(
				caretPosition - 4, caretPosition, "public static void main(String[]args) {\n \n}"
			);
			zenCodeArea.moveTo(caretPosition + 37);
		}
		else if (caretPosition >= 2 && text.substring(
			caretPosition - 2, caretPosition).equals("pv")) 
		{
			zenCodeArea.replaceText(caretPosition - 2, caretPosition, "public void ");
			zenCodeArea.moveTo(caretPosition + 10);
		}
	}
	
	/**
	 * Checks if the caret is after any given shortcut string (start of a comment in which case it
	 * is replaced by the 'full' multi-line comment, and the caret is moved to a suitable position.
	 * @author Pontus Laos, Sigge Labor 
	 */
	public void commentsShortcutsTrigger() {
		if (file == null) {
			return;
		}
		
		String text = zenCodeArea.getText();
		int caretPosition = zenCodeArea.getCaretPosition();
		
		if (caretPosition >= 2 && text.substring(
			caretPosition - 2, caretPosition).equals("/*")) 
		{
			zenCodeArea.replaceText(caretPosition - 2, caretPosition, "/*\n* \n*/");
			zenCodeArea.moveTo(caretPosition + 3);
		}
		else if (caretPosition >= 3 && text.substring(
			caretPosition - 3, caretPosition).equals("/**")) 
		{
			zenCodeArea.replaceText(caretPosition - 3, caretPosition, "/**\n* \n* @author \n*/");
			zenCodeArea.moveTo(caretPosition + 3);
			
		}
		else {
			zenCodeArea.replaceText(caretPosition, caretPosition, "\n");
		}
	}
	
	/**
	 * Checks what tab index the new line should begin at. Also adds a } if needed.
	 * @author Pontus Laos
	 */
	public void navigateToCorrectTabIndex() {
		int previousLine = zenCodeArea.getCurrentParagraph() - 1;
		String previousText = zenCodeArea.getParagraph(previousLine).getText();
		
		int count = StringUtilities.countLeadingSpaces(previousText);
		
		String spaces = "";
		for (int i = 0; i < count; i++) {
			spaces += " ";
		}
		
		if (previousText.endsWith("{")) {
			spaces += "    "; // lol
			zenCodeArea.insertText(zenCodeArea.getCaretPosition(), spaces);
			addMissingCurlyBrace(previousLine + 2, 0, spaces);
		} else {
			zenCodeArea.insertText(zenCodeArea.getCaretPosition(), spaces);
		}
	}
	
	/**
	 * Adds a curly brace on the appropriate line if one is missing.
	 * @param row The row (line number) to add the curly brace to.
	 * @param column The column to add the curly brace to.
	 * @param spaces The amount of spaces to add before the curly brace.
	 * @author Pontus Laos
	 */
	private void addMissingCurlyBrace(int row, int column, String spaces) {
		int[] counts = {
			StringUtilities.count(zenCodeArea.getText(), '{'),
			StringUtilities.count(zenCodeArea.getText(), '}'),
		};
		
		if (counts[0] == counts[1] + 1) {
			zenCodeArea.insertText(zenCodeArea.getCaretPosition(), "\n");
			zenCodeArea.insertText(
				row, column, 
				spaces.substring(0, spaces.length() - 4) + "}"
			);
			zenCodeArea.moveTo(row - 1, spaces.length());
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
		
	public ZenCodeArea getZenCodeArea() {
		return zenCodeArea;
		
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
	
	private class UpdateDetector implements EventHandler<Event> {

		@Override
		public void handle(Event event) {
			int row = zenCodeArea.getCurrentParagraph();
			int col = zenCodeArea.getCaretColumn();
			mc.updateStatusRight((row+1) + " : " + (col+1));
		}
	}
}
