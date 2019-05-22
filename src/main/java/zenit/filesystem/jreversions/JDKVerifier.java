package main.java.zenit.filesystem.jreversions;

import java.io.File;

import main.java.zenit.Zenit;

public class JDKVerifier {
	
	public static boolean validJDK(File JDK) {
		
//		String OS = Zenit.OS;
		File java = new File(getExecutablePath(JDK.getPath(), "java"));
		File javac = new File(getExecutablePath(JDK.getPath(), "javac"));
		
//		if (OS.equals("Mac OS X")) {
//			java = new File(JDK.getPath() + File.separator + "Contents" + File.separator + 
//					"Home" + File.separator + "bin" + File.separator + "java");
//			javac = new File(JDK.getPath() + File.separator + "Contents" + File.separator + 
//					"Home" + File.separator + "bin" + File.separator + "javac");
//		} else if (OS.equals("Windows")) {
////			java = new File(JDK.getPath() + File.separator;
//		} else if (OS.equals("Linux")) {
//			java = new File(JDK.getPath() + File.separator + "bin" + File.separator + "java");
//			javac = new File(JDK.getPath() + File.separator + "bin" + File.separator + "javac");
//		}
		
		return (java != null && javac != null && java.exists() && javac.exists());
		
	}
	
	public static String getExecutablePath(String JDKPath, String tool) {
		String OS = Zenit.OS;
		String path = null;
		
		if (OS.equals("Mac OS X")) {
			path = JDKPath + File.separator + "Contents" + File.separator + 
					"Home" + File.separator + "bin" + File.separator + tool;
		} else if (OS.equals("Windows")) {
//			java = new File(JDK.getPath() + File.separator;
		} else if (OS.equals("Linux")) {
			path = JDKPath + File.separator + "bin" + File.separator + tool;
		}
		
		return path;
	}

}
