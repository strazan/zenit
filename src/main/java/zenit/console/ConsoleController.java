package main.java.zenit.console;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.java.zenit.ConsoleRedirect;
import main.java.zenit.console.helpers.ConsoleHelpers;

/**
 * The controller class for ConsoleArea
 * 
 * @author siggelabor
 *
 */
/**
 * @author Admin
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
	private Button btnTerminal;

	@FXML
	private Button btnConsole;

	@FXML
	private ChoiceBox<String> consoleChoiceBox; 

	@FXML
	private ChoiceBox<String> terminalChoiceBox;
	
	@FXML
	private AnchorPane rootAnchor;

	@FXML
	private Button btnNewTerminal;
	
	@FXML
	private Button btnNewConsole;
	
	@FXML
	private Button btnClearConsole;
	
	@FXML
	private Button btnClearTerminal;
		
	private AnchorPane terminalAnchorPane; //AnchorPane on which a terminal-instance is located. Used to move a terminal-instance to the "front", 
										   //aka making it visible
	
	private AnchorPane consoleAnchorPane;//AnchorPane on which a console-instance is located. Used to move a console-instance to the "front", 
	   									 //aka making it visible
	private ConsoleArea activeConsole;
	
	private Terminal activeTerminal;
	
	/**
	 * Shows the choiceBox with console areas, and sets the choiceBox with terminal tabs to not 
	 * visible. Also sets text color of the labels.
	 */
	public void showConsoleTabs() {
		
		btnTerminal.setStyle("");
		btnConsole.setStyle("-fx-text-fill:white; -fx-border-color:#666; -fx-border-width: 0 0 2 0;");
		

		terminalChoiceBox.setVisible(false);
		terminalChoiceBox.setDisable(true);
		consoleChoiceBox.setVisible(true);
		consoleChoiceBox.setDisable(false);
		btnNewTerminal.setVisible(false);
		btnNewConsole.setVisible(true);
		btnClearConsole.setDisable(false);
		btnClearConsole.setVisible(true);
		btnClearTerminal.setDisable(true);
		btnClearTerminal.setVisible(false);
			
		if (consoleAnchorPane != null) {
				consoleAnchorPane.toFront();
		}
		
		
	}
	/**
	 * Shows the choiceBox with terminal panes, and sets the choiceBox with console tabs to not 
	 * visible. Also sets text color of the labels.
	 */
	public void showTerminalTabs() {
		btnConsole.setStyle("");
		btnTerminal.setStyle("-fx-text-fill:white; -fx-border-color:#666; -fx-border-width: 0 0 2 0;");
		
		
		if(terminalList.size() == 0) {
			newTerminal();
		}
		else {
			terminalAnchorPane.toFront();
		}
		
		
		consoleChoiceBox.setVisible(false);
		consoleChoiceBox.setDisable(true);
		terminalChoiceBox.setVisible(true);
		terminalChoiceBox.setDisable(false);
		btnNewTerminal.setVisible(true);
		btnNewConsole.setVisible(false);
		btnClearConsole.setDisable(true);
		btnClearConsole.setVisible(false);
		btnClearTerminal.setDisable(false);
		btnClearTerminal.setVisible(true);
				
	}

	/**
	 * Creates a new ConsoleArea, adds it to the console AnchorPane and puts it as an option in the
	 * choiceBox.
	 */
	public void newConsole() {
		ConsoleArea consoleArea = new ConsoleArea("Console ("+ consoleList.size()+")");
		consoleAnchorPane = new AnchorPane();
		
		fillAnchor(consoleArea);
		fillAnchor(consoleAnchorPane);
		
		/*
		 * TODO 
		 * this should probably be reworked, so that 'ID' isn't used. 
		 */
		
		consoleAnchorPane.getChildren().add(consoleArea);
		rootAnchor.getChildren().add(consoleAnchorPane);
		
		consoleList.put(consoleArea.getID(), consoleAnchorPane);
		updateConsoleList(consoleArea.getID());
		
		new ConsoleRedirect(consoleArea);	
		showConsoleTabs();
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
	
	
	/*
	 * Creates a new Terminal, adds it to the terminal
	 *  AnchorPane and puts it as an option in the
	 * choiceBox.
	 */
	public void newTerminal() {
		TerminalConfig darkConfig = new TerminalConfig();
		darkConfig.setBackgroundColor(Color.BLACK);
		darkConfig.setForegroundColor(Color.WHITE);
		darkConfig.setCursorBlink(true);
		darkConfig.setCursorColor(Color.WHITE);
		darkConfig.setFontFamily("Ubuntu Mono");
		darkConfig.setFontSize(14);
		
		
		Terminal terminal = new Terminal(darkConfig, FileSystems.getDefault().getPath(".").toAbsolutePath());
		terminal.setId("Terminal ("+terminalList.size()+")");
		terminalAnchorPane = new AnchorPane();
		
		fillAnchor(terminal);
		fillAnchor(terminalAnchorPane);
		
		terminalAnchorPane.getChildren().add(terminal);
		rootAnchor.getChildren().add(terminalAnchorPane);
		terminalList.put(terminal.getId(), terminalAnchorPane);
		updateTerminalList(terminal.getId());
		
		showTerminalTabs();
	}
	
	// TODO maybe add to (create) package 'helpers' //Oskar vad menas??
	
	
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
	
	
	/**
	 * Clears the active consoleArea
	 */
	public void clearConsole() {
		activeConsole.clear();
	}
	
	/**
	 * Clears the active Terminal.
	 */
	public void clearTerminal(){
		if( System.getProperty("os.name").startsWith("Windows") ) {
			ConsoleHelpers.executeTerminalCommand(activeTerminal, "cls");
		}
		else {
			ConsoleHelpers.executeTerminalCommand(activeTerminal, "reset");
		}
				
		//TODO: Make it work when terminal is inside a "process" (when you need to press q, ex after git branch command).
	}
	
	/**
	 * Performs initialization steps.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		consoleChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			consoleList.get(newValue).toFront();
			activeConsole = (ConsoleArea) consoleList.get(newValue).getChildren().get(0);
		});
		
		terminalChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			terminalList.get(newValue).toFront();
			activeTerminal = (Terminal) terminalList.get(newValue).getChildren().get(0);
		});
		
		newConsole();

	}
}