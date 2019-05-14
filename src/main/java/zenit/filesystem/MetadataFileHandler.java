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
import java.util.LinkedList;

/**
 * Method for creating a metadata file used during compilation
 * @author Alexander Libot
 *
 */
public class MetadataFileHandler extends FileHandler {
	
	public final static String LATEST_VERSION = "2.0.0";
	
	/**
	 * Tries to create a new .metadata file in project folder.
	 * Standard directory for class-files is "bin" and standard directory for java-files
	 * is "src"
	 * @param projectFile The folder where .metadata file is created in
	 * @throws IOException Throws IOException if file already exists or if
	 * {@link java.io.BufferedWriter BufferedWriter} throws an IOException.
	 */
	protected static File createMetadataFile(File projectFile) throws IOException {
		File metadataFile = new File(projectFile.getPath() + File.separator + ".metadata");
		
		boolean success = metadataFile.createNewFile();
		
		if (!success) {
			throw new IOException("File already exist");
		}

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadataFile), textEncoding));
		bw.write("ZENIT METADATA");
		bw.newLine();
		bw.write(LATEST_VERSION);
		bw.newLine();
		bw.write("DIRECTORY");
		bw.newLine();
		bw.write("bin");
		bw.newLine();
		bw.write("SOURCEPATH");
		bw.newLine();
		bw.write("src");
		bw.flush();
		bw.close();
		
		return metadataFile;
	}
	
	protected static File replaceMetadataFile(File metadata) throws IOException {
		LinkedList<String> lines = new LinkedList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadata), textEncoding)); 
		
		String line;
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		br.close();
		
		if (!lines.getFirst().equals("ZENIT METADATA")) {
			lines.addFirst(LATEST_VERSION);
			lines.addFirst("ZENIT METADATA");
		} else if (lines.getFirst().equals("ZENIT METADATA")) {
			if (!lines.get(1).equals(LATEST_VERSION)) {
				lines.set(1, LATEST_VERSION);
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadata), textEncoding));
		for (String readLine : lines) {
			bw.write(readLine);
			bw.newLine();
		}
		bw.close();
		
		return metadata;
		
		
	}
	
	protected static void addLibraryDependency(File metadata, File library) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadata), textEncoding)); 
		
		ArrayList<String> lines = new ArrayList<String>();
		String line;
		
		//Read all metadata before LIBRARIES
		while ((line = br.readLine()) != null && !line.equals("LIBRARIES")) {
			lines.add(line);
		}
		
		//If libraries don't exist, add "LIBRARIES"
		if (line == null || !line.equals("LIBRARIES")) {
			lines.add("LIBRARIES");
		} else if (line.equals("LIBRARIES")) {
			lines.add(line);
		}
		
		//Create libraryPath in project
		String libraryPath = library.getPath();
		File projectFile = metadata.getParentFile();
		String projectName = projectFile.getName();
		
		libraryPath = libraryPath.substring(libraryPath.lastIndexOf(projectName));
		libraryPath = libraryPath.substring(libraryPath.indexOf("lib"));
		
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		
		for (String readLine : lines) {
			if (readLine.equals(libraryPath)) {
				br.close();
				throw new IOException("Library already exist");
			}
		}
		lines.add(libraryPath);
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
