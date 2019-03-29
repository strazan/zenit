package zenit.filesystem;

import java.io.File;
import java.io.IOException;

import zenit.filesystem.helpers.CodeSnippets;

/**
 * Class for controlling and manipulating the file system of a project.
 * @author Alexander Libot
 *
 */
public class FileController {
	
	private File workspace; //Used as a base-file for all files

	//Constructors
	
	/**
	 * Creates a new file controller to manipulate the file system
	 */
	public FileController(File workspace) {
		this.workspace = workspace;
	}
	
	//Getters
	/**
	 * Returns the current {@code workspace}-file
	 */
	public File getWorkspace() {
		return workspace;
	}
	
	/**
	 * Creates a new .java file from the File-objects using 
	 * {@link JavaFileHandler#createFile(int, File, String) ClassHandler}
	 * @param file File to be created
	 * @param content Content of the file, may be null.
	 * @param typeCode If new file, the type of code snippet to be inserted.
	 * Use {@link zenit.filesystem.helpers.CodeSnippets#newSnippet(int, String, String)
	 * CodeSnippets} constants.
	 * @return Returns the created File-object if created, otherwise null
	 */
	public File createFile(File file, String content, int typeCode) {
		if (file != null) {
			try {
				return JavaFileHandler.createFile(file, content, typeCode);
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Calls {@link #createFile(int, File, String) createFile} method with null 
	 * content parameter.
	 * @param file The file to be created
	 * @param typeCode If new file, the type of code snippet to be inserted.
	 * Use {@link zenit.filesystem.helpers.CodeSnippets#newSnippet(int, String, String)
	 * CodeSnippets} constants.
	 * @return See {@link #createFile(int, File, String) createFile} for return value
	 */
	public File createFile(File file, int typeCode) {
		return createFile(file, null, typeCode);
	}
	
	/**
	 * Calls {@link #createFile(int, File, String) createFile} method with {@code typeCode}
	 * for empty code snippet.
	 * @param file The file to be created
	 * @param content The content to write to file. Can be null.
	 * @return See {@link #createFile(int, File, String) createFile} for return value
	 */
	public File createFile(File file, String content) {
		return createFile(file, content, CodeSnippets.EMPTY);
	}
	
	/**
	 * Tries to read a java-file from disk. Prints an error message if file can't be read.
	 * @param file The file to read from disk.
	 * @return The content of read file if read, otherwise null.
	 */
	public String readFileContent(File file) {
		if (file != null) {
			try {
				return JavaFileHandler.readFile(file);
			} catch (IOException ex) {
				System.err.println("FileController.readFileContent: " + ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Tries to write {@code content} to {@code file}. Prints error message if content
	 * can't be written.
	 * @param file The file to write over.
	 * @param dsd The content to write to disk
	 */
	public boolean writeFile(File file, String content) {

		if (file != null && content != null) {
			try {
				JavaFileHandler.saveFile(file, content); //Tries to save file
				return true;
			} catch (IOException ex) {
				System.err.println("FileController.writeFile: " + ex.getMessage());
			}
		}
		return false;
	}
	
	/**
	 * Renames a file-object and it's file path using {@link zenit.filesystem.JavaFileHandler
	 * #renameFile(File, String) renameFile} method if {@code file} is a file or
	 * {@link zenit.filesystem.ProjectHandler#renameFolder(File, String) renameProject}
	 * method if {@code file} is a directory. Prints an error message if file couldn't
	 * be renamed.
	 * 
	 * @param file File to be renamed
	 * @param newName New name of file
	 * @return Renamed file if renamed, otherwise null
	 */
	public File renameFile(File file, String newName) {
		File newFile = null;
		if (file != null && newName != null) {
			try {
				if (file.isDirectory()) {
					newFile = FolderHandler.renameFolder(file, newName);
				} else {
					newFile = JavaFileHandler.renameFile(file, newName);
				}
			} catch (IOException ex) {
				System.err.println("FileController.renameFile: " + ex.getMessage());
			}
		}
		return newFile;
	}
	
	/**
	 * Deletes a file from disk. Using {@link zenit.filesystem.JavaFileHandler
	 * #deleteFile(File) deleteFile} method if {@code file} is a file or
	 * {@link zenit.filesystem.ProjectHandler#deleteFolder(File) deleteProject}
	 * method if {@code file} is a directory. Prints an error message if file or a children
	 * file couldn't be deleted.
	 * @param file File to be deleted.
	 * @return True if deleted properly or false if file or a children file couldn't
	 * be deleted.
	 */
	public boolean deleteFile(File file) {
		boolean success = false;
		if (file != null) {
			try {
				if (file.isDirectory()) {
					FolderHandler.deleteFolder(file);
				} else {
					JavaFileHandler.deleteFile(file);
				}
				success = true;
			} catch (IOException ex) {
				System.err.println("FileController.deleteFile: " + ex.getMessage());
			}
		}
		return success;
	}
	
	/**
	 * Tries to create a new package using {@link PackageHandler#createPackage(File)}.
	 * Prints error message if package can't be created.
	 * @param file The package file to be created.
	 * @return {@code true} if package was created, otherwise {@code false}
	 */
	public boolean createPackage(File file) {	
		try {
			PackageHandler.createPackage(file);
			return true;
		} catch (IOException ex) {
			System.err.println("FileController.createPackage: " + ex.getMessage());
			return false;
		}
	}
	
	//Project methods
	
	/**
	 * Tries to create a new project using {@link zenit.filesystem.ProjectHandler
	 * #createNewProject(File) createNewProject} method in the set workspace.
	 * Prints an error message if project or any of its files couldn't be created.
	 * @param projectname Name of the new project
	 * @return Returns File-object of the created project if created, otherwise null
	 */
	public File createProject(String projectname) {
		if (projectname != null) {
			try {
				File file = new File(workspace + "/" + projectname);
				ProjectHandler.createNewProject(file);
				return file;
			} catch (IOException ex) {
				System.err.println("FileController.createProject: IOException: " + 
						ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Tries to create or overwrite new {@code res/workspace/workspace.dat} file
	 * and set {@code workspace} instance variable to new {@code workspace} file.
	 * @param workspace The new workspace file
	 * @return True if changed, otherwise false.
	 */
	public boolean changeWorkspace(File workspace) {
		boolean success = WorkspaceHandler.createWorkspace(workspace);
		if (success) {
			this.workspace = workspace;
		}
		return success;
	}
}
