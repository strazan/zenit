package main.java.zenit;

import java.io.PrintStream;

import javafx.scene.control.TextArea;
import main.java.zenit.console.ConsoleArea;
import main.java.zenit.console.ConsoleAreaErrorStream;
import main.java.zenit.console.ConsoleAreaOutputStream;


/**
@ -12,21 +15,28 @@ import javafx.scene.control.TextArea;
 * This class redirects the PrintStream from the console to a TextArea
 */
public class ConsoleRedirect {

	public ConsoleRedirect(ConsoleArea ta) {
		try {
		
			ConsoleAreaOutputStream socat = new ConsoleAreaOutputStream(ta);
			ConsoleAreaErrorStream tacos = new ConsoleAreaErrorStream(ta);

			PrintStream outPrintStream = new PrintStream(socat);
			PrintStream errPrintStream = new PrintStream(tacos);

			System.setOut(outPrintStream);
			System.setErr(errPrintStream);

			/* 
			 * TODO
			 * System.setIn(in);
			 */
			

		} catch (Exception e) {
			e.printStackTrace();
			//			e.printStackTrace();
		}
	}	
}