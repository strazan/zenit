package main.java.zenit.terminal;

import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class TerminalController {

	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private AnchorPane basePane;
	
	public void initialize() {
		
		addTerminalTab();
	}
	
	private void addTerminalTab() {
		TerminalConfig darkConfig = new TerminalConfig();
		darkConfig.setBackgroundColor(Color.BLACK);
		darkConfig.setForegroundColor(Color.WHITE);
		darkConfig.setCursorBlink(true);
		darkConfig.setCursorColor(Color.WHITE);
		darkConfig.setFontFamily("consolas");
		
		
		TerminalBuilder builder = new TerminalBuilder(darkConfig);
		TerminalTab terminalTab = builder.newTerminal();
		tabPane.getTabs().add(terminalTab);
		
	}
}

