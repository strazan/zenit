package zenit.filesystem.controller;

import java.io.File;
import java.io.IOException;

import zenit.filesystem.handlers.ClassHandler;
import zenit.filesystem.handlers.PackageHandler;
import zenit.filesystem.handlers.ProjectHandler;
import zenit.filesystem.handlers.WorkspaceHandler;

/**
 * Class for controlling and manipulating the filesystem of a project.
 * TODO This is currently a big mess due to an extreme makeover. Will look nicer in
 * next commit :)
 * @author Alexander Libot
 *
 */
public class FileController {
	
	private File workspace; //Used as a base-file for all files

	//Constructors
	
	/**
	 * Creates a new file controller to manipulate the filesystem
	 */
	public FileController(File workspace) {
		this.workspace = workspace;
		
		//TODO Connect to JavaFX-controller
//		this.gui = gui;
//		gui.setController(this);
//		gui.initTree(workspace);
	}
	
	//Getters
	/**
	 * Returns the current workspace-file
	 */
	public File getWorkspace() {
		return workspace;
	}
	
	//Class methods
	
	/**
	 * Creates a new .java file with specified name in specified folder
	 * @param filepath The folder where the class will be created
	 * @param filename The name of the class
	 */
//	public void createFile(String filepath, String filename) {
//		if (filepath != null && filename != null) {
//			filepath = workspace.getPath() + filepath + filename + ".java";
//
//			try {
//				File file = ClassHandler.createFile(filepath);
//				readFile(file);
//				
//				//TODO Change to JavaFX-controller
//				gui.addNode(file.getName());
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	public File createFile(int typeCode, File file, String content) {
		if (file != null) {
			try {
				return ClassHandler.createFile(typeCode, file, content);
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return null;
	}
	
	public File createFile(int typeCode, File file) {
		return createFile(typeCode, file, null);
	}
	
	/**
	 * Reads a java-file from disk. 
	 * @param file The file to read from disk.
	 */
	public String readFileContent(File file) {
		if (file != null) {
			return ClassHandler.readFile(file);
		}
		return null;
	}
	
	
	/**
	 * Reads a java-file from disk. Only used when filepath is within workspace.
	 * Concatenates the workspace path and the input path.
	 * Uses the readFile(File) for reading.
	 * @param filepath Path to read within workspace.
	 */
	public void readFile(String filepath) {
		if (filepath != null) {
			filepath = workspace.getPath() + filepath;
			File file = new File(filepath);
			readFileContent(file);
		}
	}
	
	/**
	 * Writes a java-file to disk. Concatenates the workspace filepath and the input filepath.
	 * @param file The file to write over.
	 * @param dsd The content to write to disk
	 */
	public boolean writeFile(File file, String content) {

		if (file != null && content != null) {
			return ClassHandler.saveFile(file, content); //Tries to save file
		}
		
		return false;
	}
	
	/**
	 * Renames a class. Concatenates the workspace filepath with the input filepath
	 * @param filepath The class to be renamed. Must be filepath within workspace
	 * @param newFilename The new name of the class
	 */
//	public void renameFile(String filepath, String newFilename) {
//		if (filepath != null && newFilename != null) {
//			try {
//				filepath = workspace.getPath() + filepath;
//				File file = new File(filepath);
//				File newFile = ClassHandler.renameFile(file, newFilename);
//				readFileContent(newFile);
//
//				// TODO Change to JavaFX-controller
////				gui.renameNode(newFile.getName());
////				gui.openDialog("Renaming was successful", "Renamed file", JOptionPane.PLAIN_MESSAGE);
//			} catch (IOException ex) {
//				System.err.println(ex.getMessage());
////				gui.openDialog("Renaming was unsuccessful", "Renaming failed", JOptionPane.WARNING_MESSAGE);
//			}
//		}
//	}

	public File renameFile(File file, String newName) {
		File newFile = null;
		if (file != null && newName != null) {
			try {
				if (file.isDirectory()) {
					newFile = ProjectHandler.renameProject(file, newName);
				} else {
					newFile = ClassHandler.renameFile(file, newName);
				}
			} catch (IOException ex) {
				System.err.println("FileController.renameFile: " + ex.getMessage());
			}
		}
		return newFile;
	}
	
	/**
	 * Deletes a class from disk. Concatenates the workspace filepath with the input filepath.
	 * @param filepath The class to be deleted. Must be filepath within workspace 
	 */
//	public void deleteFile(String filepath) {
//		filepath = workspace + filepath;
//		File file = new File(filepath);
//		
//		boolean success = ClassHandler.deleteFile(file);
//		
//		if (success) {
////			gui.openDialog("Deletion was successful", "File deleted", JOptionPane.PLAIN_MESSAGE);
////			gui.clearTextPane();
////			gui.setOpenFile(FileNameHelpers.getFilepathWithoutTopFile(file));
////			gui.deleteNode();
//			
//		} else {
////			gui.openDialog("Deletion was unsuccessful", "Deletion failed", JOptionPane.WARNING_MESSAGE);		
//		}
//	}
	
	public boolean deleteFile(File file) {
		boolean success = false;
		if (file != null) {
			try {
				if (file.isDirectory()) {
					ProjectHandler.deleteProject(file);
				} else {
					ClassHandler.deleteFile(file);
				}
				success = true;
			} catch (IOException ex) {
				System.err.println("FileController.deleteFile: " + ex.getMessage());
			}
		}
		return success;
	}
	
//	public void deleteProject(File file) {
//		if (file != null) {
//			try {
//				
//			} catch (IOException ex) {
//				
//			}
//		}
//	}
	
	
	//Package methods
	
	/**
	 * Creates a new package inside the selected filepath.
	 * Concatenates the workspace filepath, input filepath and packagename.
	 * @param filepath The filepath within the workspace where package is created
	 * @param packageName The name of the package
	 */
	public void createPackage(String filepath, String packageName) {
		filepath = workspace + filepath + packageName;
		
		File file = new File(filepath);
		boolean success = PackageHandler.createNewPackage(file);
		
		if (success) {
//			gui.addNode(file.getName());
		} else {
//			gui.openDialog("Couldn't create new package", "Package not created", JOptionPane.WARNING_MESSAGE);
		}
	}
	
//	public void openPackage(String filepath) {
//		filepath = workspace + filepath;
//		
////		gui.setOpenFile(new File(filepath));
////		gui.clearTextPane();
//	}
//	
//	public void savePackage() {
//		//TODO
//	}
	
	
	/**
	 * Rename package
	 * @param filepath Path of the package to rename
	 * @param packagename New name of package
	 */
//	public void renamePackage(File filepath, String packagename) {
//		if (filepath != null && packagename != null) {
//			try {
//				File newFilepath = PackageHandler.renamePackage(filepath, packagename);
//				
//				//TODO Change GUI methods
////				gui.setOpenFile(newFilepath);
////				gui.openDialog("Renaming was successful", "Renamed file", JOptionPane.PLAIN_MESSAGE);
////				gui.initTree(workspace);
//			} catch (IOException ex) {
//				System.err.println(ex.getMessage());
////				gui.openDialog("Renaming was unsuccessful", "Renaming failed", JOptionPane.WARNING_MESSAGE);
//			}
//			
//		}
//	}
	
//	public void renamePackage(String filepath, String packagename) {
//		try {
//			File file = new File(workspace + filepath);
//			File newFile = PackageHandler.renamePackage(file, packagename);
//			
////			gui.renameNode(newFile.getName());
////			gui.openDialog("Renaming was successful", "Renamed package", JOptionPane.PLAIN_MESSAGE);
//		} catch (IOException ex) {
//			System.err.println(ex.getMessage());
////			gui.openDialog("Renaming was unsuccessful", "Renaming failed", JOptionPane.WARNING_MESSAGE);
//		}
//	}
	
//	public void deletePackage(String filepath) {
//		filepath = workspace + filepath;
//		File file = new File(filepath);
//		
//		try {
//			PackageHandler.deletePackage(file);
//			
////			gui.openDialog("Deletion was successful", "File deleted", JOptionPane.PLAIN_MESSAGE);
////			gui.clearTextPane();
////			gui.setOpenFile(FileNameHelpers.getFilepathWithoutTopFile(file));
////			gui.deleteNode();
//			
//		} catch (IOException ex) {
////			gui.openDialog("Deletion was unsuccessful", "Deletion failed", JOptionPane.WARNING_MESSAGE);		
//		}
//		
//	}
	
	//Project methods
	//TODO Update to new filetree-system
	
	/**
	 * Creates a new project with a src-folder and a default-package
	 * @param filepath Folder where the new project should be put
	 * @param projectname Name of the new project
	 */
//	public void createProject(File filepath, String projectname) {
//		if (filepath != null && projectname != null) {
//			File newFilepath = ProjectHandler.createNewProject(filepath, projectname); //Create new project using handler
//
////			gui.setOpenFile(newFilepath);
//		}
//	}
	
	public File createProject(String projectname) {
		if (projectname != null) {
			try {
				File file = new File(workspace + "/" + projectname);
				ProjectHandler.createNewProject(file);
				return file;
			} catch (IOException ex) {
				System.err.println("FileController.createProject: IOException: " + ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Opens an existing project
	 * @param filepath The path of the project
	 */
//	public void openProject(String filepath) {
//		if (filepath != null) {
//			filepath = workspace + filepath;
//			File file = new File(filepath);
////			gui.setOpenFile(file);
////			gui.clearTextPane();
//		}
//	}
	
	/**
	 * Saves all files in a project
	 */
	public void saveProject() {
		//TODO
	}
	
	/**
	 * Renames the project
	 * @param file The path of the project
	 * @param projectname The new name of the project
	 */
//	public void renameProject(String filepath, String newProjectName) {
//		if (filepath != null && newProjectName != null) {
//			try {
//				filepath = workspace + filepath;
//				File file = new File(filepath);
//				File newFilepath = ProjectHandler.renameProject(file, newProjectName);
//
////				gui.setOpenFile(newFilepath);
////				gui.renameNode(newFilepath.getName());
//				
//			} catch (IOException ex) {
////				gui.openDialog("Couldn't rename project", "Rename failed", JOptionPane.WARNING_MESSAGE);
//			}
//		}
//	}
	
	/**
	 * Deletes the project and all files in it
	 * @param filepath The path of the project or any of the files/folders inside the project that should be deleted
	 */
//	public void deleteProject(String filepath) {
//		if (filepath != null) {
//			try {
//				filepath = workspace + filepath;
//				File file = new File(filepath);
//				ProjectHandler.deleteProject(file);
//
////				gui.openDialog("Deletion was successful", "Project deleted", JOptionPane.PLAIN_MESSAGE);
////				gui.clearTextPane();
////				gui.setOpenFile(null);
////				gui.deleteNode();
//			} catch (IOException ex) {
////				gui.openDialog(ex.getMessage(), "Deletion failed", JOptionPane.WARNING_MESSAGE);
//			}
//		}
//	}
	

	
//	public boolean createWorkspace() {
//		
//	}
//	
	public boolean changeWorkspace(File workspace) {
		return WorkspaceHandler.createWorkspace(workspace);
	}
//	
//	public boolean deleteWorkspace() {
//		
//	}
}
