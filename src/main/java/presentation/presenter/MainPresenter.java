package presentation.presenter;

import java.util.ArrayList;

import core.handler.OpenLowPassFilterPlotEventHandler;
import core.service.PulsationService;
import core.service.filter.LowPassFilterService;
import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import presentation.presenter.Presenter.View;
import core.handler.OpenFilesEventHandler;
import core.handler.OpenPulsationPlotEventHandler;
import core.service.io.FileCsv;
import core.service.io.FileIOService;

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

    public EventHandler<ActionEvent> onClickOpenPulsationPlot() {
        return new OpenPulsationPlotEventHandler(new PulsationService(), fileCsvPublishSubject);
    }

    public EventHandler<ActionEvent> onClickOpenLowPassFilterPlot() {
        return new OpenLowPassFilterPlotEventHandler(new LowPassFilterService(), fileCsvPublishSubject);
    }

    public interface View extends Presenter.View {

    }
}
