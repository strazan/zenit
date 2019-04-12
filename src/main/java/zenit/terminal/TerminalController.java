package main.java.zenit.terminal;

import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TerminalController {

	
	@FXML
	private TabPane tabPane;
	
	public void initialize() {
		addTerminalTab();
	}
	
	private void addTerminalTab() {
		TerminalConfig darkConfig = new TerminalConfig();
		darkConfig.setBackgroundColor(Color.rgb(16, 16, 16));
		darkConfig.setForegroundColor(Color.rgb(240, 240, 240));
		darkConfig.setCursorColor(Color.rgb(255, 0, 0, 0.5));
		
		TerminalBuilder builder = new TerminalBuilder(darkConfig);
		TerminalTab terminalTab = builder.newTerminal();
		
		tabPane.getTabs().add(terminalTab);

	}
}
