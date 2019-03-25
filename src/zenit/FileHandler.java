package zenit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class for handling files: writing, reading, etc.
 * @author Pontus Laos
 *
 */
public class FileHandler {

	/**
	 * Writes the given text to the given file. Only used for text files.
	 * @param file The File to write to.
	 * @param text The text to write to the File.
	 */
	public boolean writeTextFile(File file, String text) {
		try (
			var fileWriter = new FileWriter(file);
			var bufferedWriter = new BufferedWriter(fileWriter);
		) {
			bufferedWriter.write(text);
		} catch (IOException ex) {
			ex.printStackTrace();

			return false;
		}
		return true;
	}
	
	/**
	 * Reads a file line by line.
	 * @param file The File to read.
	 * @return A String containing all the lines of the File content. 
	 * Null if the file could not be read.
	 */
	public String readFile(File file) {
		try (
			var fileReader = new FileReader(file);
			var bufferedReader = new BufferedReader(fileReader);
		) {
			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}

			return builder.toString();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();

			// TODO: give the user feedback that the file could not be found
		} catch (IOException ex) {
			ex.printStackTrace();

			// TODO: handle IO exception
		}
		return null;
	}
}
