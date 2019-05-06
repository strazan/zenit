package main.java.zenit.textsizewindow;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.java.zenit.zencodearea.ZenCodeArea;


/**
 * Controller class for the NewTextSize window,
 * @author siggelabor
 *
 */
public class TextSizeController extends AnchorPane {

	private int oldSize;
	private ZenCodeArea codeArea;
	private Stage window;

	@FXML
	private TextField fldNewSize;

	@FXML
	private Label lblOldValue;

	@FXML
	private Slider sldrNewSize;

	/**
	 * constructs a controller for the TextSizeWindow. 
	 * @param codeArea the ZenCodeArea that will have its font size modified.
	 */
	public TextSizeController(ZenCodeArea codeArea) {
		this.codeArea = codeArea;
		oldSize = codeArea.getFontSize();

		FXMLLoader loader = new FXMLLoader(
			getClass().getResource("/zenit/textsizewindow/NewTextSize.fxml"
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
		window.setTitle("Zenit");
		initialize();
		window.show();
	}
	
	/**
	 * Closes the entire NewTextSize window.
	 */
	public void exitFrame() {
		window.close();
	}
	
	/**
	 * Sets the font size of the given ZenCodeArea.
	 * @param newFontSize the font size to be applied.
	 */
	public void setNewFontSize(long newFontSize) {
		long size = newFontSize;
		if(size > 100) {
			fldNewSize.textProperty().setValue(String.valueOf(size));
			size = 100;
		}
		else if(size < 6) {
			fldNewSize.textProperty().setValue(String.valueOf(size));
			size = 6;
		}
		this.codeArea.setFontSize((int)size);
	}

	@SuppressWarnings("unchecked")
	/**
	 * initializing steps. Variables will get their value. ActionListeners added.
	 */
	private void initialize() {
		lblOldValue.setText(String.valueOf(oldSize));
		fldNewSize.setText(String.valueOf(oldSize));
		sldrNewSize.setValue(oldSize);
		sldrNewSize.valueProperty().addListener(
			(ChangeListener) (arg0, arg1, arg2) -> setNewFontSize(Math.round(sldrNewSize.getValue()
		)));
		fldNewSize.textProperty().addListener((arg0, arg1, arg2) -> {
			try {  
				setNewFontSize(Long.parseLong(fldNewSize.getText()));  
			  } catch(NumberFormatException e){  
			    
			  }  
		});
	}
}