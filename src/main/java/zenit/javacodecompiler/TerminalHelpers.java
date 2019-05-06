package main.java.zenit.javacodecompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
