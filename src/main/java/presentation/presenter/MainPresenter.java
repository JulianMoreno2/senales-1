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
import core.handler.OpenFileEventHandler;
import core.handler.OpenPulsationPlotEventHandler;
import core.provider.ServiceProvider;
import core.service.io.FileCsv;
import core.service.io.FileIOService;

import static core.provider.ServiceProvider.provideLowPassFilterService;
import static core.provider.ServiceProvider.providePulsationService;

public class MainPresenter extends Presenter<View> {

    private FileChooser fileChooser;
    private FileIOService fileIOService;
    private PublishSubject<ArrayList<FileCsv>> fileCsvPublishSubject;
    private String frecuency;

    public MainPresenter(FileChooser fileChooser, FileIOService fileIOService,
                         PublishSubject<ArrayList<FileCsv>> fileCsvPublishSubject) {
        this.fileChooser = fileChooser;
        this.fileIOService = fileIOService;
        this.fileCsvPublishSubject = fileCsvPublishSubject;
        frecuency = "0";
    }

    public EventHandler<ActionEvent> onClickOpenFile() {
        return new OpenFileEventHandler(fileChooser, fileIOService, fileCsvPublishSubject);
    }

    public EventHandler<ActionEvent> onClickOpenPulsationPlot() {
        return new OpenPulsationPlotEventHandler(providePulsationService(), fileCsvPublishSubject);
    }

    public EventHandler<ActionEvent> onClickOpenLowPassFilterPlot(Integer frecuency, Integer order) {
        return new OpenLowPassFilterPlotEventHandler(provideLowPassFilterService(), fileCsvPublishSubject, frecuency, order);
    }

    public interface View extends Presenter.View {

    }

	public String getSignalFrecuency() {
		fileCsvPublishSubject.subscribe(fileCsvs -> {
            ArrayList<Double> data = fileCsvs.stream().findFirst().get().getArrayListDataFileCsv();
            frecuency = "0";
		});
		
		return frecuency;
	}
}
