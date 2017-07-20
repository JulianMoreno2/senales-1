package untref;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import untref.interfacebuilders.MenuBarBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PrincipalGraphicInterface extends Application {
	

	@Override
	public void start(Stage primaryStage) {
		 
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 400, 100, Color.WHITE);

		MenuBar menuBar = new MenuBarBuilder().build();

		root.setTop(menuBar);
		primaryStage.setTitle("Procesamiento de Senales I");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}