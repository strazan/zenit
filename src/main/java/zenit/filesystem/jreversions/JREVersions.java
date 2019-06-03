package main.java.zenit.filesystem.jreversions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import main.java.zenit.Zenit;

public class JREVersions {

	public static void createNew() {

		try {
			File file = new File("res/JDK/JDK.dat");

			if (!file.exists()) {
				file.createNewFile();
			}

			ArrayList<File> JVMsList = new ArrayList<File>();
			File javaFolder = getJVMDirectory();

			if (javaFolder != null && javaFolder.exists()) {

				File[] JVMs = javaFolder.listFiles();
				for (File JVM : JVMs) {
					JVMsList.add(JVM);
				}

				if (JVMsList.size() > 0) {
					write(JVMsList);
				}
			}

		} catch (IOException e) {
		}
	}
	
	public static void main(String[] args) {
		setDefaultJDKFile(null);
	}

	public static List<File> read() {
		
		ArrayList<File> JDKs = new ArrayList<File>();
		File file;
		
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream("res/JDK/JDK.dat")))) {
			file = (File) ois.readObject();
			
			while (file != null) {
				JDKs.add(file);
				file = (File) ois.readObject();
			}
			
		
		} catch (IOException | ClassNotFoundException e) {
			
		}
		
		return JDKs;
	}
	
	public static List<String> readString() {
		List<String> JDKsString = new ArrayList<String>();
		List<File> JDKs = read();

		if (JDKs.size() > 0) {
			for (File JDK : JDKs) {
				JDKsString.add(JDK.getName());
			}
		}

		return JDKsString;

	}
	
	public static void write(List<File> files) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream("res/JDK/JDK.dat")))) {
			
			for (File file : files) {
				oos.writeObject(file);
			}
			oos.flush();
		} catch (IOException e) {
			
		}
	}
	
	public static boolean append(File file) {
		boolean success = false;
		
		if (JDKVerifier.validJDK(file)) {
			List<File> files = read();
			files.add(file);
			
			write(files);
			
			success = true;
		}
		
		return success;

	}
	
	public static boolean remove(File file) {
		List<File> files = read();
		boolean success = files.remove(file);
		
		if (success) {
			write(files);
		}
		
		return success;
	}
	
	public static File getJVMDirectory() {
		String OS = Zenit.OS;
		if (OS.equals("Mac OS X")) {
			return new File("/library/java/javavirtualmachines");
		} else if (OS.equals("Linux")) {
			return new File("/usr/lib/jvm");
		} else if (OS.equals("Windows")) {
			return new File("C:\\Program Files\\Java\\");
		}
		
		return null;
	}
	
	public static String getFullPathFromName(String name) {
		List<File> JDKs = read();
		
		for (File JDK : JDKs) {
			if (JDK.getName().equals(name)) {
				return JDK.getPath();
			}
		}
		
		return null;
	}

	public static void setDefaultJDKFile(File file) {
		File defaultJDK = new File("res/JDK/DefaultJDK.dat");
			
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream(defaultJDK)))) {

			if (!defaultJDK.exists()) {
				defaultJDK.createNewFile();
			}
			
			oos.writeObject(file);
			oos.flush();

		} catch (IOException e) {
		}
	}
	
	public static File getDefaultJDKFile() {
		
		File defaultJDK = new File("res/JDK/DefaultJDK.dat");
		
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream(defaultJDK)))) {
			return (File) ois.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
		
	}
}
