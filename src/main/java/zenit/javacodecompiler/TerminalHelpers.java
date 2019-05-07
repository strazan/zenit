package main.java.zenit.javacodecompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Contains static helper methods to run shellscripts in terminal
 * @author Alexander Libot
 *
 */
public class TerminalHelpers {
	
	//TODO Only works on UNIX atm
	
	/**
	 * Tries to run the {@code command} in {@code directory} in the terminal.
	 * Uses {@link StreamGobbler} to manage input stream
	 * @param command The command to be run in terminal
	 * @param directory The directory to run the command in
	 */
	protected static int runCommand(String command, File directory) {
		runBackgroundCommand(command, directory);
		try {
			ProcessBuilder builder = new ProcessBuilder();
			if (System.getProperty("os.name").startsWith("Windows")) {
				builder.command("cmd.exe", "/c", command);
			} else {
				builder.command("sh", "-c", command);
			}

			builder.directory(directory);

			Process process = builder.start();
			
			StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), process.getErrorStream(), System.out::println);
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			
			process.waitFor();
			
			return process.exitValue();
			

		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
			return 1;
		}
	}
	
	protected static void runBackgroundCommand(String command, File directory) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			if (System.getProperty("os.name").startsWith("Windows")) {
				builder.command("cmd.exe", "/c", command);
			} else {
				builder.command("sh", "-c", command);
			}
	
			builder.directory(directory);
	
			Process process = builder.start();
			
			process.waitFor();
			
			BufferedReader reader =
	                    new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			String place;
			String problemType;
			String problem;
			int row;
			int column;
			
			ArrayList<DebugError> errors = new ArrayList<>();
			DebugError error;
			

			while ((line = reader.readLine()) != null) {
				
				int colon1Index = line.indexOf(':');
				int colon2Index = line.indexOf(':', colon1Index+1);
				int colon3Index = line.indexOf(':', colon2Index+1);
				
				place = line.substring(0, colon1Index);
				row = Integer.parseInt(line.substring(colon1Index+1, colon2Index));
				problemType = line.substring(colon2Index+1, colon3Index);
				problem = line.substring(colon3Index+2);
					
				line = reader.readLine();
				line = reader.readLine();
				column = line.indexOf('^');
				
				error = new DebugError(place, problemType, problem, row, column);
				errors.add(error);
				
			}
			//TODO Update textarea instead of printouts
			if (errors.size() > 0) {
				for (DebugError de : errors) {
					System.out.println(de);
				}
			} else {
				System.out.println("No problems");
			}
		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Used to print terminal output in consumers
	 * @author Alexander Libot
	 *
	 */
	protected static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private InputStream errorStream;
		private Consumer<String> consumer;
		
		/**
		 * Creates a new StreamGobbler
		 * @param inputStream inputstream to print
		 * @param consumer Consumers to print inputStream messages to
		 */
		public StreamGobbler(InputStream inputStream, InputStream errorStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.errorStream = errorStream;
			this.consumer = consumer;
		}
		
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
			new BufferedReader(new InputStreamReader(errorStream)).lines().forEach(consumer);
		}
	}
}
