package main.java.zenit.filesystem.metadata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import main.java.zenit.filesystem.RunnableClass;

/**
 * Encodes a {@link Metadata} object to file.
 * @author Alexander Libot
 *
 */
public class MetadataEncoder {
	
	/**
	 * Writes the parameters from {@link Metadata} object along with keywords to file.
	 * @param metadataFile File to write to
	 * @param metadata Object to extract data from
	 */
	public static boolean encode(File metadataFile, Metadata metadata) {
		ArrayList<String> lines = new ArrayList<String>();
		
		String line;
		
		//Version
		line = metadata.getVersion();
		if (line != null) {
			lines.add("ZENIT METADATA");
			lines.add(line);
		}
		
		//Directory
		line = metadata.getDirectory();
		if (line != null) {
			lines.add("DIRECTORY");
			lines.add(line);
		}
		
		//Sourcepath
		line = metadata.getSourcepath();
		if (line != null) {
			lines.add("SOURCEPATH");
			lines.add(line);
		}
		
		//JRE Version
		line = metadata.getJREVersion();
		if (line != null) {
			lines.add("JRE VERSION");
			lines.add(line);
		} else {
			lines.add("JRE VERSION");
			lines.add("unknown");
		}
		
		//Runnable classes
		RunnableClass[] runnableClasses = metadata.getRunnableClasses();
		if (runnableClasses != null) {
			RunnableClass runnableClass;
			int nbrOfRunnableClasses = runnableClasses.length;
			lines.add("RUNNABLE CLASSES");
			lines.add(Integer.toString(nbrOfRunnableClasses));
			for (int i = 0; i < nbrOfRunnableClasses; i++) {
				runnableClass = runnableClasses[i];
				lines.add("RCLASS");
				lines.add(runnableClass.getPath());
				lines.add("PROGRAM ARGUMENTS");
				lines.add(runnableClass.getPaArguments());
				lines.add("VM ARGUMENTS");
				lines.add(runnableClass.getVmArguments());
				lines.add("/RCLASS");	
			}
		} else {
			lines.add("RUNNABLE CLASSES");
			lines.add("0");
		}
		
		//Internal libraries
		String[] internalLibraries = metadata.getInternalLibraries();
		if (internalLibraries != null) {
			int nbrOfInternalLibraries = internalLibraries.length;
			lines.add("INTERNAL LIBRARIES");
			lines.add(Integer.toString(nbrOfInternalLibraries));
			for (int i = 0; i < nbrOfInternalLibraries; i++) {
				lines.add(internalLibraries[i]);
			}
		} else {
			lines.add("INTERNAL LIBRARIES");
			lines.add("0");
		}
		
		//External libraries
		String[] externalLibraries = metadata.getExternalLibraries();
		if (externalLibraries != null) {
			int nbrOfExternalLibraries = externalLibraries.length;
			lines.add("EXTERNAL LIBRARIES");
			lines.add(Integer.toString(nbrOfExternalLibraries));
			for (int i = 0; i < nbrOfExternalLibraries; i++) {
				lines.add(externalLibraries[i]);
			}
		} else {
			lines.add("EXTERNAL LIBRARIES");
			lines.add("0");
		}
		
		//Write to file
		try {
			writeMetadata(metadataFile, lines);
			return true;
		} catch (IOException e) {
			System.err.println(e);
			return false;
		}
		
	}
	
	/**
	 * Writes lines to file
	 * @param metadataFile  File to write to
	 * @param lines Lines to write to file
	 * @throws IOException
	 */
	private static void writeMetadata(File metadataFile, ArrayList<String> lines) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadataFile), "UTF-8"))) {
			for (String writeLine : lines) {
				bw.write(writeLine);
				bw.newLine();
			}
		} catch (IOException e) {
			throw new IOException("Can't encode metadata");
		}	
	}
}
