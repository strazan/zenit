package main.java.zenit.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyEvent;
import main.java.zenit.ui.MainController.Search;

/**
 * Static methods for interacting with a scene's accelerators.
 * @author Pontus Laos
 *
 */
public final class KeyboardShortcuts {
	/**
	 * Maps a KeyCodeCombination of the given KeyCode and Modifier to the given Runnable, 
	 * adding it to the given Scene.
	 * @param scene The Scene to add the KeyCodeCombination on.
	 * @param code The KeyCode to create a KeyCodeCombination with.
	 * @param modifier The Modifier to create a KeyCodeCombination with.
	 * @param action The Runnable to be run when the KeyCodeCombination is triggered.
	 */
	public static final void add(Scene scene, KeyCode code, Modifier modifier, Runnable action) {
		scene.getAccelerators().put(
			new KeyCodeCombination(code, modifier), 
			action
		);
	}
	
	/**
	 * Sets up keyboard shortcuts for the main scene, using methods of the MainController.
	 * @param scene The scene to be used.
	 * @param controller The controller to call methods from.
	 */
	public static final void setupMain(Scene scene, MainController controller, Search search) {
		add(scene, KeyCode.S, KeyCombination.SHORTCUT_DOWN, () -> controller.saveFile(null));
		add(scene, KeyCode.O, KeyCombination.SHORTCUT_DOWN, () -> controller.openFile((Event) null));
		add(scene, KeyCode.N, KeyCombination.SHORTCUT_DOWN, controller::addTab);
		add(scene, KeyCode.W, KeyCombination.SHORTCUT_DOWN, () -> controller.closeTab(null)); 		
		add(scene, KeyCode.R, KeyCombination.SHORTCUT_DOWN, controller::compileAndRun);
		add(scene, KeyCode.F, KeyCombination.SHORTCUT_DOWN, search::searchInFile);
		add(scene, KeyCode.B, KeyCombination.SHORTCUT_DOWN, search::jumpUp);
		add(scene, KeyCode.M, KeyCombination.SHORTCUT_DOWN, search::jumpDown);
		add(scene, KeyCode.SPACE, KeyCombination.SHORTCUT_DOWN, controller::shortcutsTrigger);
		
		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    public void handle(KeyEvent ke) {
		        if (ke.getCode() == KeyCode.ENTER) {
		        	controller.commentsShortcutsTrigger();
		            ke.consume(); // <-- stops passing the event to next node
		        }
		    }
		});
	}
}
