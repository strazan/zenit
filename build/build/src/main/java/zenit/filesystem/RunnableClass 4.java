package main.java.zenit.filesystem;

import java.io.File;

/**
 * Abstract data type containing data about a runnable class.
 * Mainly used by {main.java.zenit.filesystem.Metadata Metadata} class.
 * @author Alexander Libot
 *
 */
public class RunnableClass {
	
	private String path;
	private String paArguments;
	private String vmArguments;
	
	/**
	 * Creates a new runnable class object
	 * @param path Path to the class, should be within src-folder, e.g. {@code package/Class.java}
	 * or set source path directory, see {@link main.java.zenit.filesystem.Metadata#setSourcepath}.
	 * @param paArguments Program arguments for class
	 * @param vmArguments VM Arguments for class
	 */
	public RunnableClass(String path, String paArguments, String vmArguments) {
		this.path = path;
		this.paArguments = paArguments;
		this.vmArguments = vmArguments;
	}
	
	public RunnableClass(String path) {
		this(path, "", "");
	}
	
	public RunnableClass(File file) {
		this(file.getPath());
	}
	
	/**
	 * Returns runnable class path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets new runnable class path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Returns program arguments for class
	 */
	public String getPaArguments() {
		return paArguments;
	}

	/**
	 * Sets new program arguments for class
	 */
	public void setPaArguments(String paArguments) {
		this.paArguments = paArguments;
	}

	/**
	 * Returns VM arguments for class
	 */
	public String getVmArguments() {
		return vmArguments;
	}

	/**
	 * Sets new VM arguments for class
	 */
	public void setVmArguments(String vmArguments) {
		this.vmArguments = vmArguments;
	}

	/**
	 * Converts attributes to string
	 */
	public String toString() {
		String nl = "\n";
		String string = path + nl + paArguments + nl + vmArguments;
		return string;
	}
}
