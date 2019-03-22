package zenit.filesystem.handlers;

import java.io.File;
import java.io.IOException;

import zenit.filesystem.helpers.FileNameHelpers;

/**
 * Methods for creating, opening, saving, renaming and deleting packages in filesystem
 * @author Alexander Libot
 *
 */
public class PackageHandler {

	/**
	 * Creates a new package-folder
	 * @param file Folder to put the new package in
	 * @param packagename The name of the new package
	 * @return
	 */
	public static File createNewPackage(File file, String packagename, boolean topLevelPackage) {
		String packagePath;
		if (topLevelPackage) {
			packagePath = file.getAbsolutePath() + "/src/" + packagename;
		} else {
			packagePath = file.getAbsolutePath() + "/" + packagename;
		}
		
		new File(packagePath).mkdirs();
		return new File(packagePath);
	}
	
	public static boolean createNewPackage(File file) {
		return file.mkdirs();
	}
	
//	public static void openPackage() {
//		Not sure if neeeded
//	}
	
//	public static void savePackage(File file) {
//		Not sure if needed
//	}
	
	/**
	 * Renames a package
	 * @param file The path to the file to be renamed
	 * @param newPackagename The new name of the package
	 * @return The renamed file
	 * @throws IOException Throws IOException if package already exists or if it can't be renamed
	 */
	public static File renamePackage(File file, String newPackageName) throws IOException {
		
		String oldName = file.getName();
		file = FileNameHelpers.getFilepathWithoutTopFile(file); //Removes file name
		
		String newFilepath = file.getAbsolutePath();
		newFilepath += "/" + newPackageName;
		File newFile = new File(newFilepath);
		
		String oldFilepath = file.getPath() + "/" + oldName;
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
	
	public static void deletePackage(File file) throws IOException {
		
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deletePackage(entry);
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}
