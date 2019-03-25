package zenit;

import java.io.PrintStream;

import javafx.scene.control.TextArea;

/**
 * 
 * @author Fredrik Eklundh
 *
 * This class redirects the PrintStream from the console to a TextArea
 */
public class ConsoleRedirect {
	
	public ConsoleRedirect(TextArea ta) {
		try {
			TextAreaOutputStream tacos = new TextAreaOutputStream(ta);
			PrintStream ps = new PrintStream(tacos);
			System.setOut(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}