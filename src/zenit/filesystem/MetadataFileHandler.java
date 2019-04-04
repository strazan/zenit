package zenit.filesystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Method for creating a metadata file used during compilation
 * @author Alexander Libot
 *
 */
public class MetadataFileHandler extends FileHandler {
	
	/**
	 * Tries to create a new .metadata file in project folder.
	 * Standard directory for class-files is "bin" and standard directory for java-files
	 * is "src"
	 * @param projectFile The folder where .metadata file is created in
	 * @throws IOException Throws IOException if file already exists or if
	 * {@link java.io.BufferedWriter BufferedWriter} throws an IOException.
	 */
	protected static void createMetadataFile(File projectFile) throws IOException {
		File metadataFile = new File(projectFile.getPath() + "/.metadata");
		
		boolean success = metadataFile.createNewFile();
		
		if (!success) {
			throw new IOException("File already exist");
		}
		
		String metadata = "-d \"bin\"\n"
						+ "-sourcepath \"src\"";

		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadataFile), textEncoding));
		br.write(metadata);
		br.flush();
		br.close();
	}
	
	//TODO Ability to add classpath when importing libraries
	
	//TODO Ability to remove classpath when removing imported libraries

}
