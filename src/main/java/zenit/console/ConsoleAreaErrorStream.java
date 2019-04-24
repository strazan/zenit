package main.java.zenit.console;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleAreaErrorStream extends OutputStream {
	private ConsoleArea ca;

	private StringBuilder sb = new StringBuilder();

	public ConsoleAreaErrorStream(ConsoleArea ca) {
		this.ca = ca;
	}

	@Override
	public void flush() {
		ca.errPrint(sb.toString());
		sb.setLength(0);
	}

	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void write(int b) throws IOException {

		if (b == '\n') {
			ca.errPrint(sb.toString() + "\n");
			sb.setLength(0);
			return;
		}
		if (b == '\r') {
			return;
		}
		
		sb.append((char)b);
		
		ca.errPrint(Character.toString((char)b));
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		ca.errPrint(new String(b, off, len));
	}
}