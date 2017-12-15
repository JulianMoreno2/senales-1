package plot;

import plot.plotter.plot.Plot;
import plot.plotter.plot.PlotApplication;
import plot.plotter.plot.PlotBuilder;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlotterApp {

    private PlotBuilder plotBuilder;

    public PlotterApp(PlotBuilder plotBuilder) {
        this.plotBuilder = plotBuilder;
    }

    public void startPulsationPlot(List<Double> points,
                                   Integer quantityPulsation,
                                   Integer arrhythmiaQuantityPoint) throws IOException {

        Plot[] plotCsvFile = plotBuilder.buildPlotFromCsv(points, "Pulsations per minute:"
                + String.valueOf(quantityPulsation + "  ArrhythmiaQuantityPoint: "
                + String.valueOf(arrhythmiaQuantityPoint)));

        SwingUtilities.invokeLater(() -> new PlotApplication(plotCsvFile[0]));
    }

    public void startSimplePlot(List<Double> points) throws IOException {
    	Plot[] plotCsvFile = plotBuilder.buildPlotFromCsv(points, "Plot");

        SwingUtilities.invokeLater(() -> new PlotApplication(plotCsvFile[0]));
    }
}