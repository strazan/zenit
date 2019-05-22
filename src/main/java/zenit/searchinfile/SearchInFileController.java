package main.java.zenit.searchinfile;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SearchInFileController extends AnchorPane {

	@FXML
	private TextField fldInputField;

	private Search search;

	public SearchInFileController() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/seachinfile/SearchInFileWindow.fxml"));

		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Stage window = new Stage();
		Scene scene = new Scene(this);

		window.setScene(scene);
		window.setTitle("preferences");
		initialize();
		scene.getStylesheets()
				.add(getClass().getResource("/zenit/settingspanel/settingspanelstylesheet.css").toString());

		window.show();
//		search = new Search(this);
		initialize();
	}

	private void initialize() {
		fldInputField.textProperty().addListener((observable, oldValue, newValue) -> {
//		   search.searchInFile(newValue);
		});
	}

//	public set

}
