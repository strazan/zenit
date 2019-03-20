package zenit.ui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class MainController {
	@FXML
	private TextArea taConsole;
	
	@FXML
	private MenuItem openFile;
	
	@FXML
	public void openFile(Event e) {
		// TODO: open file explorer and get text
	}
}
