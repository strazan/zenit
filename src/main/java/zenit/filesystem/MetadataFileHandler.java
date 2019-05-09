package main.java.zenit.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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

		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadataFile), textEncoding));
		br.write("DIRECTORY");
		br.newLine();
		br.write("bin");
		br.newLine();
		br.write("SOURCEPATH");
		br.newLine();
		br.write("src");
		br.flush();
		br.close();
	}
	
	protected static void addLibraryDependency(File metadata, File library) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadata), textEncoding)); 
		
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null && !line.equals("LIBRARIES")) {
			lines.add(line);
		}
		
		if (line == null || !line.equals("LIBRARIES")) {
			lines.add("LIBRARIES");
		}
		String libraryPath = library.getPath();
		File projectFile = metadata.getParentFile();
		String projectName = projectFile.getName();
		
		libraryPath = libraryPath.substring(libraryPath.lastIndexOf(projectName));
		libraryPath = libraryPath.substring(libraryPath.indexOf("lib"));
		
		lines.add(libraryPath);
		
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		
		br.close();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadata), textEncoding)); 
		
		for (String writeLine : lines) {
			bw.write(writeLine);
			bw.newLine();
		}
		
		bw.close();
	}
	
	//TODO Ability to add classpath when importing libraries
	
	//TODO Ability to remove classpath when removing imported libraries

}
