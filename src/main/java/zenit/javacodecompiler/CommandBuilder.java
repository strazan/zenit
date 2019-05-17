package main.java.zenit.javacodecompiler;

import java.io.File;

public class CommandBuilder {

	public static final String RUN = "java";
	public static final String COMPILE = "javac";

	String tool;
	String directory;
	String sourcepath;
	String[] internalLibraries;
	String[] externalLibraries;
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

	public void setInternalLibraries(String[] internalLibraries) {
		this.internalLibraries = internalLibraries;
	}
	
	public void setExternalLibraries(String[] externalLibraries) {
		this.externalLibraries = externalLibraries;
	}

	public void setRunPath(String runPath) {
		this.runPath = runPath;
	}
	
	private void mergeLibraries() {
		int intLength = 0;
		int extLength = 0;
		if (internalLibraries != null) {
			intLength = internalLibraries.length;
		}
		if (externalLibraries != null) {
			extLength = externalLibraries.length;
		}
		libraries = new String[intLength + extLength];
		for (int i = 0; i < libraries.length; i++) {
			if (i < intLength) {
				libraries[i] = internalLibraries[i];
			} else {
				libraries[i] = externalLibraries[i-intLength];
			}
		}
	}

	public String generateCommand() {
		String command = tool;
		
		mergeLibraries();
		
		if (libraries != null) {
			if (tool.equals(COMPILE)) {
				command += " -cp " + libraries[0];
				if (libraries.length > 1) {
					for (int i = 1; i < libraries.length; i++) {
						command += ":" + libraries[i];
					}
				}
			} else if (tool.equals(RUN) ) {
				command += " -cp \".." + File.separator + libraries[0];
				if (libraries.length > 1) {
					for (int i = 1; i < libraries.length; i++) {
						command += ":.." + File.separator + libraries[i];
					}
				}
				command += ":.\"";
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