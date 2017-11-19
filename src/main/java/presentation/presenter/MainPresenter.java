package presentation.presenter;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import presentation.presenter.Presenter.View;
import core.handler.OpenFilesEventHandler;
import core.handler.OpenPulsationPlotEventHandler;
import core.service.FileCsv;
import core.service.FileIOService;

public class MainPresenter extends Presenter<View> {

    private FileChooser fileChooser;
    private FileIOService fileIOService;
    private PublishSubject<ArrayList<FileCsv>> fileCsvPublishSubject;

    public MainPresenter(FileChooser fileChooser, FileIOService fileIOService,
            PublishSubject<ArrayList<FileCsv>> fileCsvPublishSubject) {
        this.fileChooser = fileChooser;
        this.fileIOService = fileIOService;
        this.fileCsvPublishSubject = fileCsvPublishSubject;
    }

    public EventHandler<ActionEvent> onClickOpenFile() {
        return new OpenFilesEventHandler(fileChooser, fileIOService, fileCsvPublishSubject);
    }

    public EventHandler<ActionEvent> onClickOpenPlot() {
        return new OpenPulsationPlotEventHandler(fileCsvPublishSubject);
    }

    public interface View extends Presenter.View {

    }
}
