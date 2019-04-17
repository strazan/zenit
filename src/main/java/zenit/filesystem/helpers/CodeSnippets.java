package main.java.zenit.filesystem.helpers;

import main.java.zenit.exceptions.TypeCodeException;

/**
 * Snippets of code to insert into classes. Accessed via newSnippet method and constants.
 * @author Alexander Libot
 *
 */
public class CodeSnippets {
	
	public static final int EMPTY = 99;
	public static final int CLASS = 100;
	public static final int INTERFACE = 101;
	
	public static String newSnippet(int typeCode, String classname, String packagename) throws TypeCodeException {
		String snippet;
		switch (typeCode) {
		case (EMPTY): snippet = ""; break;
		case (CLASS): snippet = newClass(classname, packagename); break;
		case (INTERFACE): snippet = newInterface(classname, packagename); break;
		default: throw new TypeCodeException();
		}
		return snippet;
	}
	
	/**
	 * Template code for new class
	 * @param classname The name of the class
	 * @param packagename The name of the package
	 * @return Created code
	 */
	private static String newClass (String classname, String packagename) {
		
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
		
		String codesnippet =
				"package " + packagename + ";\n" + 
				"\n" +
				"public class " + classname + " {\n" +
				"\n" + 
				"}";
				
		return codesnippet;
	}
	
	/**
	 * Template code for new interface
	 * @param classname The name of the interface
	 * @param packagename The name of the package
	 * @return Created code
	 */
	private static String newInterface (String classname, String packagename) {
		
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
		
		String codesnippet =
				"package " + packagename + "\n" + 
				"\n" +
				"public interface " + classname + " {\n" +
				"\n" + 
				"}";
				
		return codesnippet;
	}
}
