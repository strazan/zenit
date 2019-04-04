package zenit;

import java.io.PrintStream;

import javafx.scene.control.TextArea;
import zenit.console.ConsoleArea;
import zenit.console.ConsoleAreaErrorStream;
import zenit.console.ConsoleAreaOutputStream;
import zenit.textFlow.ZenCodeArea;

/**
 * 
 * @author Fredrik Eklundh
 *
 * This class redirects the PrintStream from the console to a TextArea
 */
public class ConsoleRedirect {
	
	public ConsoleRedirect(ConsoleArea ta) {
		try {
			ConsoleAreaOutputStream tacos = new ConsoleAreaOutputStream(ta);
			//ConsoleAreaErrorStream socat = new ConsoleAreaErrorStream(ta);
			PrintStream ps = new PrintStream(tacos);
		//	PrintStream err = new PrintStream(socat);
			
			System.setOut(ps);
			System.setErr(ps);
			
			/* 
			 * TODO
			 * System.setIn(in);
			 */
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}