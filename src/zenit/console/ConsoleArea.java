package zenit.console;

import java.net.URL;
import java.util.ResourceBundle;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.fxml.Initializable;

public class ConsoleArea extends InlineCssTextArea {
	
	private final String ID;
	
	public ConsoleArea(){
		this("");
	}

	public ConsoleArea(String identity) {
		this.ID = identity;
		setStyle("-fx-background-color: yellow;");
	}
	public String getID() {
		// TODO Auto-generated method stub
		return ID;
	}
}
