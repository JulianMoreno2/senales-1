package core.handler;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import core.service.FileCsv;

public class OpenPlotEventHandler implements EventHandler<ActionEvent> {

	private PublishSubject<ArrayList<FileCsv>> publishSubject;

	public OpenPlotEventHandler(PublishSubject<ArrayList<FileCsv>> publishSubject) {
		this.publishSubject = publishSubject;
	}

	public void handle(ActionEvent event) {

		publishSubject.subscribe(fileCsvs -> {
			plot.PlotterApp plotterApp = new plot.PlotterApp(fileCsvs);

			try {
				plotterApp.start(new plot.plotter.plot.PlotBuilder());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}
}