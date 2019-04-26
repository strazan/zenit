package main.java.zenit.console;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleAreaOutputStream extends OutputStream {

	private ConsoleArea ca;

	private StringBuilder sb = new StringBuilder();

	public ConsoleAreaOutputStream(ConsoleArea coar) {
		
		this.ca = coar;
	}

	@Override
	public void flush() {
		ca.outPrint(sb.toString());
		sb.setLength(0);
	}

	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}

	@Override
	public void write(int b) {
		
		if (b == '\n') {
			ca.outPrint(sb.toString() + "\n");
			sb.setLength(0);
			return;
		}
		if (b == '\r') {
			return;
		}
		
		sb.append((char)b);
		
		ca.outPrint(Character.toString((char)b));
	}

	@Override
	public void write(byte b[], int off, int len) {
		
		 ca.outPrint(new String(b, off, len));
	}
}