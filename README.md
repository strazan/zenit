To run the program:

• change JRE System Library for the project to [Java SE 11.0.2]
(if not downloaded)
Download:  https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

• add following in VM arguemnts inside 'run configurations' :

--module-path lib/javafx-sdk-11.0.2/lib/ --add-modules=javafx.controls,javafx.fxml,javafx.web
 --add-opens
javafx.graphics/javafx.scene.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
--add-opens
javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED
-Dprism.allowhidpi=true

• Run src/zenit/uiTestUI.java 
