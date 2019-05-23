package main.java.zenit.searchinfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import main.java.zenit.zencodearea.ZenCodeArea;

/**
 * The Search class lets you search for a word then either
 * highlights it yellow or grey, depending on what background you have,
 * or replaces it with another word of your choosing
 * 
 * @author Fredrik Eklundh
 *
 */

public class Search {

	private ZenCodeArea zenCodeArea;
	
	private Scanner txtscan = null;
	
	private File file;
	
	private List<Integer> line = new ArrayList<>();
	private List<Integer> wordPos = new ArrayList<>();  
	private List<Integer> absolutePos = new ArrayList<>();
	
	private int numberOfTimes = 0;
	private int numberOfLines = -1;
	private int lineLenght = 0;
	private int i = 0;
	
	private String searchWord = "";
	private String replaceWord = "";
	
	private boolean isDarkMode;	
	private boolean caseSensetive = false;
	private boolean replace = false;
	
	
	/**
	 * Opens a TextInputDialog and let's you type in a word to search for 
	 * 
	 * @throws FileNotFoundException
	 */
	public Search(ZenCodeArea zenCodeArea, File file, boolean isDarkMode) {
		
		new SearchInFileController(this);
		
		TextInputDialog dialog = new TextInputDialog("search");
		dialog.setTitle("Search");
		dialog.setHeaderText("What are you looking for?");
		
		this.zenCodeArea = zenCodeArea;
		this.file = file;
		this.isDarkMode = isDarkMode;
		
		clearZen();
	}
	
//	private void searchInFile(Label occurrences) {
	public void searchInFile(String word) {
		clearZen();
		
		try {
			txtscan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

//		Optional<String> result = dialog.showAndWait();
//		if (result.isPresent()) {
//			searchWord = result.get();
			//caseSensetive needs to be change from the panel
			searchWord = word;
			if (caseSensetive == false) {
				notCaseSensetive();
			}else {
				caseSensetive();   //
			}
		

		if (numberOfTimes > 0) {
			System.out.println("Exsist " + numberOfTimes + " times");  //visa i s�kpanelen
			
			//replace has to be change from the panel
			if (replace == false) {
				
				for (int i = 0; i < numberOfTimes; i++) {
					if (isDarkMode) {
						zenCodeArea.setStyle(line.get(i), wordPos.get(i), wordPos.get(i) + searchWord.length(), List.of("search-dark-mode"));
						absolutePos.add(zenCodeArea.getAbsolutePosition(line.get(i), wordPos.get(i)));
						
					}else {
						zenCodeArea.setStyle(line.get(i), wordPos.get(i), wordPos.get(i) + searchWord.length(), List.of("search-light-mode"));				
						absolutePos.add(zenCodeArea.getAbsolutePosition(line.get(i), wordPos.get(i)));
					}
				}
			}else {
				
				for (int i = 0; i < numberOfTimes; i++) {
					absolutePos.add(zenCodeArea.getAbsolutePosition(line.get(i), wordPos.get(i)));
				}
				//replace word needs to be set from the panel
				replaceWord(searchWord, replaceWord, absolutePos);
			}

			zenCodeArea.moveTo(absolutePos.get(0));
			zenCodeArea.requestFollowCaret();
			
		}
	}

	/**
	 * Appends a Char to make the highlight disappear from 
	 * the highlighted words then removes it again
	 */
	private void clearZen() {
		zenCodeArea.appendText(" ");
		zenCodeArea.deletePreviousChar();
	}
	
	/**
	 * Replaces every occurrence of a certain word with another word 
	 * 
	 * @param wordBefore
	 * @param wordAfter
	 * @param absolutePos
	 */
	private void replaceWord(String wordBefore, String wordAfter, List<Integer> absolutePos) {
		for (int i = absolutePos.size() -1; i >= 0; i--) {
			zenCodeArea.replaceText(absolutePos.get(i), absolutePos.get(i) + wordBefore.length(), wordAfter);
		}
	}
	
	/**
	 * Jumps down/to the next occurrence of the highlighted word
	 */
	public void jumpDown() {
		if (i < absolutePos.size()) {
			i++;
		}
		
		zenCodeArea.moveTo(absolutePos.get(i));
		zenCodeArea.requestFollowCaret();
	}
	
	/**
	 * Jumps up/to the previous occurrence of the highlighted word
	 */
	public void jumpUp() {
		if(i > 0) {
			i--;
		}
		
		zenCodeArea.moveTo(absolutePos.get(i));
		zenCodeArea.requestFollowCaret();	
	}
	
	/**
	 * Making the search ignore if it's capital letters or lowercase
	 */
	private void notCaseSensetive() {
		
		while (txtscan.hasNextLine()) {
			String str = txtscan.nextLine().toLowerCase();
			numberOfLines++;
			lineLenght = 0;
			while (str.indexOf(searchWord.toLowerCase()) != -1) {
				numberOfTimes++;

				line.add(numberOfLines);

				wordPos.add(str.indexOf(searchWord.toLowerCase()) + lineLenght);

				lineLenght += str.length() - str.substring(str.indexOf(searchWord.toLowerCase()) + searchWord.length()).length();

				str = str.substring(str.indexOf(searchWord.toLowerCase()) + searchWord.length());
			}
		}
	}
	
	/**
	 * This search makes a different if it's capital letters or lowercase
	 */
	private void caseSensetive() {
		
		while (txtscan.hasNextLine()) {
			String str = txtscan.nextLine();
			numberOfLines++;
			lineLenght = 0;
			while (str.indexOf(searchWord) != -1) {
				numberOfTimes++;

				line.add(numberOfLines);

				wordPos.add(str.indexOf(searchWord) + lineLenght);

				lineLenght += str.length() - str.substring(str.indexOf(searchWord) + searchWord.length()).length();

				str = str.substring(str.indexOf(searchWord) + searchWord.length());
			}
		}
	}
}
