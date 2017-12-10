package presentation.presenter;

import java.util.ArrayList;

import core.handler.OpenLowPassFilterPlotEventHandler;
import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import presentation.presenter.Presenter.View;
import core.handler.OpenPulsationPlotEventHandler;
import core.service.io.FileCsv;
import static core.provider.ServiceProvider.provideLowPassFilterService;
import static core.provider.ServiceProvider.providePulsationService;
import static core.provider.ServiceProvider.provideOpenFileService;

public class MainPresenter extends Presenter<View> {
	
    private PublishSubject<ArrayList<FileCsv>> fileCsvPublishSubject;
    private String frecuency;

    public MainPresenter(PublishSubject<ArrayList<FileCsv>> fileCsvPublishSubject) {
        this.fileCsvPublishSubject = fileCsvPublishSubject;
        frecuency = "0";
    }

    public EventHandler<ActionEvent> onClickOpenPulsationPlot() {
        return new OpenPulsationPlotEventHandler(provideOpenFileService(), providePulsationService(), fileCsvPublishSubject);
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
