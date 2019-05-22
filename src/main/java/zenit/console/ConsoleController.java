package main.java.zenit.console;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.kordamp.ikonli.javafx.FontIcon;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.java.zenit.ConsoleRedirect;
import main.java.zenit.console.helpers.ConsoleHelpers;
import main.java.zenit.ui.MainController;

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
//	private HashMap<ConsoleArea, AnchorPane> consoleList = new HashMap<ConsoleArea, AnchorPane>(); 
	private ArrayList<ConsoleArea> consoleList = new ArrayList<ConsoleArea>();
	private ArrayList<Terminal> terminalList = new ArrayList<Terminal>();
//	private HashMap<Terminal, AnchorPane> terminalList = new HashMap<Terminal, AnchorPane>(); 
	
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
	private AnchorPane rootNode;

	@FXML
	private Button btnNewTerminal;
	
	@FXML
	private Button btnNewConsole;
	
	@FXML
	private Button btnClearConsole;
	
	@FXML
	private FontIcon iconCloseConsoleInstance;
	
	@FXML
	private FontIcon iconTerminateProcess;
	
	@FXML
	private FontIcon iconCloseTerminalInstance;
		
	private AnchorPane terminalAnchorPane; 
	
	private AnchorPane consoleAnchorPane;
	
	private ConsoleArea activeConsole;
	
	private Terminal activeTerminal;
	
	private AnchorPane noConsolePane;
		
	private MainController mainController;
	
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
	
	
	
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
		iconTerminateProcess.setVisible(true);
		iconTerminateProcess.setDisable(false);
		iconCloseConsoleInstance.setVisible(true);
		iconCloseConsoleInstance.setDisable(false);
		iconCloseTerminalInstance.setVisible(false);
		iconCloseTerminalInstance.setDisable(true);
		
			
		if (consoleAnchorPane != null) {
				consoleAnchorPane.toFront();
		}
		
		if(consoleList.size() == 0) {
			createEmptyConsolePane();
		}
		
		
	}
	
	
	/*
	 * Creates and displays an anchorPane when there is no console to display in the console-window
	 */
	private void createEmptyConsolePane() {
		noConsolePane = new AnchorPane();
		fillAnchor(noConsolePane);
		Label label = new Label("No Console To Display");
		noConsolePane.getChildren().add(label);
		label.setFont(new Font(14));
		label.setTextFill(Color.BLACK);
		label.setMaxWidth(Double.MAX_VALUE);
		AnchorPane.setLeftAnchor(label, 0.0);
		AnchorPane.setRightAnchor(label, 0.0);
		label.setAlignment(Pos.CENTER);
		noConsolePane.setStyle("-fx-background-color:#444;");
		
		rootAnchor.getChildren().add(noConsolePane);
		noConsolePane.toFront();
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
		iconTerminateProcess.setVisible(false);
		iconTerminateProcess.setDisable(true);
		iconCloseConsoleInstance.setVisible(false);
		iconCloseConsoleInstance.setDisable(true);
		iconCloseTerminalInstance.setVisible(true);
		iconCloseTerminalInstance.setDisable(false);
				
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
		
		consoleList.add(consoleArea);
		consoleChoiceBox.getItems().add(consoleArea.getID());
		consoleChoiceBox.getSelectionModel().select(consoleArea.getID());
		
		new ConsoleRedirect(consoleArea);	
		showConsoleTabs();
	}

	
	/*
	 * Creates a new Terminal, adds it to the terminal
	 *  AnchorPane and puts it as an option in the
	 * choiceBox.
	 */
	public void newTerminal() {		
		Terminal terminal = new Terminal(createTerminalConfig(), FileSystems.getDefault().getPath(".").toAbsolutePath());
		terminal.setId("Terminal ("+terminalList.size()+")");
		terminalAnchorPane = new AnchorPane();
		terminalAnchorPane.setStyle("-fx-background-color:black");
		
		terminal.setMinHeight(5);
		fillAnchor(terminal);
		fillAnchor(terminalAnchorPane);
		
		terminalAnchorPane.getChildren().add(terminal);
		rootAnchor.getChildren().add(terminalAnchorPane);
		terminalList.add(terminal);
		terminalChoiceBox.getItems().add(terminal.getId());
		terminalChoiceBox.getSelectionModel().select(terminal.getId());
		
		showTerminalTabs();
		
	}
	
	private TerminalConfig createTerminalConfig() {
		TerminalConfig windowsConfig = new TerminalConfig();
		windowsConfig.setBackgroundColor(Color.BLACK);
		windowsConfig.setForegroundColor(Color.WHITE);
		windowsConfig.setCursorBlink(true);
		windowsConfig.setCursorColor(Color.WHITE);
		windowsConfig.setFontFamily("consolas");
		windowsConfig.setFontSize(12);
		windowsConfig.setScrollbarVisible(false);
		
		//TODO Non-windows config (if needed).
		
		
		return (System.getProperty("os.name").startsWith("W") ? windowsConfig : new TerminalConfig());
	}
	

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
	
	
	public void closeComponent() {
		mainController.closeConsoleComponent();
	}
	
	/**
	 * Performs initialization steps.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		consoleChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			
			if(newValue != null) {
				for(ConsoleArea ca : consoleList) {
					if(newValue == ca.getID()) {
						ca.getParent().toFront();
						activeConsole = ca;
					}
				}
			}
			
		});
		
		terminalChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			if(newValue != null) {
				for(Terminal t : terminalList) {
					if(newValue == t.getId()) {
						t.getParent().toFront();
						activeTerminal = t;
						t.onTerminalFxReady(()-> {
							t.focusCursor();
						});
					}
				}
			}
		});
		
		newConsole();
		//Console
		iconCloseConsoleInstance.setOnMouseClicked(e -> {
				rootAnchor.getChildren().remove(activeConsole.getParent());
				consoleList.remove(activeConsole);
				consoleChoiceBox.getItems().remove(activeConsole.getID());
				consoleChoiceBox.getSelectionModel().selectLast();

				if(consoleList.size() == 0) {
					createEmptyConsolePane();
				}
		});
		
		iconCloseConsoleInstance.setOnMouseEntered(e -> {
			iconCloseConsoleInstance.setIconColor(Paint.valueOf("rgb(255,255,255)"));
		});
		
		iconCloseConsoleInstance.setOnMouseExited(e -> {
			iconCloseConsoleInstance.setIconColor(Paint.valueOf("#666"));
		});
		
		//Terminal
		iconCloseTerminalInstance.setOnMouseClicked(e ->{
			
			if(terminalList.size() > 1) {
				rootAnchor.getChildren().remove(activeTerminal.getParent());
				terminalList.remove(activeTerminal);
				terminalChoiceBox.getItems().remove(activeTerminal.getId());
				terminalChoiceBox.getSelectionModel().selectLast();
			}
			
			
		});
		
		iconCloseTerminalInstance.setOnMouseEntered(e -> {
			iconCloseTerminalInstance.setIconColor(Paint.valueOf("rgb(255,255,255)"));
		});
		
		iconCloseTerminalInstance.setOnMouseExited(e -> {
			iconCloseTerminalInstance.setIconColor(Paint.valueOf("#666"));
		});
		
		
		
	}
}