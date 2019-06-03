package main.java.zenit.ui.tree;

import javafx.event.EventHandler;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.java.zenit.ui.MainController;

/**
 * Handles events for a TreeView instance. Implements EventHandler.
 * @author Alexander Libot
 *
 */
public class TreeClickListener implements EventHandler<MouseEvent> {
	
	private MainController controller;
	private TreeView<String> treeView;
	
	/**
	 * Creates a new Listener for a specific TreeView instance that calls methods in
	 * a specific MainController instance.
	 * @param controller The MainController instance where methods will be called from
	 * @param treeView The TreeView instance where data will be collected from
	 */
	public TreeClickListener(MainController controller, TreeView<String> treeView) {
		this.controller = controller;
		this.treeView = treeView;
	}

	/**
	 * Event handler method for TreeClickListener. Collects the selected FileTreeItem
	 * from TreeView instance and calls MainController instance openFile method, when fired
	 */
	@Override
	public void handle(MouseEvent mouseEvent) {
		FileTreeItem<String> selectedItem = (FileTreeItem<String>) 
				treeView.getSelectionModel().getSelectedItem();

		if (selectedItem != null && !selectedItem.getFile().isDirectory() && 
				mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
			controller.openFile(selectedItem.getFile());
		}
		if (selectedItem != null) {
			controller.updateStatusLeft(selectedItem.getFile().getPath());
		}
	}

}
