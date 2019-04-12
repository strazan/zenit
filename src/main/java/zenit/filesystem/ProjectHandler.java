package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

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
}
