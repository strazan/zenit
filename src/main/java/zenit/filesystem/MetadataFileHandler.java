package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import main.java.zenit.filesystem.jreversions.JREVersions;
import main.java.zenit.filesystem.metadata.Metadata;

/**
 * Method for creating a metadata file used during compilation
 * @author Alexander Libot
 *
 */
public class MetadataFileHandler extends FileHandler {
	
	public final static String LATEST_VERSION = "2.2.1";
	
	/**
	 * Tries to create a new .metadata file in project folder.
	 * Standard directory for class-files is "bin" and standard directory for java-files
	 * is "src".
	 * @param projectFile The folder where .metadata file is created in
	 * @throws IOException Throws IOException if file already exists.
	 */
	protected static File createMetadataFile(File projectFile) throws IOException {
		File metadataFile = new File(projectFile.getPath() + File.separator + ".metadata");
		boolean success = metadataFile.createNewFile();
		
		if (!success) {
			throw new IOException("File already exist");
		}
		
		Metadata metadata = new Metadata(metadataFile);
		metadata.setVersion(LATEST_VERSION);
		metadata.setDirectory("bin");
		metadata.setSourcepath("src");
		File JDK = JREVersions.getDefaultJDKFile();
		if (JDK != null) {
			metadata.setJREVersion(JDK.getPath());
		}
		metadata.setRunnableClasses(null);
		metadata.setInternalLibraries(null);
		metadata.setExternalLibraries(null);
		
		metadata.encode();
		
		return metadataFile;
	}
	
	/**
	 * Updates the metadata-file with the requirements for the latest version
	 * @param metadataFile The metadata file to update
	 * @return Updated {@link main.java.zenit.filesystem.Metadata Metadata} object if updated,
	 * otherwhise {@code null}
	 */
	protected static Metadata updateMetadata(File metadataFile) {
		Metadata metadata = new Metadata(metadataFile);
		
		if (metadata.getVersion() == null || !metadata.getVersion().equals(LATEST_VERSION)) {
			metadata.setVersion(LATEST_VERSION);
		}
		if (metadata.getDirectory() == null) {
			metadata.setDirectory("bin");
		}
		if (metadata.getSourcepath() == null) {
			metadata.setSourcepath("src");
		}
		if (metadata.getJREVersion() == null || metadata.getJREVersion().equals("unknown")) {
			String JRE = JREVersions.getDefaultJDKFile().getPath();
			metadata.setJREVersion(JRE);
		}
		if (metadata.getRunnableClasses() == null) {
			metadata.setRunnableClasses(null);
		}
		if (metadata.getInternalLibraries() == null) {
			metadata.setInternalLibraries(null);
		}
		if (metadata.getExternalLibraries() == null) {
			metadata.setExternalLibraries(null);
		}
		
		if (metadata.encode()) {
			return metadata;
		} else {
			return null;
		}		
	}
	
	/**
	 * Changes the compile directory to directory parameter in project.
	 * @param directory New directory
	 * @param projectFile Project to change directory in
	 * @param internal {@code true} if directory path should be internal, otherwise {@code false}
	 * @return The new directory path
	 */
	protected static String changeDirectory(File directory, ProjectFile projectFile,
			boolean internal) {
		String directoryPath = directory.getPath();
		
		if (internal) {
			directoryPath = directoryPath.replaceFirst(Matcher.quoteReplacement(
					projectFile.getPath() + File.separator), "");
		}
		
		Metadata metadata = new Metadata(projectFile.getMetadata());
		metadata.setDirectory(directoryPath);
		metadata.encode();
		
		return directoryPath;
	}
	
	/**
	 * Changes the compile source path to directory parameter in project.
	 * @param directory New source path directory
	 * @param projectFile Project to change directory in
	 * @param internal {@code true} if source path should be internal, otherwise {@code false}
	 * @return The new source path
	 */
	protected static String changeSourcepath(File directory, ProjectFile projectFile,
			boolean internal) {
		String sourcepath = directory.getPath();
		
		if (internal) {
			sourcepath = sourcepath.replaceFirst(Matcher.quoteReplacement(
					projectFile.getPath() + File.separator), "");
		}
		
		Metadata metadata = new Metadata(projectFile.getMetadata());
		metadata.setSourcepath(sourcepath);
		metadata.encode();
		
		return sourcepath;
	}
}
