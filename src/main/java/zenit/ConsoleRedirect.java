package main.java.zenit;

import java.io.PrintStream;

import main.java.zenit.console.ConsoleArea;
import main.java.zenit.console.ConsoleAreaErrorStream;
import main.java.zenit.console.ConsoleAreaOutputStream;

/**
 * This class redirects the PrintStream to given ConsoleArea.
 * 
 * @author siggelabor
 *
 */
public class ConsoleRedirect {

	/**
	 * This method will set the out and error PrintStream to chosen ConsoleArea.
	 * 
	 * @param the ConsoleArea prints are to be directed to.
	 */
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
		}
	}	
}