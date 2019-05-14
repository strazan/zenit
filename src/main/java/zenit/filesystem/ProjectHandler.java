package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
			String packagePath = file.getPath() + "/src/default_package";
			File packageFile = new File(packagePath);
			
			String binPath = file.getPath() + "/bin";
			File binFile = new File(binPath);
			
			PackageHandler.createPackage(packageFile);
			PackageHandler.createPackage(binFile);
			MetadataFileHandler.createMetadataFile(file);

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
			MetadataFileHandler.createMetadataFile(target);
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
	
	protected static boolean libFolderExists(File projectFolder) {
		File libFolder = new File(projectFolder+File.separator+"lib");
		return libFolder.exists();
	}
	
	protected static File createLibFolder(File projectFolder) {
		File libFolder = new File(projectFolder+File.separator+"lib");
		boolean success = libFolder.mkdir();
		
		if (success) {
			return libFolder;
		} else {
			return null;
		}
		
	}
	
	protected static void importJar(File sourceFile, File destinationFolder) throws IOException {
		destinationFolder = new File(destinationFolder + File.separator + sourceFile.getName());
		File destinationFile = null;
		
		destinationFile = Files.copy(sourceFile.toPath(), destinationFolder.toPath()).toFile();
		
		File[] files = destinationFolder.getParentFile().getParentFile().listFiles();
		File metadata = null;
		
		for (File file : files) {
			if (file.getName().equals(".metadata")) {
				metadata = file;
				break;
			}
		}
	
		if (metadata != null) {
			MetadataFileHandler.addLibraryDependency(metadata, destinationFolder);
		}
		
		if (destinationFile == null) {
			throw new IOException(sourceFile.getName() + " couldn't be imported");
		}
	}
	
	protected static void removeJar(File fileToRemove, File projectFile) throws IOException {
		
	}
}
