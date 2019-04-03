package zenit.javacodeCompiler;

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
	protected static void runCommand(String command, File directory) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("sh", "-c", command); //Only works for UNIX-based OS atm

			builder.directory(directory);

			Process process = builder.start();
			StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
			Executors.newSingleThreadExecutor().submit(streamGobbler);
            
//			int exitCode = process.waitFor();
//			assert exitCode == 0;

		} catch (IOException ex) {
			ex.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}
	
	/**
	 * Used to print terminal output in consumers
	 * @author Alexander Libot
	 *
	 */
	protected static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;
		
		/**
		 * Creates a new StreamGobbler
		 * @param inputStream inputstream to print
		 * @param consumer Consumers to print inputStream messages to
		 */
		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}
		
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
		}
	}

}
