package zenit.ui.tree;

import java.io.File;
import javafx.scene.control.TreeItem;

/**
 * Extension of the TreeItem class with the ability to save a corresponding File-object
 * in the instance.
 * @author Alexander Libot
 *
 * @param <T>
 */
public class FileTreeItem<T> extends TreeItem<T> {
	private File file;
	
	/**
	 * @param file Corresponding file
	 * @param name
	 */
	public FileTreeItem(File file, T name) {
		super(name);
		this.file = file;
	}
	
	/**
	 * Set the corresponding file
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * Get the corresponding file
	 */
	public File getFile() {
		return file;
	}
}