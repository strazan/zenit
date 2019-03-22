package zenit.filesystem.helpers;

/**
 * Snippets of code to insert into classes
 * @author Alexander Libot
 *
 */
public class CodeSnippets {
	
	/**
	 * Template code for new class
	 * @param classname The name of the class
	 * @param packagename The name of the package
	 * @return Created code
	 */
	public static String newClass (String classname, String packagename) {
		
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
		
		String codesnippet =
				"package " + packagename + "\n" + 
				"\n" +
				"public class " + classname + " {\n" +
				"\n" + 
				"}";
				
		return codesnippet;
	}
}
