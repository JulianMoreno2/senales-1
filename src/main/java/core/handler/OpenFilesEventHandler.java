package core.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
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
import core.service.FileCsv;
import core.service.FileIOService;

public class OpenFilesEventHandler implements EventHandler<ActionEvent> {

    private FileChooser fileChooser;
    private FileIOService fileIOService;
    private PublishSubject<ArrayList<FileCsv>> publishSubject;

    public OpenFilesEventHandler(FileChooser fileChooser, FileIOService fileIOService,
            PublishSubject<ArrayList<FileCsv>> publishSubject) {
        this.fileChooser = fileChooser;
        this.fileIOService = fileIOService;
        this.publishSubject = publishSubject;
    }

    public void handle(ActionEvent event) {

        try {
            ArrayList<FileCsv> fileCsvs = new ArrayList<>();
            List<File> file = fileIOService.openFiles(fileChooser);

            for (File filess : file) {
                FileCsv files = new FileCsv(filess.getPath());
                fileCsvs.add(files);
            }

            publishSubject.onNext(fileCsvs);

        } catch (NullPointerException e) {
            try {
                throwPopup(new Stage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void throwPopup(final Stage stage) throws Exception {

        final Button popupButton = new Button("OK");
        final VBox layout = new VBox(60);
        layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
        layout.getChildren().add(createPopupContent());
        layout.getChildren().addAll(popupButton);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setMinHeight(20);
        stage.setMinWidth(300);
        popupButton.setOnAction(t -> stage.close());
        stage.show();
    }

    private VBox createPopupContent() {
        final Label label = new Label();
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setText("You must choose a file");
        final VBox wizBox = new VBox(5);
        wizBox.setAlignment(Pos.CENTER);
        wizBox.getChildren().setAll(label);
        return wizBox;
    }
}