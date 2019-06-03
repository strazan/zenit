package main.java.zenit.javacodecompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Contains static helper methods to run shellscripts in terminal
 * @author Alexander Libot
 *
 */
public class TerminalHelpers {
	
	protected static Process runCommand(String command) {
		return runCommand(command, null);
	}
	
	/**
	 * Tries to run the {@code command} in {@code directory} in the terminal.
	 * Uses {@link StreamGobbler} to manage input stream
	 * @param command The command to be run in terminal
	 * @param directory The directory to run the command in
	 */
	protected static Process runCommand(String command, File directory) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			if (System.getProperty("os.name").startsWith("Windows")) {
				builder.command("cmd.exe", "/c", command);
			} else {
				builder.command("sh", "-c", command);
			}

			builder.directory(directory);

			Process process = builder.start();
			
			return process;
			
			
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	protected static Process runBackgroundCommand(String command, DebugErrorBuffer buffer) {
		return runBackgroundCommand(command, null, buffer);
	}
	
	protected static Process runBackgroundCommand(String command, File directory, DebugErrorBuffer buffer) {
		Process process = runCommand(command, directory);
		
		try {
			process.waitFor();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			String place;
			String problemType;
			String problem;
			int row;
			int column;
			
			ArrayList<DebugError> errors = new ArrayList<>();
			DebugError error;
			
			try {
				while ((line = reader.readLine()) != null) {

					int colon1Index = line.indexOf(':');
					int colon2Index = line.indexOf(':', colon1Index + 1);
					int colon3Index = line.indexOf(':', colon2Index + 1);

					place = line.substring(0, colon1Index);
					row = Integer.parseInt(line.substring(colon1Index + 1, colon2Index));
					problemType = line.substring(colon2Index + 1, colon3Index);
					problem = line.substring(colon3Index + 2);

					line = reader.readLine();
					line = reader.readLine();
					column = line.indexOf('^');

					error = new DebugError(place, problemType, problem, row, column);
					errors.add(error);
				}
			} catch (NumberFormatException | IndexOutOfBoundsException ex) {

			}
			
			if (buffer != null) {
				for (DebugError de : errors) {
					buffer.put(de);
				}
			}
			
		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
		}
		
		return process;
	}
}
