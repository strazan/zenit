package main.java.zenit.console;

import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.java.zenit.ConsoleRedirect;

/**
 * The controller class for ConsoleArea
 * 
 * @author siggelabor
 *
 */
public class ConsoleController implements Initializable {

	/*
	 * These HashMaps are ugly.
	 */
	private HashMap<String, AnchorPane> consoleList = new HashMap<String, AnchorPane>(); 
	private HashMap<String, AnchorPane> terminalList = new HashMap<String, AnchorPane>(); 
	
	@FXML 
	private TabPane consoleTabPane;

	@FXML
	private Label lblTerminal;

	@FXML
	private Label lblConsole;

	@FXML
	private ChoiceBox<String> consoleChoiceBox; 

	@FXML
	private ChoiceBox<String> terminalChoiceBox;
	
	@FXML
	private AnchorPane consoleAnchor;

	@FXML
	private Button btnNewTerminal;
	
	@FXML
	private Button btnNewConsole;
	
	@FXML
	private Button btnClear;
	
	/**
	 * Shows the choiceBox with console areas, and sets the choiceBox with terminal tabs to not 
	 * visible. Also sets text color of the labels.
	 */
	public void showConsoleTabs() {
		lblConsole.setTextFill(Color.DARKGREY);
		lblTerminal.setTextFill(Color.BLACK);
		terminalChoiceBox.setVisible(false);
		terminalChoiceBox.setDisable(true);
		consoleChoiceBox.setVisible(true);
		consoleChoiceBox.setDisable(false);
		btnNewTerminal.setVisible(false);
		btnNewConsole.setVisible(true);
		btnClear.setDisable(false);
	}
	/**
	 * Shows the choiceBox with terminal panes, and sets the choiceBox with console tabs to not 
	 * visible. Also sets text color of the labels.
	 */
	public void showTerminalTabs() {
		lblTerminal.setTextFill(Color.DARKGREY);
		lblConsole.setTextFill(Color.BLACK);
		consoleChoiceBox.setVisible(false);
		consoleChoiceBox.setDisable(true);
		terminalChoiceBox.setVisible(true);
		terminalChoiceBox.setDisable(false);
		btnNewTerminal.setVisible(true);
		btnNewConsole.setVisible(false);
		btnClear.setDisable(true);
		
	}

	/**
	 * Creates a new ConsoleArea, adds it to the console AnchorPane and puts it as an option in the
	 * choiceBox.
	 */
	public void startNewConsole() {
		ConsoleArea console = new ConsoleArea("Console (" + consoleList.size() + ")");
		AnchorPane anchorPane = new AnchorPane();
		
		fillAnchor(console);
		fillAnchor(anchorPane);
		
		/*
		 * TODO 
		 * this should probably be reworked, so that 'ID' isn't used. 
		 */
		
		anchorPane.getChildren().add(console);
		consoleAnchor.getChildren().add(anchorPane);
		
		consoleList.put(console.getID(), anchorPane);
		updateConsoleList(console.getID());
		
		new ConsoleRedirect(console);	
	}

	/**
	 * updates the choiceBox the consoleAreas. 
	 * @param id
	 */
	public void updateConsoleList(String id) {
		boolean didExist = false;
		for(int i = 0; i < consoleChoiceBox.getItems().size(); i++ ) {
			if(consoleChoiceBox.getItems().get(i).equals(id)) {
				consoleChoiceBox.getItems().remove(i);
				didExist = true;
			}
		}
		if(!didExist) {
			consoleChoiceBox.getItems().add(id);
			consoleChoiceBox.setValue(id);
			consoleList.get(id).toFront();
		}	
	}
	
	public void updateTerminalList(String id ) {
		boolean didExist = false;
		for(int i = 0; i < terminalChoiceBox.getItems().size(); i++ ) {
			if(terminalChoiceBox.getItems().get(i).equals(id)) {
				terminalChoiceBox.getItems().remove(i);
				didExist = true;
			}
		}
		if(!didExist) {
			terminalChoiceBox.getItems().add(id);
			terminalChoiceBox.setValue(id);
			terminalList.get(id).toFront();
		}	
	}
	
	public void newTerminal() {
		TerminalConfig darkConfig = new TerminalConfig();
		darkConfig.setBackgroundColor(Color.BLACK);
		darkConfig.setForegroundColor(Color.WHITE);
		darkConfig.setCursorBlink(true);
		darkConfig.setCursorColor(Color.WHITE);
		darkConfig.setFontFamily("consolas");
		darkConfig.setFontSize(12);
		
		
		Terminal terminal = new Terminal(darkConfig, Paths.get(System.getProperty("user.home")));
		terminal.setId("Terminal ("+terminalList.size()+")");
		AnchorPane anchorPane = new AnchorPane();
		
		fillAnchor(terminal);
		fillAnchor(anchorPane);
		
		anchorPane.getChildren().add(terminal);
		consoleAnchor.getChildren().add(anchorPane);
		terminalList.put(terminal.getId(), anchorPane);
		updateTerminalList(terminal.getId());
		
		showTerminalTabs();
	}
	
	// TODO maybe add to (create) package 'helpers' 
	/**
	 * sets the anchor of a node to fill parent 
	 * 
	 * @param node to fill to parent anchor
	 */
	public void fillAnchor(Node node) {
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
	}
	
	public void clearConsole() {
		// TODO
	}

	/**
	 * Performs initialization steps.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		consoleChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			consoleList.get(newValue).toFront();
		});
		
		terminalChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			terminalList.get(newValue).toFront();
		});

		showConsoleTabs();

	}
}