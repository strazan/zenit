package main.java.zenit.console;

import org.fxmisc.richtext.InlineCssTextArea;
import javafx.application.Platform;

/**
 * A console area with prints string in two different colors. All error prints in one and all other
 * in one
 * 
 * @author sigge labor , Oskar Molander
 *
 */
public class ConsoleArea extends InlineCssTextArea {
	
	private String ID;
	private Process process;
	private String backgroundColor;
	private String fileName;
	
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
		this("UNKNOWN", null, "");
	}

	/**
	 * constructs a new ConsoleArea with chosen identity and process. 
	 */
	public ConsoleArea(String identity, Process process, String backgroundColor) {
		this.ID = identity;
		this.process = process;
		
		// TODO maybe remove this one and add to main.css
		getStylesheets().add(getClass().getResource("/zenit/console/consoleStyle.css").toString());
		this.setStyle(backgroundColor);
		this.setEditable(false);
		
		
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String name) {
		this.fileName = name;
	}
	
	public String getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public void setBackgroundColor(String color ) {
		this.backgroundColor = color;
		this.setStyle(color);
	}
	
	public Process getProcess() {
		return this.process;
	}
	
	public void setProcess(Process p) {
		this.process = p;
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
		    	try {
					appendText(stringToPrint);
					setStyle(getText().length() - stringToPrint.length(), getText().length(), "-fx-fill: red;");
				} catch (IndexOutOfBoundsException e) {
					// Windows bug, dont do anything with the exception.
				}
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
			    	try {
						appendText(stringToPrint);
						setStyle(getText().length() - stringToPrint.length(), getText().length(),
								"-fx-fill: white");
					} catch (IndexOutOfBoundsException e) {
						// Windows bug, dont do anything with the exception.
					}	   
		    }
		});
	}
	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	@Override
	public String toString() {
		return this.ID;
	}
}