package zenit.filesystem;

/**
 * Super class for file handling concerning files. Should be extended.
 * Text encoding is by default "UTF-8" but can be changed with {@link #setTextEncoding(String)
 * setTextEncoding}.
 * Use constant {@link #UTF UTF} for UTF-8.
 * @author Alexander Libot
 *
 */
public class FileHandler {
	
	public final static String UTF = "UTF-8";
	
	protected static String textEncoding = UTF; //Text-encoding
	
	/**
	 * Changes text encoding. Use {@link JavaFileHandler ClassHandler} constants.
	 * @param encoding The new text encoding.
	 */
	public static void setTextEncoding(String encoding) {
		textEncoding = encoding;
	}

}
