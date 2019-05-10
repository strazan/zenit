package main.java.zenit.settingspanel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.java.zenit.zencodearea.ZenCodeArea;


/**
 * Controller class for the NewTextSize window,
 * @author Sigge Labor
 *
 */
public class SettingsPanelController extends AnchorPane {

	private int oldSize;
	private ZenCodeArea codeArea;
	private Stage window;

	@FXML
	private TextField fldNewSize;

	@FXML
	private Label lblOldTextSize;

	@FXML
	private Slider sldrNewSize;
	
	@FXML
	private Label lblOldFont;
	
	@FXML
	private Label lblCurrentJavaHome;
	
	@FXML
	private Label newJavaHome;
	
	@FXML
	private Label lblTxtAppeaSize;
	
	@FXML
	private ChoiceBox<String> chcbxNewFont;
	
	@FXML
	private Button btnTextAppearance;
	
	@FXML
	private Button btnJavaHome;
	
	@FXML
	private AnchorPane pnlTextAppearance;
	
	@FXML
	private AnchorPane pnlJavaHome;

	/**
	 * constructs a controller for the TextSizeWindow. 
	 * @param codeArea the ZenCodeArea that will be modified.
	 */
	public SettingsPanelController() {
		this(new ZenCodeArea());
	}
	
	public SettingsPanelController(ZenCodeArea codeArea) {
		this.codeArea = codeArea;
		oldSize = codeArea.getFontSize();

		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/zenit/settingspanel/SettingsPanel.fxml"
		));

		
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		window = new Stage();
		Scene scene = new Scene(this);
		
		window.setScene(scene);
		window.setTitle("preferences");
		initialize();
		scene.getStylesheets().add(getClass().getResource("/zenit/settingspanel/settingspanelstylesheet.css").toString());
		window.show();
	}
	
	/**
	 * Sets the font of the given ZenCodeArea.
	 * @param newFont the font to be applied.
	 */
	public void setNewFont(String newFont) {
		chcbxNewFont.setValue(newFont);
		codeArea.setFont(newFont);
	}
	
	/**
	 * Sets the font size of the given ZenCodeArea.
	 * @param newFontSize the font size to be applied.
	 */
	public void setNewFontSize(long newFontSize) {
		long size = newFontSize;
		fldNewSize.textProperty().setValue(String.valueOf(size));
		if(size > 100) {
			size = 100;
		}
		else if(size < 6) {
			size = 6;
		}
		sldrNewSize.setValue(size);
		this.codeArea.setFontSize((int)size);
	}
	
	/**
	 * Moves a panel to the front, and thereby makes it visible.
	 * @param e
	 */
	public void panelToFront(Event e) {
		if(e.getSource() == btnTextAppearance) {
			pnlTextAppearance.toFront();
		}
		else if(e.getSource() == btnJavaHome) {
			pnlJavaHome.toFront();
		}
	}
	
	@FXML
	private void setNewJavaHome() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(window);

		if(selectedDirectory == null){
		     //No Directory selected
		}
		else{
			Thread t = new Thread(new Runnable() {
				public void run() {
					CommandLine clCompileJavaFile = CommandLine.parse(
						"export JAVA_HOME=" + selectedDirectory.getAbsolutePath()
					);
					DefaultExecutor executor = new DefaultExecutor();
					try {
						executor.execute(clCompileJavaFile);
						newJavaHome.setText(System.getenv("JAVA_HOME"));
						newJavaHome.setStyle("-fx-text-fill: #0B6623;");
					} catch (IOException e) {
					//TODO
				}
			}});
			t.start();
		}
	}
	
	
	private String getJavaHome() {
		
		return null;
	}

	/**
	 * initializing steps. Variables will get their value. ActionListeners added.
	 */
	private void initialize() {
		lblOldTextSize.setText(String.valueOf(oldSize));
		fldNewSize.setText(String.valueOf(oldSize));
		sldrNewSize.setValue(oldSize);
		
		sldrNewSize.valueProperty().addListener(
			(ChangeListener<? super Number>) (arg0, arg1, arg2) -> {
				setNewFontSize(Math.round(sldrNewSize.getValue()));	
		});
		
		fldNewSize.textProperty().addListener((arg0, arg1, arg2) -> {
			try {  
				setNewFontSize(Long.parseLong(fldNewSize.getText()));  
			  } catch(NumberFormatException e){  
				 
			  }  
		});
		
		List<String> fonts = javafx.scene.text.Font.getFamilies();
		
		for(int i = 0; i < fonts.size(); i++) {
			chcbxNewFont.getItems().add(fonts.get(i));
		}
		chcbxNewFont.setValue(codeArea.getFont());
		lblOldFont.setText(codeArea.getFont());
		chcbxNewFont.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
			setNewFont(arg2);
		});
		
		lblCurrentJavaHome.setText(System.getenv("JAVA_HOME"));
	}
}