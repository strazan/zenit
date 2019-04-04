package zenit.console;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.InlineCssTextArea;


public class ConsoleArea extends CodeArea {
	
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
	
	
	public void outPrint(String s) {
		super.appendText(s);
	}
}
