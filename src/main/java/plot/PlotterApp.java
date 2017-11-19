package plot;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import core.service.FileCsv;
import core.service.PulsationService;
import plot.builder.Builder;
import plot.builder.FFTBuilder;
import plot.function.Sin;
import plot.model.Function;
import plot.plotter.plot.Plot;
import plot.plotter.plot.PlotApplication;
import plot.plotter.plot.PlotBuilder;

public class PlotterApp {

    private ArrayList<FileCsv> arrayfileCsv;

    public PlotterApp(ArrayList<FileCsv> arrayfileCsv) {
        this.arrayfileCsv = arrayfileCsv;
    }

    public void start(PlotBuilder plotBuilder) throws IOException {

        FileCsv fileCsv = arrayfileCsv.stream().findFirst().get();
        Plot[] plotCsvFile = getCsvPoints(plotBuilder, fileCsv.getArrayListDataFileCsv());

        SwingUtilities.invokeLater(() -> new PlotApplication(plotCsvFile[0]));
    }

    private Plot[] getCsvPoints(PlotBuilder plotBuilder, ArrayList<Double> points) {
        PulsationService pulsations = new PulsationService(points);
        int quantityPulsation = pulsations.getPulsationsQuantity();
        return plotBuilder
                .buildPlotFromCsv(points, "Pulsations per minute:"
                        + String.valueOf(quantityPulsation + "  ArrhythmiaQuantityPoint: "
                        + String.valueOf(pulsations.getArrhythmiaQuantityPoint())));
    }
}