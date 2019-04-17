package main.java.zenit;


import java.io.PrintStream;

import zenit.console.ConsoleArea;
import zenit.console.ConsoleAreaErrorStream;
import zenit.console.ConsoleAreaOutputStream;


/**
 * 
 * @author Fredrik Eklundh
 *
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
			//			e.printStackTrace();
		}
	}	
}