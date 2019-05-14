package main.java.zenit.javacodecompiler;

import java.io.File;
import java.util.regex.Matcher;

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

	public void setLibraries(String[] libraries) {
		int length = libraries.length;
		this.libraries = new String[length];

		for (int i = 0; i < length; i++) {
			this.libraries[i] = libraries[i];
		}
	}

	public void setRunPath(String runPath) {
		this.runPath = runPath;
	}

	public String generateCommand() {
		String command = tool;
		
		
		if (directory != null) {
			command += " " + directory;
		}
		if (sourcepath != null) {
			command += " " + sourcepath;
		}
		if (libraries != null) {
			// TODO Add library(ies)
		}
		if (runPath != null) {
			if (tool.equals(RUN)) {
				runPath = runPath.replaceAll(Matcher.quoteReplacement(File.separator), "/");
			}
			command += " " + runPath;
		}

		return command;
	}
}