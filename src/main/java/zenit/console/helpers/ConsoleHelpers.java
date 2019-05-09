package main.java.zenit.console.helpers;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.kodedu.terminalfx.Terminal;

public class ConsoleHelpers {

	
	
	/**
	 * 
	 * @param Terminal terminal
	 * @param String command
	 * Executes a command in the active terminal.
	 */

	public static void executeTerminalCommand(Terminal terminal, String command) {
		try {
			terminal.command(command);
			terminal.focusCursor();
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
