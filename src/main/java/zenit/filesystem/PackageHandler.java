package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * Package specific methods for manipulating the file system.
 * Only to be accessed through {@link FileController} methods.
 * @author Alexander Libot
 *
 */
public class PackageHandler extends FolderHandler {

	/**
	 * Creates a new package-folder using {@link FolderHandler FolderHandler}
	 * @param file Package folder to be created.
	 * @throws IOException See {@link FolderHandler#createNewFolder(File)}
	 */
	protected static void createPackage(File file) throws IOException {
		if (file.getName().equals("package")) {
			throw new IOException("Can't name package: " + file.getName());
		}
		createNewFolder(file);
	}
	
}
