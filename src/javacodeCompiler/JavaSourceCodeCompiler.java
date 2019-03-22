package javacodeCompiler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

/**
 * 
 * JavaSourceCodeCompiler compiles and runs / compiles a [Foo.java] file via terminal using the 
 * javac compiler. Java Virtual machine needs to be installed on the machine for the compiler
 * to work.  
 * 
 * @author Sigge Labor
 *
 */
public class JavaSourceCodeCompiler {
	
	private DefaultExecutor executor = new DefaultExecutor();
	private char slash;

	
	public JavaSourceCodeCompiler() {
		slash = System.getProperty("os.name").startsWith("Windows") ? '\\' : '/';
	}
	
	/**
	 * Compiles and runs a [Foo.java] file, placed in a package.
	 * 
	 * @param absolutePath to the [Foo.java] file, including full file name, where the main 
	 * method is. 
	 * @param targetDirectoryIn is directory where the compiled [Foo.class] file will be placed.
	 * @param dependenciesPath are the paths to dependent libraries.
	 */
		
	public void compileAndRunJavaFileInPackage(File file, String targetDirectoryPathIn) {
		String absolutePath = file.getAbsolutePath();
		
		String fileName = absolutePath.substring(absolutePath.lastIndexOf(slash) + 1).trim();
		String targetDirectoryPath = formatDirectoryPath(targetDirectoryPathIn);
		CommandLine clCompileJavaFile = CommandLine.parse(
			"javac \"" + absolutePath + "\" -d \"" + targetDirectoryPath + "\""
		);
		CommandLine clRunJavaByteCodeFile = CommandLine.parse(
			"java -classpath \"" + System.getProperty("java.home") + "\" \"" + targetDirectoryPath + fileName + "\""
		);
		
		try {
			executor.execute(clCompileJavaFile);
			executor.execute(clRunJavaByteCodeFile);
		} catch (ExecuteException e) {
			System.err.println("Compile and run file failed. The file was: " + absolutePath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Something went wrong.");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Compiles and runs [Foo.java] file that is not placed in a package.
	 * 
	 * @param absolutePath to the [Foo.java] file, including full file name, where the main 
	 * method is. 
	 * @param targetDirectoryIn is directory where the compiled [Foo.class] file will be placed.
	 * @param dependenciesPath are the paths to dependent libraries.
	 */
	public void compileAndRunJavaFileWithoutPackage(File file, String targetDirectoryPathIn ) {
		String absolutePath = file.getAbsolutePath();
		
		String fileName = absolutePath.substring(absolutePath.lastIndexOf(slash) + 1).trim();


		fileName = fileName.substring(0,(fileName.lastIndexOf('.')));

		CommandLine clCompileJavaFile = CommandLine.parse(
			"javac \"" + absolutePath + "\" -d \"" + targetDirectoryPathIn + "\""
		);
		CommandLine clRunJavaByteCodeFile = CommandLine.parse(
			"java -classpath \"" + System.getProperty("java.home") + "\" -cp \"" + targetDirectoryPathIn  + "\" \"" + fileName + "\""
		);
		
		try {
			executor.execute(clCompileJavaFile);
			executor.execute(clRunJavaByteCodeFile);
		} catch (ExecuteException e) {
			System.err.println("Compile and run file failed. The file was: " + absolutePath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Something went wrong.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Compiles a [Foo.java] file into a target directory.
	 * 
	 * @param absolutePath to the [Foo.java] file, including full file name, where the main 
	 * method is. 
	 * @param targetDirectoryIn is directory where the compiled [Foo.class] file will be placed.
	 * @param dependenciesPath are the paths to dependent libraries.
	 */
	public void compileJavaFile(
		String absolutePath, 
		String targetDirectoryPathIn
	) {
		String targetDirectoryPath = formatDirectoryPath(targetDirectoryPathIn);
		CommandLine clCompileJavaFile = CommandLine.parse(
				"javac " + absolutePath + " -d " + targetDirectoryPath
		);

		try {
			executor.execute(clCompileJavaFile);
		} catch (ExecuteException e) {
			System.err.println("Compile and run file failed. The file was: " + absolutePath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Something went wrong.");
			e.printStackTrace();
		}
	}
	
	/**
	 * NOT COMPLETE
	 */
	public void terminate() {
		
	}
	
	/**
	 * Takes a (String) directory-path and correct it if it misses the final '/'. 
	 * 
	 * @param directoryIn
	 * @return finalDirectory
	 */
	public String formatDirectoryPath(String targetDirectoryPathIn) {
		String finalDirectoryPath = targetDirectoryPathIn;
	
		if ((finalDirectoryPath.charAt(finalDirectoryPath.length() - 1)) != slash) {
			finalDirectoryPath += slash;
		}
		
		return finalDirectoryPath;
	}
}