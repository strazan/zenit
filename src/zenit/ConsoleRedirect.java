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
			// Store console print stream.
			//PrintStream ps_console = System.out;

			TextAreaOutputStream tacos = new TextAreaOutputStream(ta);

			PrintStream ps = new PrintStream(tacos);
			System.setOut(ps);
			System.out.println("THE ZENIT CONSOLE");

			// Set console print stream.
			// System.setOut(ps_console);
			//System.out.println("Console again !!");
		} catch (Exception e) {
			System.out.println("Wrong console");
		}
	}	
}
