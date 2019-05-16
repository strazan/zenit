package main.java.zenit.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

public class Metadata {
	
	private LinkedList<String> lines;
	
	private int nbrOfRunnableClasses;
	private int nbrOfInternalLibraries;
	private int nbrOfExternalLibraries;
	
	private String version;
	private String directory;
	private String sourcepath;
	private String JREVersion;
	
	private RunnableClass[] runnableClasses;
	private String[] internalLibraries;
	private String[] externalLibraries;
	
	public Metadata(File metadata) {
		MetadataDecoder.decode(metadata, this);
	}
	
	public int getNbrOfRunnableClasses() {
		return nbrOfRunnableClasses;
	}




	public void setNbrOfRunnableClasses(int nbrOfRunnableClasses) {
		this.nbrOfRunnableClasses = nbrOfRunnableClasses;
	}




	public int getNbrOfInternalLibraries() {
		return nbrOfInternalLibraries;
	}




	public void setNbrOfInternalLibraries(int nbrOfInternalLibraries) {
		this.nbrOfInternalLibraries = nbrOfInternalLibraries;
	}




	public int getNbrOfExternalLibraries() {
		return nbrOfExternalLibraries;
	}




	public void setNbrOfExternalLibraries(int nbrOfExternalLibraries) {
		this.nbrOfExternalLibraries = nbrOfExternalLibraries;
	}




	public String getVersion() {
		return version;
	}




	public void setVersion(String version) {
		this.version = version;
	}




	public String getDirectory() {
		return directory;
	}




	public void setDirectory(String directory) {
		this.directory = directory;
	}




	public String getSourcepath() {
		return sourcepath;
	}




	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}




	public String getJREVersion() {
		return JREVersion;
	}




	public void setJREVersion(String jREVersion) {
		JREVersion = jREVersion;
	}




	public RunnableClass[] getRunnableClasses() {
		return runnableClasses;
	}




	public void setRunnableClasses(RunnableClass[] runnableClasses) {
		this.runnableClasses = runnableClasses;
	}




	public String[] getInternalLibraries() {
		return internalLibraries;
	}




	public void setInternalLibraries(String[] internalLibraries) {
		this.internalLibraries = internalLibraries;
	}




	public String[] getExternalLibraries() {
		return externalLibraries;
	}




	public void setExternalLibraries(String[] externalLibraries) {
		this.externalLibraries = externalLibraries;
	}

	public String toString() {
		String nl = "\n";
		String string = version + nl + directory + nl + sourcepath + nl + JREVersion + nl;
		if (runnableClasses != null) {
			for (RunnableClass rc : runnableClasses) {
				string += rc + nl;
			}
		}
		if (internalLibraries != null) {
			for (String il : internalLibraries) {
				string += il + nl;
			}
		}
		if (externalLibraries != null) {
			for (String el : externalLibraries) {
				string += el + nl;
			}
		}

		return string;
	}




	public static LinkedList<String> readMetadata(File metadata) throws IOException {
		
		if (!metadata.exists()) {
			throw new IOException("Metadata don't exist");
		}
		
		LinkedList<String> lines = new LinkedList<String>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadata), "UTF-8"))) {
			String line = br.readLine();
			
			while (line != null) {
				lines.add(line);
				line = br.readLine();
			}
			return lines;	
		} catch (IOException ex) {
			throw new IOException("Couldn't read metadata");
		}
	}
	
	public static void writeMetadata(File metadata, List<String> lines) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metadata), "UTF-8"))) {
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
		} catch (IOException ex) {
			throw new IOException("Couldn't write to metadata");
		}
	}
}
