package zenit.console;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleAreaOutputStream extends OutputStream {

	private ConsoleArea ca;

	private StringBuilder sb = new StringBuilder();

	public ConsoleAreaOutputStream(ConsoleArea ca) {
		this.ca = ca;
	}

	@Override
	public void flush() {
		ca.appendText(sb.toString());
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
			ca.appendText(sb.toString() + "\n");
			sb.setLength(0);
			return;
		}
		if (b == '\r') {
			return;
		}
		
		sb.append((char)b);
		
		ca.replaceText(Character.toString((char)b));
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		ca.replaceText(new String(b, off, len));
	}
}
