package main.java.zenit.javacodecompiler;

import java.io.File;

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

	public JavaSourceCodeCompiler() {
	}
	
	/** 
	 * starts a new thread where a .java file (in a defined package) will be compiled into chosen
	 * directory and executed
	 * @param file to be compiled and executed
	 * @param targetDirectoryPath
	 */
	public void compileAndRunJavaFileInPackage(File file, File metadata) {
		new CompileAndRunJavaFileInPackage(file, metadata).start();
	}

	/** 
	 * starts a new thread where a .java file (without a defined package) will be compiled into
	 * chosen directory and executed
	 * @param file to be compiled and executed
	 * @param targetDirectoryPath
	 */
	public void compileAndRunJavaFileWithoutPackage(File file) {
		new CompileAndRunJavaFileWithoutPackage(file).start();
	}
	
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
			runPath = runPath.replaceAll(projectPath+File.separator, "");

			//Creates command for compiling
			String command = "javac " + runPath + " @.metadata";

			//Runs command
			TerminalHelpers.runCommand(command, projectFile);
			
			//Creates runPath without extension from package
			runPath = runPath.replaceAll("src" + File.separator, "");
			runPath = runPath.replaceAll(".java", "");
			
			//Creates command for running
			command = "java " + runPath;
			
			//Creates new directory folder, bin-folder
			File binFile = new File(projectFile.getPath() + File.separator + "bin");

			//Runs command
			TerminalHelpers.runCommand(command, binFile);
		}
	}
	
	public class CompileAndRunJavaFileWithoutPackage extends Thread {
		private File runFile;

		public CompileAndRunJavaFileWithoutPackage(File runFile) {
			this.runFile = runFile;
		}

		public void run() {
			//Creates runPath within project folder
			String className = runFile.getName();

			//Creates command for compiling
			String command = "javac " + className;

			//Runs command
			TerminalHelpers.runCommand(command, runFile.getParentFile());
			
			//Creates runPath without extension from package
			className = className.replaceAll(".java", "");
			
			//Creates command for running
			command = "java " + className;

			//Runs command
			TerminalHelpers.runCommand(command, runFile.getParentFile());
		}
	}

	/**
	 * NOT COMPLETE
	 * 
	 * prob. better to put in threads
	 */
	public void terminate() {

	}
}
