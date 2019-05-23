package main.java.zenit.ui.projectinfo;

import java.io.File;

import javafx.scene.control.TreeItem;

public class RunnableClassTreeItem<t> extends TreeItem<t> {
	
	private File file;
	private boolean runnable;
	
	public RunnableClassTreeItem(t name, File file, boolean runnable) {
		super(name);
		this.file = file;
		this.runnable = runnable;
	}

	public File getFile() {
		return file;
	}

	public boolean isRunnable() {
		return runnable;
	}
	
	

}
