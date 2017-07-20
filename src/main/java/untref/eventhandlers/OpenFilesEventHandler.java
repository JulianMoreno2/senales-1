package untref.eventhandlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import untref.repository.FileCsv;
import untref.service.FileIOService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenFilesEventHandler implements EventHandler<ActionEvent> {

	private FileChooser fileChooser;
	private ArrayList<FileCsv> arrayfileCsv;
	private FileIOService imageIOService;

	public OpenFilesEventHandler(FileChooser fileChooser, FileIOService imageIOService,
			ArrayList<FileCsv> arrayfileCsv) {
		this.fileChooser = fileChooser;
		this.arrayfileCsv = arrayfileCsv;
		this.imageIOService = imageIOService;
	}

	public void handle(ActionEvent event) {

		try{

			List<File> file = imageIOService.openFiles(fileChooser);

			for (File filess : file) {
				FileCsv files = new FileCsv();
				files.setPathArchivo(filess.getPath());
				arrayfileCsv.add(files);
			}

		} catch (NullPointerException e){
			try {
				throwPopup(new Stage(), "You must choose a file");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void throwPopup(final Stage stage, String message) throws Exception {

		final Button popupButton = new Button("OK");
		final VBox layout = new VBox(60);
		layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
		layout.getChildren().add(createPopupContent(message));
		layout.getChildren().addAll(popupButton);
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.setMinHeight(20);
		stage.setMinWidth(300);
		popupButton.setOnAction(t -> {
			stage.close();
		});
		stage.show();
	}

	private VBox createPopupContent(String message) {
		final Label label = new Label();
		label.setWrapText(true);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setText(message);
		final VBox wizBox = new VBox(5);
		wizBox.setAlignment(Pos.CENTER);
		wizBox.getChildren().setAll(
				label
		);
		return wizBox;
	}
}