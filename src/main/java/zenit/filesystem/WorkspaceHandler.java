package main.java.zenit.filesystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for handling the workspace.
 * @author Alexander Libot, Pontus Laos
 *
 */
public class WorkspaceHandler {
	/**
	 * Checks if a workspace.dat file exists in the correct folder, and creates it and its
	 * parent folders if not.
	 * @author Pontus Laos
	 */
	private static void createWorkspaceFile() {
		File workspaceFile = new File("res/workspace/workspace.dat");
		
		if (!workspaceFile.exists()) {
			workspaceFile.getParentFile().mkdirs();

			try {
				workspaceFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Create new or overwrites the res/workspace/workspace.dat file.
	 * @param file The File-object to the workspace folder
	 * @return True if created successfully, otherwise false
	 */
	public static boolean createWorkspace(File file) {
		boolean success = false;
				
		createWorkspaceFile();
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream("res/workspace/workspace.dat")))) {
			oos.writeObject(file);
			oos.flush();
			success = true;
		} catch (IOException ex) {
			System.err.println("WorkspaceHandler.createWorkspace: IOException: " + ex.getMessage());
		}
		return success;
	}
	
	/**
	 * Reads the res/workspace/workspace.dat file.
	 * @return The File-object to the workspace folder if existing, otherwise null.
	 */
	public static File readWorkspace() throws IOException {
		File workspace = null;
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream("res/workspace/workspace.dat")))) {
			workspace = (File) ois.readObject();
		} catch (ClassNotFoundException ex) {
			System.err.println("WorkspaceHandler.readWorkspace: ClassNotFoundException " + ex.getMessage());
		}
		return workspace;
	}

}
