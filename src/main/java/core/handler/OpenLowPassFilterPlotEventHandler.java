package core.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import core.provider.PlotterAppProvider;
import core.service.io.FileCsv;
import core.service.io.OpenFileService;
import core.service.filter.LowPassFilterService;
import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import plot.PlotterApp;
import plot.plotter.plot.PlotBuilder;

public class OpenLowPassFilterPlotEventHandler implements EventHandler<ActionEvent> {

	private OpenFileService openFileService;
    private LowPassFilterService lowPassFilterService;
    private PublishSubject<ArrayList<FileCsv>> publishSubject;
	private Integer frecuency;
	private Integer order;

    public OpenLowPassFilterPlotEventHandler(OpenFileService openFileService, LowPassFilterService lowPassFilterService, PublishSubject<ArrayList<FileCsv>> publishSubject, Integer frecuency, Integer order) {
    	this.openFileService = openFileService;
        this.lowPassFilterService = lowPassFilterService;
        this.publishSubject = publishSubject;
        this.frecuency = frecuency;
        this.order = order;
    }

    public void handle(ActionEvent event) {

        this.publishSubject.subscribe(fileCsvs -> {

            ArrayList<Double> data = fileCsvs.stream().findFirst().get().getArrayListDataFileCsv();
			ArrayList<Double> points = this.lowPassFilterService.apply(data, this.frecuency, this.order);

            try {
                PlotterAppProvider.provide().startLowPassFilterPlot(points);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        this.openFileService.openFile();
    }
}
