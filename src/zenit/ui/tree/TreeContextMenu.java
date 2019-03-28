package zenit.ui.tree;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import zenit.filesystem.helpers.CodeSnippets;
import zenit.ui.MainController;

/**
 * Class that extends ContextMenu with static menu items with dynamic text.
 * Also contains event handler.
 * @author Alexander Libot
 *
 */
public class TreeContextMenu extends ContextMenu implements EventHandler<ActionEvent>{
	
	private MainController controller;
	private TreeView<String> treeView;
	
	private Menu createItem = new Menu("New...");
	private MenuItem createClass = new MenuItem("New class");
	private MenuItem createInterface = new MenuItem("New interface");
	private MenuItem renameItem = new MenuItem("Rename");
	private MenuItem deleteItem = new MenuItem("Delete");
	
	/**
	 * Creates a new TreeContextMenu that can manipulate a specific TreeView instance
	 * and call methods in a specific MainController.
	 * @param controller The MainController instance where methods will be called
	 * @param treeView The TreeView instance which will be manipulated
	 */
	public TreeContextMenu(MainController controller, TreeView<String> treeView) {
		super();
		this.controller = controller;
		this.treeView = treeView;
		initContextMenu();
	}
	
	/**
	 * Updates the menu items with dynamic text.
	 * @param selectedNode The name of the node in the tree to be inserted dynamically
	 */
	private void setContext(String selectedNode) {
		String renameItemTitle = String.format("Rename \"%s\"", selectedNode);
		String deleteItemTitle = String.format("Delete \"%s\"", selectedNode);
		renameItem.setText(renameItemTitle);
		deleteItem.setText(deleteItemTitle);
	}
	
	/**
	 * Overrides the show method in ContextMenu. Dynamically updates the menu items before
	 * showing the context menu.
	 */
	@Override
	public void show(Node node, double x, double y) {
		setContext(treeView.getSelectionModel().getSelectedItem().getValue());
		super.show(node, x, y);
	}
	
	/**
	 * Initializes the context menu by adding all menus and menu items and setting
	 * action listeners.
	 */
	private void initContextMenu() {
		createItem.getItems().add(createClass);
		createItem.getItems().add(createInterface);
		getItems().addAll(createItem, renameItem, deleteItem);
		createClass.setOnAction(this);
		createInterface.setOnAction(this);
		renameItem.setOnAction(this);
		deleteItem.setOnAction(this);
	}
	
	/**
	 * To create a new file, calls the createFile method in MainController.
	 * @param typeCode The type of item to be created. Use constants from the CodeSnippets-class
	 */
	private void newFile(int typeCode) {
		FileTreeItem<String> parent = (FileTreeItem<String>) 
				treeView.getSelectionModel().getSelectedItem();
		File newFile = controller.createFile(parent.getFile(), typeCode);
		if (newFile != null) {
			FileTreeItem<String> newItem = new FileTreeItem<String>(newFile, newFile.getName());
			parent.getChildren().add(newItem);
		}
	}

	/**
	 * Event handler for TreeContextMenu. Calls different methods in MainController
	 * depending on input.
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(createClass)) {
			newFile(CodeSnippets.CLASS);
		} else if (actionEvent.getSource().equals(createInterface)) {
			newFile(CodeSnippets.INTERFACE);
		} else if (actionEvent.getSource().equals(renameItem)) {
			FileTreeItem<String> item = (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
			File file = item.getFile();
			File newFile = controller.renameFile(file);
			if (newFile != null) {
				item.setFile(newFile);
				item.setValue(newFile.getName());
				FileTree.changeFileForNodes(item, item.getFile());
			}
		} else if (actionEvent.getSource().equals(deleteItem)) {
			FileTreeItem<String> item = (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
			File file = item.getFile();
			controller.deleteFile(file);
			item.getParent().getChildren().remove(item);
		}
	}
}
