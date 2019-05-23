package main.java.zenit.searchinfile;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SearchInFileController extends AnchorPane {

	@FXML
	private TextField fldInputField;
	
	@FXML
	private TextField fldReplaceWord;
	
	@FXML
	private Button btnUp;
	
	@FXML
	private Button btnDown;
	
	@FXML 
	private Button btnEsc;
	
	@FXML
	private Button btnReplaceOne;
	
	@FXML
	private Button btnReplaceAll;
	
	@FXML
	private Label lblOccurrences;

	private Search search;

	public SearchInFileController(Search search) {

		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/zenit/searchinfile/SearchInFileWindow.fxml"));
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

		window.initStyle(StageStyle.UNDECORATED);

		window.setScene(scene);
		
		window.setX(1050);
		window.setY(160);
		
		initialize();
		scene.getStylesheets()
				.add(getClass().getResource("/zenit/settingspanel/settingspanelstylesheet.css").toString());

		window.show();
		
		this.search = search;
//		initialize();
	}

	private void initialize() {
		fldInputField.textProperty().addListener((observable, oldValue, newValue) -> {

		search.searchInFile(newValue);
		});
	}

}
