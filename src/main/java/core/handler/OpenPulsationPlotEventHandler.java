package core.handler;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import core.service.FileCsv;
import plot.PlotterApp;
import plot.plotter.plot.PlotBuilder;

public class OpenPulsationPlotEventHandler implements EventHandler<ActionEvent> {

	private PublishSubject<ArrayList<FileCsv>> publishSubject;

	public OpenPulsationPlotEventHandler(PublishSubject<ArrayList<FileCsv>> publishSubject) {
		this.publishSubject = publishSubject;
	}

	public void handle(ActionEvent event) {

		publishSubject.subscribe(fileCsvs -> {

			PlotterApp plotterApp = new PlotterApp(fileCsvs);

			try {
				plotterApp.startPulsationPlot(new PlotBuilder());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}
}