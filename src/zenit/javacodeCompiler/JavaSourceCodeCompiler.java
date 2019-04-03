package zenit.javacodeCompiler;

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
	 * starts a new thread where a .java file (in a defined package) will be compiled into chosen
	 * directory and executed
	 * @param file to be compiled and executed
	 * @param targetDirectoryPath
	 */
//	public void compileAndRunJavaFileInPackage(File file, String targetDirectoryPathIn) {
//		new CompileAndRunJavaFileInPackage(file, targetDirectoryPathIn);
//	}
	public void compileAndRunJavaFileInPackage(File file, File metadata) {
		new CompileAndRunJavaFileInPackage(file, metadata).start();
	}

	/** 
	 * starts a new thread where a .java file (without a defined package) will be compiled into
	 * chosen directory and executed
	 * @param file to be compiled and executed
	 * @param targetDirectoryPath
	 */
	public void compileAndRunJavaFileWithoutPackage(File file, String targetDirectoryPathIn) {
		new CompileAndRunJavaFileWithoutPackage(file, targetDirectoryPathIn).start();
	}
	
	/** 
	 * starts a new thread where the file will be compiled into chosen directory.
	 * @param file to be compiled and executed
	 * @param targetDirectoryPath
	 */
	public void compileJavaFile(File file, String targetDirectoryPathIn) {
		new CompileJavaFile(file, targetDirectoryPathIn).start();
	}

	/**
	 * Compiles and runs a [Foo.java] file, placed in a package.
	 * 
	 * @param absolutePath to the [Foo.java] file, including full file name, where the main 
	 * method is. 
	 * @param targetDirectoryIn is directory where the compiled [Foo.class] file will be placed.
	 * @param dependenciesPath are the paths to dependent libraries.
	 */
//	public class CompileAndRunJavaFileInPackage extends Thread {
//		private File file;
//		private String targetDirectoryPathIn;
//
//		public CompileAndRunJavaFileInPackage(File file, String targetDirectoryPathIn) {
//			this.file = file;
//			this.targetDirectoryPathIn = targetDirectoryPathIn;
//		}
//
//		public void run() {
//			String absolutePath = file.getAbsolutePath();
//			String fileName = absolutePath.substring(absolutePath.lastIndexOf(slash) + 1).trim();
//			String targetDirectoryPath = formatDirectoryPath(targetDirectoryPathIn);
//			CommandLine clCompileJavaFile = CommandLine.parse(
//					"javac -cp src \"" + absolutePath + "\" -d \"" + targetDirectoryPath + "\""
//					);
//			CommandLine clRunJavaByteCodeFile = CommandLine.parse(
//					"java -classpath \"" + System.getProperty(
//							"java.home") + "\" \"" + targetDirectoryPath + fileName + "\""
//					);	
//			try {
//				executor.execute(clCompileJavaFile);
//				executor.execute(clRunJavaByteCodeFile);
//			} catch (ExecuteException e) {
//				System.err.println("Compile and run file failed. The file was: " + absolutePath);
//				e.printStackTrace();
//			} catch (IOException e) {
//				System.err.println("Something went wrong.");
//				e.printStackTrace();
//			}
//		}
//	}
	
	/**
	 * Compiles and runs a java file using the .metadata file in project folder.
	 * 
	 * @param runFile The file to run, must contain main-method
	 * @param projectFile The folder of the project. Must contain a .metadata file
	 * with directory and sourcepath flags and directories and a bin folder.
	 */
	public class CompileAndRunJavaFileInPackage extends Thread {
		private File runFile;
		private File projectFile;

		public CompileAndRunJavaFileInPackage(File runFile, File projectFile) {
			this.runFile = runFile;
			this.projectFile = projectFile;
		}

		public void run() {
			//Creates runPath within project folder
			String runPath = runFile.getPath();
			String projectPath = projectFile.getPath();
			runPath = runPath.replaceAll(projectPath+"/", "");

			//Creates command for compiling
			String command = "javac " + runPath + " @.metadata";

			//Runs command
			TerminalHelpers.runCommand(command, projectFile);
			
			//Creates runPath without extension from package
			runPath = runPath.replaceAll("src/", "");
			runPath = runPath.replaceAll(".java", "");
			
			//Creates command for running
			command = "java " + runPath;
			
			//Creates new directory folder, bin-folder
			File binFile = new File(projectFile.getPath() + "/bin");

			//Runs command
			TerminalHelpers.runCommand(command, binFile);
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
	private class CompileAndRunJavaFileWithoutPackage extends Thread {
		private String targetDirectoryPathIn;
		private File file;

		public CompileAndRunJavaFileWithoutPackage(File file, String targetDirectoryPathIn) {
			this.file = file;
			this.targetDirectoryPathIn = targetDirectoryPathIn;
		}

		public void run() {
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
	}

	/**
	 * Compiles a [Foo.java] file into a target directory.
	 * 
	 * @param absolutePath to the [Foo.java] file, including full file name, where the main 
	 * method is. 
	 * @param targetDirectoryIn is directory where the compiled [Foo.class] file will be placed.
	 * @param dependenciesPath are the paths to dependent libraries.
	 */
	private class CompileJavaFile extends Thread {
		private String targetDirectoryPathIn;
		private File file;

		public CompileJavaFile(File file, String targetDirectoryPathIn) {
			this.file = file;
			this.targetDirectoryPathIn = targetDirectoryPathIn;
		}
		public void run() {
			String absolutePath = file.getAbsolutePath();
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
	}

	/**
	 * NOT COMPLETE
	 * 
	 * prob. better to put in threads
	 */
	public void terminate() {

	}

	/**
	 * Takes a (String) directory-path and correct it if it misses the final '/'. 
	 * 
	 * @param directoryIn
	 * @return finalDirectory
	 */
	private synchronized String formatDirectoryPath(String targetDirectoryPathIn) {
		String finalDirectoryPath = targetDirectoryPathIn;

		if ((finalDirectoryPath.charAt(finalDirectoryPath.length() - 1)) != slash) {
			finalDirectoryPath += slash;
		}

		return finalDirectoryPath;
	}
}
