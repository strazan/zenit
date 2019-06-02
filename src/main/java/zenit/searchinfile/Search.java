package main.java.zenit.searchinfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.fxmisc.richtext.model.StyleSpan;
import org.reactfx.value.Var;

import javafx.application.Platform;
import javafx.scene.control.Label;
import main.java.zenit.ui.MainController;
import main.java.zenit.util.Tuple;
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
	
	private List<Integer> line;
	private List<Integer> wordPos;
	private List<Tuple<Integer, Integer>> absolutePos;

	private int numberOfTimes = 0;
	private int numberOfLines = -1;
	private int lineLenght = 0;
	private int i = 0;
	
	private String searchWord = "";
	
	private boolean isDarkMode;	
	private boolean caseSensetive = false; 
	
	/**
	 * Opens a TextInputDialog and let's you type in a word to search for 
	 * 
	 * @throws FileNotFoundException
	 */
	public Search(ZenCodeArea zenCodeArea, File file, boolean isDarkMode, MainController mainController) {
		
		new SearchInFileController(this, mainController);
		
		this.zenCodeArea = zenCodeArea;
		this.file = file;
		this.isDarkMode = isDarkMode;
		

	}
	
	public int searchInFile(String word) {
		line = new ArrayList<>();
		wordPos = new ArrayList<>();
		absolutePos = new ArrayList<>();
		
		numberOfTimes = 0;
		numberOfLines = -1;
		lineLenght = 0;
		i = 0;
		
		if(word.length() < 1) {
			return 0;
		}
		
		try {
			txtscan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		
		//caseSensetive needs to be change from the panel
		searchWord = word;
		if (caseSensetive == false) {
			notCaseSensetive();
		} else {
			caseSensetive();   
		}

		if (numberOfTimes > 0) {
			for (int i = 0; i < numberOfTimes; i++) {
				int start = zenCodeArea.getAbsolutePosition(line.get(i), wordPos.get(i));
				int end = start + searchWord.length();
				
				absolutePos.add(new Tuple<>(start, end));
				zenCodeArea.setStyle(
					line.get(i), 
					wordPos.get(i), 
					wordPos.get(i) + searchWord.length(), 
					List.of(isDarkMode ? "search-dark-mode" : "search-light-mode")
				);
			}
			
			zenCodeArea.moveTo(absolutePos.get(0).fst());
			zenCodeArea.requestFollowCaret();
		}
		return numberOfTimes;
	}

	/**
	 * Clears the "found match"-styling.
	 * @author Fredrik Eklundh, Pontus Laos
	 */
	public void clearZen() {
		if (absolutePos != null) {
			for (int i = 0; i < absolutePos.size(); i++) {
				zenCodeArea.clearStyle(absolutePos.get(i).fst(), absolutePos.get(i).snd());
				zenCodeArea.update();
			}
		}
	}
	
	/**
	 * When you close the search panel the highlight disappears
	 */
	public void cleanZen() {
		int carPos = zenCodeArea.getCaretPosition();
		zenCodeArea.appendText(" ");
		zenCodeArea.deletePreviousChar();
		zenCodeArea.moveTo(carPos);
	}
	
	/**
	 * Replaces every occurrence of a certain word with another word.
	 * 
	 * @param wordBefore
	 * @param wordAfter
	 * @param absolutePos
	 */	
	public void replaceAll(String wordAfter) {
		for (int i = absolutePos.size() -1; i >= 0; i--) {
			zenCodeArea.replaceText(absolutePos.get(i).fst(), absolutePos.get(i).snd(), wordAfter);
		}
	}
	
	/**
	 * Replaces a single word with another word.
	 * 
	 * @param wordBefore
	 * @param wordAfter
	 * @param absolutePos
	 */
	public void replaceOne(String wordAfter) {
		zenCodeArea.replaceText(absolutePos.get(i).fst(), absolutePos.get(i).snd(), wordAfter);
	}
	
	/**
	 * Jumps down/to the next occurrence of the highlighted word
	 */
	public int jumpDown() {
		if (i < absolutePos.size() - 1) {
			i++;
		}else {
			i = 0;
			
		}
		
		zenCodeArea.moveTo(absolutePos.get(i).fst());
		zenCodeArea.requestFollowCaret();
		return i;
	}
	
	/**
	 * Jumps up/to the previous occurrence of the highlighted word
	 */
	public int jumpUp() {
		if(i > 0) {
			i--;
		}else {
			i = absolutePos.size() - 1;
		}
		
		zenCodeArea.moveTo(absolutePos.get(i).fst());
		zenCodeArea.requestFollowCaret();	
		return i;
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
