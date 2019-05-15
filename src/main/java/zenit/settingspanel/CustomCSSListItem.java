package main.java.zenit.settingspanel;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class CustomCSSListItem extends AnchorPane {

	@FXML 
	private Label lblCSSText;
	
	private String CSSLINEM;
	
	public CustomCSSListItem(String name) {
		CSSLINEM = name;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/settingspanel/CustomCSSListItem.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			loader.setRoot(this);

			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		lblCSSText.setText(CSSLINEM);;
	}
}
