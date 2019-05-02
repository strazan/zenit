package main.java.zenit.textsizewindow;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TextSizeController extends AnchorPane {

	private int oldSize;

	@FXML
	private TextField fldNewSize;

	@FXML
	private Label lblOldValue;

	@FXML
	private Slider sldrNewSize;

	public TextSizeController(int formerSize) {
		oldSize = formerSize;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/textsizewindow/NewTextSize.fxml"));

		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Stage stage = new Stage();
		Scene scene = new Scene(this);

		stage.setScene(scene);
		stage.setTitle("Zenit");

		initialize();
		stage.show();

	}
	
	public void setNewFontSize() {
		
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		lblOldValue.setText(String.valueOf(oldSize));
		fldNewSize.setText(String.valueOf(oldSize));
		sldrNewSize.setValue(oldSize);
		sldrNewSize.valueProperty().addListener(
			(ChangeListener) (arg0, arg1, arg2) -> fldNewSize.textProperty().setValue(
				String.valueOf(Math.round(sldrNewSize.getValue(
		)))));
	}

}
