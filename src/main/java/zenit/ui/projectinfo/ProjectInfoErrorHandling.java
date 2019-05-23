package main.java.zenit.ui.projectinfo;

import main.java.zenit.ui.DialogBoxes;

/**
 * Handles different kinds of errors by displaying dialog boxes and if needed returns the
 * answer code.
 * @author Alexander Libot
 *
 */
public class ProjectInfoErrorHandling {
	
	/**
	 * Opens a two choice dialog if metadata-file is missing
	 * @return 1 if "Yes, generate is selected", 2 if "No, close window if selected", 
	 * otherwise 0.
	 */
	public static int metadataMissing() {
		return DialogBoxes.twoChoiceDialog("Metadata missing", "Metadata missing from "
				+ "project", "It seems this is the first time you are using this project in "
				+ "Zenit. Do you want to generate a new metadata file about this project?",
				"Yes, generate", "No, close window");
	}
	
	/**
	 * Opens a two choice dialog if metadata-file is outdated
	 * @return 1 if "Yes, update" is selected, 2 if "No, close window" is selected, 
	 * otherwise 0.
	 */
	public static int metadataOutdated() {
		return DialogBoxes.twoChoiceDialog("Metadata outdated", "Metadata must be updated",
				"It seems that you must update the metadata file. Do you want to update the "
				+ "metadata file about this project?", "Yes, update", "No, close window");
	}
	
	/**
	 * Opens an error dialog if internal library add failed
	 */
	public static void addInternalLibraryFail() {
		DialogBoxes.errorDialog("Import failed", "Couldn't import internal libraries",
				"Failed to import internal libraries");
	}
	
	/**
	 * Opens an error dialog if internal library remove failed
	 */
	public static void removeInternalLibraryFail() {
		DialogBoxes.errorDialog("Removal failed", "Couldn't remove internal libraries",
				"Failed to remove internal libraries");
	}
	
	/**
	 * Opens an error dialog if external library add failed
	 */
	public static void addExternalLibraryFail() {
		DialogBoxes.errorDialog("Add failed", "Couldn't add external libraries",
				"Failed to add external libraries");
	}
	
	/**
	 * Opens an error dialog if external library remove failed
	 */
	public static void removeExternalLibraryFail() {
		DialogBoxes.errorDialog("Removal failed", "Couldn't remove external libraries",
				"Failed to remove external libraries");
	}
}
