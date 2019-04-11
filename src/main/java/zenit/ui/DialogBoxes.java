package main.java.zenit.ui;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 * Opening different kinds of dialog boxes with dynamic text depending of input.
 * @author Alexander Libot
 *
 */
public class DialogBoxes {

	/**
	 * Opens an input dialog for reading text from user.
	 * @param stage Stage to open in.
	 * @param title The title of the input dialog
	 * @param header The header of the input dialog
	 * @param content The main text of the input dialog
	 * @param textInput The pre-written input text of the input dialog
	 * @return
	 */
	public static String inputDialog(Stage stage, String title, String header, String content, String textInput) {
		TextInputDialog dialog = new TextInputDialog(textInput);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		   return result.get();
		}
		return null;
	}
}
