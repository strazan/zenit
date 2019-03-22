package javacodeCompiler;

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

	/**
	 * This method will both compile and the run a [Foo.java] file, placed in a package.
	 * 
	 * @param absolutePath to the [Foo.java] file, including full file name, where the main 
	 * method is. 
	 * @param targetDirectoryIn is directory where the compiled [Foo.class] file will be placed.
	 * @param dependenciesPath are the paths to dependent libraries.
	 */
	public void compileAndRunJavaFileInPackage(
		String absolutePath,
		String targetDirectorPathIn,
		String dependenciesPath
	) {
		String fileName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1).trim();
		String targetDirectoryPath = formatDirectoryPath(targetDirectorPathIn);
		CommandLine clCompileJavaFile = CommandLine.parse(
			"javac " + absolutePath + " -d " + targetDirectoryPath
		);
		CommandLine clRunJavaByteCodeFile = CommandLine.parse(
			"java -classpath " + dependenciesPath + " " + targetDirectoryPath + fileName
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
	 * This method will both compile and the run a [Foo.java] file that is not placed in a package.
	 * 
	 * @param absolutePath to the [Foo.java] file, including full file name, where the main 
	 * method is. 
	 * @param targetDirectoryIn is directory where the compiled [Foo.class] file will be placed.
	 * @param dependenciesPath are the paths to dependent libraries.
	 */
	public void compileAndRunJavaFileWithoutPackage(
		String absolutePath, 
		String targetDirectoryPathIn, 
		String dependenciesPath
	) {
		String fileName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1).trim();
		String targetDirectoryPath = formatDirectoryPath(targetDirectoryPathIn);
		fileName = fileName.substring(0,(fileName.lastIndexOf('.')));
		
		CommandLine clCompileJavaFile = CommandLine.parse(
			"javac " + absolutePath + " -d " + targetDirectoryPath
		);
		CommandLine clRunJavaByteCodeFile = CommandLine.parse(
			"java -classpath " + dependenciesPath + " -cp " + targetDirectoryPath  + " " + fileName
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
	 * This method will compile a [Foo.java] file into a target directory.
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
	 * This method will take a (String) directory-path and correct it if it misses the final '/'. 
	 * 
	 * @param directoryIn
	 * @return finalDirectory
	 */
	public String formatDirectoryPath(String targetDirectoryPathIn) {
		String finalDirectoryPath = targetDirectoryPathIn;
		
		if ((finalDirectoryPath.charAt(finalDirectoryPath.length() - 1)) != '/') {
			finalDirectoryPath += "/";
		}
		return finalDirectoryPath;
	}
}