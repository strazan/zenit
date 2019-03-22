package zenit.filesystem.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import zenit.filesystem.helpers.CodeSnippets;
import zenit.filesystem.helpers.FileNameHelpers;

/**
 * Methods for creating, reading, writing, renaming and deleting classes in filesystem.
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
	public static File createFile(String filepath) throws IOException {
		
		File file = new File(filepath); //Sets new filepath
		
		try {
			file.createNewFile();
			
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), textEncoding));
			br.write(CodeSnippets.newClass(file.getName(), 
					FileNameHelpers.getPackagenameFromFile(file)));
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
	public static DefaultStyledDocument readFile(File file) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), textEncoding))) {
			DefaultStyledDocument dsd = new DefaultStyledDocument();
			String currentString = br.readLine();
			String stringBuilder = "";

			while (currentString != null) {
				stringBuilder += currentString + "\n";
				currentString = br.readLine();
			}
				try {
					dsd.insertString(0, stringBuilder, null);
					return dsd;
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			
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
	public static boolean saveFile(File file, DefaultStyledDocument dsd) {
		boolean returnValue;
		try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), textEncoding))) {
			br.write(dsd.getText(0, dsd.getLength()));
			br.flush();
			returnValue = true;
		} catch (IOException ex) {
			ex.printStackTrace();
			returnValue = false;
		} catch (BadLocationException ex) {
			ex.printStackTrace();
			returnValue = false;
		}
		return returnValue;
	}
	
	/**
	 * Tries to rename the file.
	 * @param file File to be renamed
	 * @param newFilename The new filename
	 * @return The renamed file
	 * @throws IOException Throws IOException if file already exists with same name or 
	 * if file couldn't be renamed
	 */
	public static File renameFile(File file, String newFilename) throws IOException {
		String oldFilename = file.getName();
		newFilename = newFilename + ".java"; //Adds suffix
		
		file = FileNameHelpers.getFilepathWithoutTopFile(file); //Removes file name
		
		String newFilepath = file.getPath();
		newFilepath += "/" + newFilename;	
		File newFile = new File(newFilepath);
		
		String oldFilepath = file.getPath() + "/" + oldFilename;
		file = new File(oldFilepath);
		
		if (newFile.exists()) {
			throw new IOException("File already exists");
		}
		
		System.out.println(file.getPath());
		System.out.println(newFile.getPath());
		boolean success = file.renameTo(newFile);
		
		if (!success) {
			throw new IOException("Couldn't rename file");
		}
		
		return newFile;
	}
	
	/**
	 * Tries to delete the file. Returns true if deleted, false if not deleted
	 * @param file The file to delete
	 */
	public static boolean deleteFile(File file) {
		return file.delete();
	}
}