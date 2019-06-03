package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;

import main.java.zenit.filesystem.metadata.Metadata;

/**
 * Project specific methods for manipulating the file system.
 * Contains method for creating projects in the file system.
 * Only to be accessed through {@link FileController} methods.
 * @author Alexander Libot
 *
 */
public class ProjectHandler extends FolderHandler {
		
	/**
	 * Creates a new project with a src folder with a default-package and a bin folder.
	 * @param file File of the project folder.
	 * @throws IOException Throws {@link java.io.IOException IOException} if project or
	 * any folder inside project can't be created.
	 */
	protected static void createNewProject(File file) throws IOException {
		
		boolean success = file.mkdir();
		
		if (success) {
			ProjectFile projectFile = new ProjectFile(file);
			projectFile.addSrc();
			projectFile.addBin();
			
			File metadata = MetadataFileHandler.createMetadataFile(file);
			projectFile.setMetadata(metadata);
			

		} else {
			throw new IOException("Couldn't create project");
		}
	}
	
	/**
	 * Imports a project from source folder to target folder
	 * @param source Folder to copy from
	 * @param target Folder to copy to
	 * @return The target File
	 * @throws IOException if project can't be imported
	 */
	protected static File importProject(File source, File target) throws IOException {
		
		String targetFilepath = target.getPath();
		String projectName = source.getName();
		
		targetFilepath += File.separator + projectName;
		target = new File(targetFilepath);
		
		if (target.exists() ) {
			throw new IOException("A project with that name already exists");
		}
		boolean success = target.mkdir();
		
		if (success) {
			copyFolder(source, target);
			File[] files = target.listFiles();
			boolean metadataMissing = true;
			for (File file : files) {
				if (file.getName().equals(".metadata")) {
					metadataMissing = false;
				}
			}
			if (metadataMissing) {
				MetadataFileHandler.createMetadataFile(target);
			}
			return target;
		} else {
			throw new IOException("Couldn't copy file");
		}
	}
	
