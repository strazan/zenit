package zenit.filesystem.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import zenit.exceptions.TypeCodeException;
import zenit.filesystem.helpers.CodeSnippets;
import zenit.filesystem.helpers.FileNameHelpers;

/**
 * Methods for creating, reading, writing, renaming and deleting classes in filesystem.
 * TODO This is currently a big (but working ;) ) big mess. I'm doing some experimentation
 * with the structure and will probably settle on something until next commit.
 * @author Alexander Libot
 *
 */
public class ClassHandler {
	
	private static String textEncoding = "UTF-8"; //Text-encoding
	
	/**
	 * Tries to create a new file.
	 * Writes a codesnippet for a new class.
	 * @param filepath Filepath of the file to create
	 * @return Returns the created file
	 * @throws IOException Throws IOException if file couldn't be created.
	 */
//	public static File createFile(String filepath) throws IOException {
//		
//		File file = new File(filepath); //Sets new filepath
//		
//		try {
//			file.createNewFile();
//			
//			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream(file), textEncoding));
//			br.write(CodeSnippets.newClass(file.getName(), 
//					FileNameHelpers.getPackagenameFromFile(file)));
//			br.flush();
//			br.close();
//				
//			return file;
//		} catch (IOException e) {
//			throw new IOException("Couldn't create new class");
//		}
//	}
	
	public static File createFile(int typeCode, File file, String content) throws IOException {
		try {
			String fileName = file.getName();
			if (!fileName.endsWith(".java")) {
				String filepath = file.getPath();
				filepath += ".java";
				file = new File(filepath);
			}
			if (file.exists()) {
				throw new IOException();
			}
			file.createNewFile();
			
			if (content == null) {
				try {
					content = CodeSnippets.newSnippet(typeCode, file.getName(), FileNameHelpers.getPackagenameFromFile(file));
				} catch (TypeCodeException ex) {
					ex.printStackTrace();
				}
			}
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), textEncoding));
			br.write(content);
			br.flush();
			br.close();

			return file;
		} catch (IOException e) {
			throw new IOException("Couldn't create new class");
		}
	}
	
	/**
	 * Tries to read file from disk.
	 * @param file The file to read
	 * @return Returns DefaultStyledDocument-object if read succeeded, otherwise null
	 */
	public static String readFile(File file) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), textEncoding))) {
			
			String currentString = br.readLine();
			String stringBuilder = "";

			while (currentString != null) {
				stringBuilder += currentString + "\n";
				currentString = br.readLine();
			}
			return stringBuilder;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Tries to save file to disk.
	 * @param file The file to write over
	 * @param dsd The new content of the file
	 * @return true if save succeeded, otherwise false
	 */
	public static boolean saveFile(File file, String content) {
		boolean returnValue;
		try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), textEncoding))) {
//			br.write(dsd.getText(0, dsd.getLength()));
			br.write(content);
			br.flush();
			returnValue = true;
		} catch (IOException ex) {
			ex.printStackTrace();
			returnValue = false;
		}
		return returnValue;
	}
	
	/**
	 * Tries to rename the file.
	 * @param oldFile File to be renamed
	 * @param newFilename The new filename
	 * @return The renamed file
	 * @throws IOException Throws IOException if file already exists with same name or 
	 * if file couldn't be renamed
	 */
	public static File renameFile(File oldFile, String newFilename) throws IOException {
		
		File tempFile = FileNameHelpers.getFilepathWithoutTopFile(oldFile); //Removes file name
		
		//Create new file with new name
		if (!newFilename.endsWith(".java")) {
			newFilename = newFilename + ".java"; //Adds suffix
		}
		String newFilepath = tempFile.getPath() + "/" + newFilename;
		File newFile = new File(newFilepath);
		
		//
//		String oldFilename = oldFile.getName();
//		String oldFilepath = oldFile.getPath() + "/" + oldFilename;
//		oldFile = new File(oldFilepath);
		
		if (newFile.exists()) {
			throw new IOException("File already exists");
		}

		boolean success = oldFile.renameTo(newFile);
		
		if (!success) {
			throw new IOException("Couldn't rename file");
		}
		
		return newFile;
	}
	
	/**
	 * Tries to delete the file. Returns true if deleted, false if not deleted
	 * @param file The file to delete
	 */
	public static void deleteFile(File file) throws IOException {
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}