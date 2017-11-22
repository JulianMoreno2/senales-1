package core.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import core.provider.ProviderPlotterApp;
import core.service.io.FileCsv;
import core.service.filter.LowPassFilterService;
import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import plot.PlotterApp;
import plot.plotter.plot.PlotBuilder;

public class OpenLowPassFilterPlotEventHandler implements EventHandler<ActionEvent> {

    private LowPassFilterService lowPassFilterService;
    private PublishSubject<ArrayList<FileCsv>> publishSubject;

    public OpenLowPassFilterPlotEventHandler(LowPassFilterService lowPassFilterService, PublishSubject<ArrayList<FileCsv>> publishSubject) {
        this.lowPassFilterService = lowPassFilterService;
        this.publishSubject = publishSubject;
    }

    public void handle(ActionEvent event) {

        publishSubject.subscribe(fileCsvs -> {

            ArrayList<Double> data = fileCsvs.stream().findFirst().get().getArrayListDataFileCsv();
            //TODO: apply lowPassFilterService to signal points

            try {
                ProviderPlotterApp.provide().startLowPassFilterPlot(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