	/**
	 * Recursively copies folders and files from a source folder to a destination folder.
	 * @param sourceFolder Folder to copy from
	 * @param destinationFolder Folder to copy to
	 * @throws IOException
	 */
	private static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory()) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }
             
            //Get all files from source directory
            String files[] = sourceFolder.list();
             
            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);
                 
                //Recursive function call
                copyFolder(srcFile, destFile);
            }
        } else {
            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath());
        }
    }
	
	/**
	 * Tries to copy libraryFiles parameter to projects lib-folder (creates one if it doesn't 
	 * exist). Adds all the internal build paths to metadata.
	 * @param libraryFiles Internal library files to copy. Should contain full file path from root
	 * @param projectFile Project to copy library files to
	 * @return {@code true} if added successfully, otherwise {@code false}. Note that files can be
	 * copied but still return {@code false}
	 * 
	 * @throws IOException
	 */
	protected static boolean addInternalLibraries(List<File> libraryFiles, ProjectFile projectFile)
			throws IOException {
		//Setup variables
		String[] internalLibraries = new String[libraryFiles.size()];
		int counter = 0;
		File libFolder = projectFile.addLib();
		File destinationFile;
		String libraryName;
		String internalLibraryPath;
		
		//Create internal file path and add to array
		for (File libraryFile : libraryFiles) {
			libraryName = libraryFile.getName();
			destinationFile = new File(libFolder + File.separator + libraryName);
			
			Files.copy(libraryFile.toPath(), destinationFile.toPath());
			
			internalLibraryPath = destinationFile.getPath();
			internalLibraryPath = internalLibraryPath.replaceFirst(Matcher.quoteReplacement(
					projectFile.getPath() + File.separator), "");
			internalLibraries[counter++] = internalLibraryPath;
		}
		
		//Add to metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingInternalLibraries = metadata.getInternalLibraries();
		
		int eilLength = 0;
		if (existingInternalLibraries != null) {
			eilLength = existingInternalLibraries.length;
		}
		
		String[] newInternalLibraries = new String[eilLength + internalLibraries.length];
		for (int i = 0; i < newInternalLibraries.length; i++) {
			if (i < eilLength) {
				newInternalLibraries[i] = existingInternalLibraries[i];
			} else {
				newInternalLibraries[i] = internalLibraries[i - eilLength];
			}
		}
		
		metadata.setInternalLibraries(newInternalLibraries);
		return metadata.encode();
	}
	
	/**
	 * Tries to remove internalLibraryPathsList parameter from lib-folder and remove their build
	 * path.
	 * @param internalLibraryPathsList Internal file paths to remove. Should only contain file path
	 * within project. Correct: {@code lib/library.jar}
	 * @param projectFile Project to remove internal libraries from
	 * @return {@code true} if removed successfully, otherwise {@code false}. Note that files can be
	 * removed but still return {@code false}
	 */
	protected static boolean removeInternalLibraries(List<String> internalLibraryPathsList, 
			ProjectFile projectFile) {
		//Setup variables
		String[] internalLibraryPaths = new String[internalLibraryPathsList.size()];
		int counter = 0;	
		String libraryPath;
		File library;
		
		//Add full file path and add to array
		for (String internalLibraryPath : internalLibraryPathsList) {
			libraryPath = projectFile.getPath() + File.separator + internalLibraryPath;
			library = new File(libraryPath);
			if (library.delete() == false) {
				return false;
			}
			internalLibraryPaths[counter++] = internalLibraryPath;
		}
		
		//Remove from metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingInternalLibraries = metadata.getInternalLibraries();
		String[] newInternalLibraries = new String[existingInternalLibraries
		                                           .length-internalLibraryPaths.length];
		counter = 0;
		boolean add = true;
		
		for (int i = 0; i < existingInternalLibraries.length; i++) {
			for (int j = 0; j < internalLibraryPaths.length; j++) {
				if (existingInternalLibraries[i].equals(internalLibraryPaths[j])) {
					add = false;
				}
			}
			if (add) {
				newInternalLibraries[counter++] = existingInternalLibraries[i];
			}
			add = true;
		}
		
		metadata.setInternalLibraries(newInternalLibraries);
		return metadata.encode();
	}
	
	/**
	 * Tries to copy libraryFiles parameter to projects lib-folder (creates one if it doesn't 
	 * exist).
	 * @param externalLibraryFiles External library files to add to build path. Should contain 
	 * full file path from root
	 * @param projectFile Project to add build paths to
	 * @return {@code true} if added successfully, otherwise {@code false}.
	 * 
	 * @throws IOException
	 */
	protected static boolean addExternalLibraries(List<File> externalLibraryFiles, 
			ProjectFile projectFile) {
		//Setup variables
		String[] externalLibraries = new String[externalLibraryFiles.size()];
		int counter = 0;
		
		//Add to array
		for (File externalLibrary : externalLibraryFiles) {
			externalLibraries[counter++] = externalLibrary.getPath();
		}
		
		//Add to metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingExternalLibraries = metadata.getExternalLibraries();
		
		int eelfLength = 0;
		if (existingExternalLibraries != null) {
			eelfLength = existingExternalLibraries.length;
		}
		
		String[] newExistingExternalLibraries = new String[externalLibraries.length + eelfLength];
		
		for (int i = 0; i < newExistingExternalLibraries.length; i++) {
			if (i < eelfLength) {
				newExistingExternalLibraries[i] = existingExternalLibraries[i];
			} else {
				newExistingExternalLibraries[i] = externalLibraries[i-eelfLength];
			}
		}
		
		metadata.setExternalLibraries(newExistingExternalLibraries);
		return metadata.encode();
	}
	
	/**
	 * Tries to remove build path to all files with file path in {@code externalLibrariesList}.
	 * @param externalLibraryPathsList External file paths to remove build path to.
	 * @param projectFile Project to remove external libraries build path from.
	 * @return {@code true} if build path removed successfully, otherwise {@code false}.
	 */
	protected static boolean removeExternalLibraries(List<String> externalLibraryPathsList, 
			ProjectFile projectFile) {
		//Setup variables
		String[] externalLibraries = new String[externalLibraryPathsList.size()];
		int counter = 0;
		
		//Add to array
		for (String externalLibrary : externalLibraryPathsList) {
			externalLibraries[counter++] = externalLibrary;
		}
		
		//Remove from metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingExternalLibraries = metadata.getExternalLibraries();
		
		int eelLength = 0;
		if (existingExternalLibraries != null) {
			eelLength = existingExternalLibraries.length;
		}
		
		String[] newExternalLibraries = new String[eelLength-externalLibraries.length];
		counter = 0;
		boolean add = true;
		
		for (int i = 0; i < eelLength; i++) {
			for (int j = 0; j < externalLibraries.length; j++) {
				if (existingExternalLibraries[i].equals(externalLibraries[j])) {
					add = false;
				}
			}
			if (add) {
				newExternalLibraries[counter++] = existingExternalLibraries[i];
			}
			add = true;
		}
		
		metadata.setExternalLibraries(newExternalLibraries);
		return metadata.encode();
	}
}
