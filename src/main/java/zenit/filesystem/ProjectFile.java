package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * Subclass of {@link File} with added methods for metadata, src, bin and lib.
 * @author Alexander Libot
 *
 */
public class ProjectFile extends File {
	
	private static final long serialVersionUID = -9201755155887621850L;
	private File metadata;
	private File src;
	private File bin;
	private File lib;

	/**
	 * Creates a new {@link ProjectFile} from path
	 * @param pathname
	 */
	public ProjectFile(String pathname) {
		super(pathname);
	}
	
	/**
	 * Creates a new {@link ProjectFile} from {@link File} object
	 * @param file
	 */
	public ProjectFile(File file) {
		super(file.getPath());
	}
	
	/**
	 * If lib file doesn't exist, creates a new one.
	 * @return The lib-file.
	 */
	public File addLib() {
		if (lib == null) {
			String libPath = getPath() + File.separator + "lib";
			lib = new File(libPath);
			lib.mkdir();
		}
		
		return lib;
	}
	
	/**
	 * Returns the lib-file
	 */
	public File getLib() {
		return lib;
	}
	
	/**
	 * Sets a new lib-file
	 */
	public void setLib(File lib) {
		this.lib = lib;
	}
	
	/**
	 * If src-file doesn't exist, creates a new one.
	 * @return The src-file
	 */
	public File addSrc() {
		if (src == null) {
			String srcPath = getPath() + File.separator + "src";
			src = new File(srcPath);
			src.mkdir();
		}
		
		return src;
	}
	
	public File getSrc() {
		if (src == null) {
			File[] files = listFiles();
			
			for (File file : files) {
				if (file.getName().equals("src")) {
					src = file;
					break;
				}
			}
		}
		
		return src;
	}
	
	/**
	 * If bin-file doesn't exist, creates a new one.
	 * @return The bin-file
	 */
	public File addBin() {
		if (bin == null) {
			String binPath = getPath() + File.separator + "bin";
			bin = new File(binPath);
			bin.mkdir();
		}
		
		return bin;
	}
	
	/**
	 * Returns the bin file. If not initiated, searches the project folder for one.
	 * If no bin-file is found, returns null
	 * @return
	 */
	public File getBin() {
		if (bin == null) {
			File[] files = listFiles();
			
			for (File file : files) {
				if (file.getName().equals("bin")) {
					bin = file;
					break;
				}
			}
		}
		
		return bin;
	}
	
	/**
	 * If metadata-file doesn't exist, creates a new one
	 * @return
	 */
	public File addMetadata() {
		metadata = getMetadata();
		if (metadata == null) {
			try {
				metadata = MetadataFileHandler.createMetadataFile(this);
			} catch (IOException e) {
				return null;
			}
		}
		return metadata;
	}
	
	/**
	 * Returns the metadata-file. If not initiated, searches the project folder for one.
	 * If no metadata-file is found, returns null.
	 */
	public File getMetadata() {
		if (metadata == null && isDirectory()) {
			File[] files = listFiles();
		
			for (File file : files) {
				if (file.getName().equals(".metadata")) {
					metadata = file;
					break;
				}
			}
		}
		
		return metadata;
	}
	
	/**
	 * Sets a new metadata-file
	 */
	public void setMetadata(File metadata) {
		this.metadata = metadata;
	}
}
