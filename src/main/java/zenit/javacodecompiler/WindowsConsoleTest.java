package main.java.zenit.javacodecompiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WindowsConsoleTest {
	
	public WindowsConsoleTest(String command) {
		
		try {

			ProcessBuilder builder = new ProcessBuilder();
			builder.command("cmd.exe", "/c", command);

			Process process = builder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new WindowsConsoleTest("ping google.com");
	}
}
