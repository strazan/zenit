package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

import main.java.zenit.filesystem.helpers.FileNameHelpers;

/**
 * Methods for creating, renaming and deleting folders.
 * Only to be accessed by {@link FileController} methods.
 * 
 * @author Alexander Libot
 *
 */
public class FolderHandler {
	
	/**
	 * Tries to create a folder (or folders if file contains multiple uncreated folders).
	 * @param file The folder(s) to create
	 * @throws IOException Throws {@link java.io.IOException IOException} if any of
	 * the folders can't be created.
	 */
	protected static void createNewFolder(File file) throws IOException {
		boolean success = file.mkdirs();
		if (!success) {
			throw new IOException ("Couldn't create folder " + file.getName());
		}
	}
	
	/**
	 * Tries to rename the folder (f.x. project or package).
	 * @param file The folder to rename
	 * @param newFolderName The new folder name
	 * @return The renamed file
	 * @throws IOException Throws an IOException if folder already exist, name is not allowed
	 * or if {@link java.io.File#renameTo(File) renameTo} throws an exception
	 */
	protected static File renameFolder(File file, String newFolderName) throws IOException {
		
		if (newFolderName.equals("package")) {
			throw new IOException("Can't rename package to: " + newFolderName);
		}
		
		File tempFile = FileNameHelpers.getFilepathWithoutTopFile(file); //Removes file name
		
		//Create new file
		String newFilepath = tempFile.getPath() + "/" + newFolderName;
		File newFile = new File(newFilepath);
		
		//Check if file exists
		if (newFile.exists()) {
			throw new IOException("File already exists");
		}
		
		//Rename file
		boolean success = file.renameTo(newFile);
		if (!success) {
			throw new IOException("Couldn't rename file");
		}
	
		return newFile;
	}
	
	/**
	 * Tries to deletes the folder (f.x. project or package) and all folders and files inside
	 * recursively.
	 * @param file Folder to delete.
	 * @throws IOException Throws {@link java.io.IOException IOException} if folder or any
	 * file or folder inside couldn't be deleted.
	 */
	protected static void deleteFolder(File file) throws IOException {
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deleteFolder(entry); //Recursively delete files
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}