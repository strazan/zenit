package main.java.zenit.util;

public class StringUtilities {
	/**
	 * Counts the amount of spaces in the beginning of a string.
	 * @param text The string to check for leading spaces.
	 * @return The amount of leading spaces in a string.
	 * @author Pontus Laos
	 */
	public static int countLeadingSpaces(String text) {
		if (text == null || text.length() == 0 || text.charAt(0) != ' ') {
			return 0;
		}
		return 1 + countLeadingSpaces(text.substring(1));
	}
	
	/**
	 * Counts the amount of occurrences of a character in a string.
	 * @param haystack The string to search through.
	 * @param needle The character to search for.
	 * @return The amount of occurences of the character in the string.
	 * @author Pontus Laos
	 */
	public static int count(String haystack, char needle) {
		if (haystack == null || haystack.length() == 0) {
			return 0;
		}
		
		if (haystack.charAt(0) == needle) {
			return 1 + count(haystack.substring(1), needle);
		}
		return count(haystack.substring(1), needle);
	}
}
