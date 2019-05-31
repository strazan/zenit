package main.java.zenit.ui.tree;

import java.io.File;

import javafx.scene.control.TreeItem;
import main.java.zenit.filesystem.ProjectFile;

/**
 * Handles the nodes of a filetree
 * @author Alexander Libot
 *
 */
public class FileTree {
	
	/**
	 * Initiates all the nodes from a parent folder (excluding parent node itself. 
	 * Recursively adds all children and with corresponding file.
	 * Currently excludes bin-folders and hidden files (with '.'-prefix).
	 * For initiation including parent-node, use {@link #createParentNode(FileTreeItem, File) createParentNode} method instead.
	 * @param parent The parent node to create children nodes to
	 * @param file The file corresponding to the node, might be unnecessary due to changes
	 * in FileTreeItem-structure.
	 */
	public static void createNodes(FileTreeItem<String> parent, File file) {
		int type = 0;
		if (file.listFiles() == null) {
			return;
		}
		
		File[] files = file.listFiles();
		String itemName;
		for (int index = 0; index < files.length; index++) {
			itemName = files[index].getName();
			if (!itemName.startsWith(".") && !itemName.equals("bin") && !itemName.endsWith(".class")) { //Doesn't include hidden files
				type = calculateType(parent, files[index]);
				FileTreeItem<String> item = new FileTreeItem<String> (files[index], itemName, type);
				parent.getChildren().add(item);
				
				if (files[index].isDirectory()) {
					createNodes(item, files[index]);
				}
			}
		}
	}
	
	/**
	 * Initiates a parent node and all children with corresponding files using recursion.
	 * For initiation without parent node, use {@link #createNodes(FileTreeItem, File) createNodes}
	 * method instead.
	 * @param parent The parent node to initiate and create children nodes to
	 * @param file The file corresponding to the node, might be unnecessary due to changes
	 * in FileTreeItem-structure
	 */
	public static void createParentNode(FileTreeItem<String> parent, File file) {
		if (file == null) {
			return;
		}
		
		int type = calculateType(parent, file);
		
		FileTreeItem<String> item = new FileTreeItem<String> (file, file.getName(), type);
		parent.getChildren().add(item);
		
		if (file.isDirectory()) {
			createNodes(item, file);
		}
	}
	
	/**
	 * Updates the corresponding file to all the children of a node.
	 * @param parent The parent node to update children nodes
	 * @param file The new corresponding file
	 */
	public static void changeFileForNodes(FileTreeItem<String> parent, File file) {
		if (file.listFiles() == null) {
			return;
		}
		File[] files = file.listFiles();
		
		for (TreeItem<String> item : parent.getChildren()) {
			FileTreeItem<String> ftItem = (FileTreeItem<String>) item;
			for (File entry : files) {
				if (entry.getName().equals(ftItem.getValue())) {
					ftItem.setFile(entry);
				}
			}
			if (ftItem.getFile().isDirectory()) {
				changeFileForNodes(ftItem, ftItem.getFile());
			}
		}
	}
	
	/**
	 * Calculates the type of the tree node; workspace, package, class or source-folder.
	 * @param parent Nodes parent node
	 * @param itemName Name of the node
	 * @return An integer representing the node type.
	 */
	private static int calculateType(FileTreeItem<String> parent, File file) {
		int type = 0;
		
		String itemName = file.getName();
		ProjectFile projectFile = new ProjectFile(file);
		
		//Project
		if (projectFile.getMetadata() != null) {
			type = FileTreeItem.PROJECT;
		}
		//Package
		else if (parent.getValue().equals("src") && file.isDirectory()) {
			type = FileTreeItem.PACKAGE;
		}
		else if (itemName.equals("src")) {
			type = FileTreeItem.SRC;
		}
		//Folder
		else if (projectFile.getMetadata() == null && file.isDirectory()) {
			type = FileTreeItem.FOLDER;
		}

		//Java-file
		else if (itemName.endsWith(".java")) {
			type = FileTreeItem.CLASS;
		} 
		//Text-file
		else if (itemName.endsWith(".txt")) {
			type = FileTreeItem.FILE;
		}
		else if (file.isFile() && itemName.indexOf('.') == -1) {
			type = FileTreeItem.FILE;
		} else {
			type = FileTreeItem.INCOMPATIBLE;
		}
		
		return type;
	}
	
	
}
