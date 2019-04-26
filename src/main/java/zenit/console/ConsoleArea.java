package main.java.zenit.console;

import org.fxmisc.richtext.InlineCssTextArea;
import javafx.application.Platform;

/**
 * A console area with prints string in two different colors. All error prints in one and all other
 * in one
 * 
 * @author sigge labor
 *
 */
public class ConsoleArea extends InlineCssTextArea {
	
	private final String ID;
	
	/*
	 * README
	 * 
	 * Every console needs to be connected to an executed 'main method'. Here, each console get 
	 * an ID, but it's a shitty solution. 
	 */
	
	/**
	 * constructs a new ConsoleArea with identity "UNKNOWN".
	 */
	public ConsoleArea(){
		this("UNKNOWN");
	}

	/**
	 * constructs a new ConsoleArea with chosen identity. also adds stylesheet.  
	 */
	public ConsoleArea(String identity) {
		this.ID = identity;
		
		// TODO maybe remove this one and add to main.css
		getStylesheets().add(getClass().getResource("/zenit/console/consoleStyle.css").toString());
	}
	
	/**
	 * @return ID
	 */
	public String getID() {
		return ID;
	}
	
	/**
	 * prints out an error text with chosen style (red default) in the console. 
	 * @param error string to print
	 */
	public void errPrint(String stringToPrint) {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {    	
		    	appendText(stringToPrint);
		    	setStyle(
		    		getText().length() - stringToPrint.length(), getText().length(), "-fx-fill: red;"
		    	);
		    }
		});
	}
	
	/**
	 * prints out a regular text/string in the console.
	 * @param string to print
	 */
	public void outPrint(String stringToPrint) {
		
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {	 
			    	appendText(stringToPrint);
			    	setStyle(
			    		getText().length() - stringToPrint.length(), getText().length(), "-fx-fill: black"
			    	);	   	
		    }
		});
	}
	
	/**
	 * clears the console of all text.
	 */
	 public void clearConsole() {
		 replaceText("");
	 }
}