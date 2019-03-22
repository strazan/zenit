package zenit.filesystem;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Only used for testing, not to be implemented
 * @author Alexander Libot
 *
 */
public class FileTree {
	
	public static void createNodes(DefaultMutableTreeNode root, File workspace) {
		DefaultMutableTreeNode[] projects = null;
		
		if (workspace.listFiles() == null) {
			return;
		}
		File[] projectFiles = workspace.listFiles();

		projects = new DefaultMutableTreeNode[projectFiles.length];
		
		String projectName;
		for (int index = 0; index < projects.length; index++) {
			projectName = projectFiles[index].getName();
			projects[index] = new DefaultMutableTreeNode(projectName);
			root.add(projects[index]);
			
			if (projectFiles[index].isDirectory()) {
				createNodes(projects[index], projectFiles[index]);
			}
		}
	}
}
