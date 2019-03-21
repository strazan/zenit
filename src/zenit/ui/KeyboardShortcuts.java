package zenit.ui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;

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
}
