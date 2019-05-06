package zenit.console;

import java.io.IOException;
import java.io.OutputStream;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.scene.control.TextArea;

public class InlineCssOStream extends OutputStream {

	private InlineCssTextArea ta;

	private StringBuilder sb = new StringBuilder();

	public InlineCssOStream(InlineCssTextArea ta) {
		this.ta = ta;
	}

	@Override
	public void flush() {
		ta.appendText(sb.toString());
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
			ta.appendText(sb.toString() + "\n");
			sb.setLength(0);
			return;
		}
		if (b == '\r') {
			return;
		}
		
		sb.append((char)b);
		
		ta.appendText(Character.toString((char)b));
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		ta.appendText(new String(b, off, len));
	}
}
