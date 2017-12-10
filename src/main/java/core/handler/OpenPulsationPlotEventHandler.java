package core.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import core.provider.PlotterAppProvider;
import core.service.PulsationService;
import io.reactivex.subjects.PublishSubject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import core.service.io.FileCsv;
import core.service.io.OpenFileService;

public class OpenPulsationPlotEventHandler implements EventHandler<ActionEvent> {

	private PulsationService pulsationService;
	private PublishSubject<ArrayList<FileCsv>> publishSubject;
	private OpenFileService openFileService;

	public OpenPulsationPlotEventHandler(OpenFileService openFileService, PulsationService pulsationService, PublishSubject<ArrayList<FileCsv>> publishSubject) {
		this.openFileService = openFileService;
		this.pulsationService = pulsationService;
		this.publishSubject = publishSubject;
	}

	public void handle(ActionEvent event) {

		publishSubject.subscribe(fileCsvs -> {
            ArrayList<Double> data = fileCsvs.stream().findFirst().get().getArrayListDataFileCsv();
            int pulsationsQuantity = pulsationService.getPulsationsQuantity(data);
            int arrhythmiaQuantityPoint = pulsationService.getArrhythmiaQuantityPoint(data);

            try {
				PlotterAppProvider.provide().startPulsationPlot(data, pulsationsQuantity, arrhythmiaQuantityPoint);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		openFileService.openFile();
	}
}