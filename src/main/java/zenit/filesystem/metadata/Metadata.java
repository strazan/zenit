package main.java.zenit.filesystem.metadata;

import java.io.File;

import main.java.zenit.filesystem.RunnableClass;

/**
 * Abstract data type containing data from metadata file. Decodes a metadata file upon creation.
 * Contains methods to retrieve and update data. Uses {@link #encode()} to write changes to file.
 */
public class Metadata {
	
	private File metadataFile;
	
	private String version;
	private String directory;
	private String sourcepath;
	private String JREVersion;
	
	private RunnableClass[] runnableClasses;
	private String[] internalLibraries;
	private String[] externalLibraries;
	
	/**
	 * Creates a new metadata object and decodes the file in parameter to fill object attributes.
	 * @param metadataFile The metadata-file to decode.
	 */
	public Metadata(File metadataFile) {
		this.metadataFile = metadataFile;
		MetadataDecoder.decode(metadataFile, this);
	}
	
	/**
	 * Encodes all the objects attributes to file, replaces the current metadata-file.
	 * @return {@code true} if encoded correctly, otherwise {@code false}
	 */
	public boolean encode() {
		return MetadataEncoder.encode(metadataFile, this);
	}
	
	/**
	 * Returns the metadata-file
	 */
	public File getMetadataFile() {
		return metadataFile;
	}

	/**
	 * Sets a new metadata-file
	 * @param metadataFile
	 */
	public void setMetadataFile(File metadataFile) {
		this.metadataFile = metadataFile;
	}

	/**
	 * Returns the metadata version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets a new metadata version
	 * @param version New version number
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Returns the directory path
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * Sets a new directory path
	 * @param directory
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * Returns the source path
	 */
	public String getSourcepath() {
		return sourcepath;
	}

	/**
	 * Sets a new source path
	 * @param sourcepath
	 */
	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}

	/**
	 * Returns the JRE version
	 */
	public String getJREVersion() {
		return JREVersion;
	}

	/**
	 * Sets a new JRE version
	 */
	public void setJREVersion(String jREVersion) {
		JREVersion = jREVersion;
	}

	/**
	 * Returns an array of {@link main.java.zenit.filesystem.RunnableClass RunnableClass} objects
	 */
	public RunnableClass[] getRunnableClasses() {
		return runnableClasses;
	}

	/**
	 * Sets a new array of {@link main.java.zenit.filesystem.RunnableClass RunnableClass} objects
	 */
	public void setRunnableClasses(RunnableClass[] runnableClasses) {
		this.runnableClasses = runnableClasses;
	}

	/**
	 * Returns a string array with internal library paths
	 */
	public String[] getInternalLibraries() {
		return internalLibraries;
	}

	/**
	 * Sets a new string array with internal library paths
	 * @param internalLibraries
	 */
	public void setInternalLibraries(String[] internalLibraries) {
		this.internalLibraries = internalLibraries;
	}

	/**
	 * Returns a string array with external library paths
	 */
	public String[] getExternalLibraries() {
		return externalLibraries;
	}

	/**
	 * Sets a new string array with external library paths
	 */
	public void setExternalLibraries(String[] externalLibraries) {
		this.externalLibraries = externalLibraries;
	}

	/**
	 * Converts all attributes to string
	 */
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
}