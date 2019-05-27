package main.java.zenit.filesystem.metadata;

import java.io.File;

import main.java.zenit.filesystem.MetadataFileHandler;

/**
 * Used to verify that a metadata-file is good to use.
 * @author Alexander Libot
 *
 */
public class MetadataVerifier {
	
	public static final int VERIFIED = 0;
	public static final int METADATA_FILE_MISSING = 1;
	public static final int METADATA_OUTDATED = 2;
	
	/**
	 * Checks if metadata file exists and is the latest version. Only returns one error code.
	 * @param metadata Metadata object to verify
	 * @return The return code, 0 if verified, 1 if metadata-file is missing and
	 * 2 if metadata-file is outdated
	 */
	public static int verify(Metadata metadata) {
		
		if (metadataFileMissing(metadata)) {
			return METADATA_FILE_MISSING;
		}
		if (metadataOutdated(metadata)) {
			return METADATA_OUTDATED;
		}
		
		return VERIFIED;	
	}
	
	/**
	 * Checks if metadata-file is missing
	 * @param metadata Metadata object to check
	 * @return {@code false} if metadata-file exists, otherwise {@code true}
	 */
	private static boolean metadataFileMissing(Metadata metadata) {
		File metadataFile = null;
		if (metadata != null) {
			metadataFile = metadata.getMetadataFile();
		}
		if (metadataFile == null || !metadataFile.exists() || metadataFile.isDirectory()) {
			return true;
		}
		
		return false;	
	}
	
	/**
	 * Checks if metadata-file is the latest version
	 * @param metadata Metadata object to check
	 * @return {@code false} if metadata-file is the latest version otherwise {@code true}
	 */
	private static boolean metadataOutdated(Metadata metadata) {
		String version = metadata.getVersion();
		
		if (version == null || !version.equals(MetadataFileHandler.LATEST_VERSION)) {
			return true;
		} else {
			return false;
		}
	}
}
