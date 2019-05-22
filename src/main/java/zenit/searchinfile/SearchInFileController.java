package main.java.zenit.searchinfile;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class SearchInFileController extends AnchorPane {
	
	@FXML
	private TextField fldInputField;
	
	
	private Search search;
	
	public SearchInFileController() {
//		search = new Search(this);
		Initiazlie();
	}
	
	private void Initiazlie(){
		fldInputField.textProperty().addListener((observable, oldValue, newValue) -> {
//		   search.searchInFile(newValue);
		});
	}
	
//	public set

	
}
