package zenit.filesystem;

import java.io.File;

import zenit.filesystem.controller.FileController;

/**
 * Used for testing the file system
 * @author Alexander Libot
 *
 */
public class TestFileSystem {
	
	public static void main(String[] args) {
		FileGUI gui = new FileGUI();
		File workspace = new File("/Users/Alexander/Desktop/_testfolder");
		
		new FileController(gui, workspace);
	}

}
