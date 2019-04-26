package main.java.zenit.console;


import org.fxmisc.richtext.InlineCssTextArea;

import javafx.application.Platform;



public class ConsoleArea extends InlineCssTextArea {
	
	private final String ID;
	
	public ConsoleArea(){
		this("UNKNOWN");
		getStylesheets().add(getClass().getResource("/zenit/console/consoleStyle.css").toString());
		
//		setEditable(false);
	}

	public ConsoleArea(String identity) {
		this.ID = identity;
	//	setStyle("-fx-background-color: yellow;");
	}
	public String getID() {
		// TODO Auto-generated method stub
		return ID;
	}
	
	public void errPrint(String s) {
		
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	
		    	appendText(s);
		    	setStyle(getText().length() - s.length(), getText().length(), "-fx-fill: red;");
		    }
		});
	}
	
	public void outPrint(String s) {
		
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	for( int i = 0; i < s.length(); i++) {
			    	appendText(String.valueOf(s.charAt(i)));
			    	setStyle(getText().length() - 1, getText().length(), "-fx-fill: white");	
			    	}
		    }
		});
	}
	
	 public void clearConsole() {
		 replaceText("");
	 }
}