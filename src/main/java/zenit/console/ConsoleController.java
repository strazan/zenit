package main.java.zenit.console;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ConsoleController extends Application implements Initializable {

	private HashMap<String, AnchorPane> consoleList = new HashMap<String, AnchorPane>(); 
	private HashMap<String, AnchorPane> terminalList = new HashMap<String, AnchorPane>(); 
	
	@FXML 
	private TabPane consoleTabPane;

	@FXML
	private Label lblTerminal;

	@FXML
	private Label lblConsole;

	@FXML
	private ChoiceBox<String> consoleChoiceBox; //= new ChoiceBox<String>();

	@FXML
	private ChoiceBox<String> terminalChoiceBox;
	
	@FXML
	private AnchorPane consoleAnchor;

	@FXML
	private Label buttonNewTerminal;
	
	@FXML
	private Button buttonNewConsole;

	public void showConsoleTabs() {
		lblConsole.setTextFill(Color.DARKGREY);
		lblTerminal.setTextFill(Color.BLACK);
		terminalChoiceBox.setVisible(false);
		terminalChoiceBox.setDisable(true);
		consoleChoiceBox.setVisible(true);
		consoleChoiceBox.setDisable(false);
		buttonNewTerminal.setVisible(false);
		buttonNewConsole.setVisible(true);
	}

	public void showTerminalTabs() {
		lblTerminal.setTextFill(Color.DARKGREY);
		lblConsole.setTextFill(Color.BLACK);
		consoleChoiceBox.setVisible(false);
		consoleChoiceBox.setDisable(true);
		terminalChoiceBox.setVisible(true);
		terminalChoiceBox.setDisable(false);
		buttonNewTerminal.setVisible(true);
		buttonNewConsole.setVisible(false);
	}

	public void startNewConsole() {
		ConsoleArea console = new ConsoleArea("console (" + consoleList.size() + ")");
		AnchorPane anchorPane = new AnchorPane();
		
		fillAnchor(console);
		fillAnchor(anchorPane);
		
		anchorPane.getChildren().add(console);
		consoleAnchor.getChildren().add(anchorPane);
		
		consoleList.put(console.getID(), anchorPane);
		updateConsoleList(console.getID());
	}

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
	
	public void newTerminal() {
		terminalChoiceBox.getItems().add("bash " + terminalChoiceBox.getItems().size());
		terminalChoiceBox.setValue("bash " + terminalChoiceBox.getItems().size());
	}
	 
	public void fillAnchor(Node node) {
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
	}

	@Override
	public void start(Stage stage) throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("ConsolePane.fxml"));
		ConsoleController consoleController = new ConsoleController();
		loader.setController(consoleController);
		Parent root = loader.load();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("consoleStyle.css").toString());
		stage.setScene(scene);
		stage.setTitle("console");

		stage.show();

	}
	
	public void clearConsole() {
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		consoleChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			consoleList.get(newValue).toFront();
		});

		showConsoleTabs();
		terminalChoiceBox.getItems().add("bash");
		terminalChoiceBox.setValue("bash");
	}
}