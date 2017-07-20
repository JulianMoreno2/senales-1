package untref.eventhandlers;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import plot.PlotterApp;
import untref.repository.FileCsv;

public class OpenPlotEventHandler implements EventHandler<ActionEvent> {
	private ArrayList<FileCsv> arrayfileCsv;

	public OpenPlotEventHandler(ArrayList<FileCsv> arrayfileCsv) {
		this.arrayfileCsv = arrayfileCsv;
	}

	public void handle(ActionEvent event) {
		PlotterApp plotterApp = new PlotterApp(this.arrayfileCsv);
		plotterApp.start();

	}
}