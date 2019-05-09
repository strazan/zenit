package main.java.zenit.javacodecompiler;

import java.io.File;

public class CommandBuilder {

	public static final String RUN = "java";
	public static final String COMPILE = "javac";

	String tool;
	String directory;
	String sourcepath;
	String[] libraries;
	String runPath;

	public CommandBuilder(String tool) {
		this.tool = tool;
	}

	public void setDirectory(String directory) {
		this.directory = "-d " + directory;
	}

	public void setSourcepath(String sourcepath) {
		this.sourcepath = "-sourcepath " + sourcepath;
	}

	public void setLibraries(File[] libraries) {
		int length = libraries.length;
		this.libraries = new String[length];

		for (int i = 0; i < length; i++) {
			this.libraries[i] = libraries[i].getPath();
		}
	}

	public void setRunPath(String runPath) {
		this.runPath = runPath;
	}

	public String generateCommand() {
		String command = tool;
		
		if (libraries != null) {
			command += " -cp " + libraries[0];
			if (libraries.length > 1) {
				for (int i = 1; i < libraries.length; i++) {
					command += ":" + libraries[i];
				}
			}
		}

		if (directory != null) {
			command += " " + directory;
		}
		if (sourcepath != null) {
			command += " " + sourcepath;
		}

		if (runPath != null) {
			command += " " + runPath;
		}

		return command;
	}
}