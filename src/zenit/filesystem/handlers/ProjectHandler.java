package zenit.filesystem.handlers;

import java.io.File;
import java.io.IOException;

import zenit.filesystem.helpers.FileNameHelpers;

/**
 * Methods for creating, opening, saving, renaming and deleting projects in the filesystem.
 * @author Alexander Libot
 *
 */
public class ProjectHandler {
	
	//TODO Will probably merge this and the package handler class to a folder handler class.
	
	/**
	 * Creates a new project with a src folder and a default-package
	 * @param file Folder to create the new project in
	 * @param projectname Name of the new project
	 * @return The path of the default package
	 */
	public static File createNewProject(File file, String projectname) {
		String filepath = file.getAbsolutePath() + "/" + projectname; //New filepath
		new File(filepath).mkdir(); //Create folder
		File projectFolder = new File(filepath); //Create reference to folder
		File packagePath = PackageHandler.createNewPackage(projectFolder, "default_package", true); //Add default package
		
		return packagePath;
	}
	
	public static void createNewProject(File file) throws IOException {
		boolean success = file.mkdir();
		
		if (success) {
			String packagePath = file.getPath() + "/src/default_package";
			File packageFile = new File(packagePath);
			
			String binPath = file.getPath() + "/bin";
			File binFile = new File(binPath);
			
			PackageHandler.createNewPackage(packageFile);
			PackageHandler.createNewPackage(binFile);

		} else {
			throw new IOException("Couldn't create project");
		}
	}
	
	public static void openProject() {
		//Not sure if needed
	}
	
	public static void saveProject() {
		//Not sure if needed
	}
	
	/**
	 * Renames the project.
	 * @param file The project to rename
	 * @param newProjectName The new project name
	 * @return The renamed file
	 */
	public static File renameProject(File file, String newProjectName) throws IOException {
//		String oldProjectName = file.getName();
		
		File tempFile = FileNameHelpers.getFilepathWithoutTopFile(file); //Removes file name
		
		//Create new file
		String newFilepath = tempFile.getPath() + "/" + newProjectName;
		File newFile = new File(newFilepath);
		
//		String oldFilepath = file.getPath() + "/" + oldProjectName;
//		file = new File(oldFilepath);
		
		if (newFile.exists()) {
			throw new IOException("File already exists");
		}
		
		boolean success = file.renameTo(newFile);
		
		if (!success) {
			throw new IOException("Couldn't rename file");
		}
		
		return newFile;
	}
	
	/**
	 * Tries to deletes the project and all files inside.
	 * @param file Project to delete.
	 * @throws IOException Throws IOException if project couldn't be deleted.
	 */
	public static void deleteProject(File file) throws IOException {
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deleteProject(entry);
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}
