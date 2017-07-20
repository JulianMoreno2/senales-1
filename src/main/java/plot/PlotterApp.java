package plot;

import plot.builder.Builder;
import plot.builder.FFTBuilder;
import plot.function.Sin;
import plot.model.Function;
import plot.plotter.plot.Plot;
import plot.plotter.plot.PlotApplication;
import plot.plotter.plot.PlotBuilder;
import untref.pulsations.PulsationsCalculations;
import untref.repository.FileCsv;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;

public class PlotterApp {

    private ArrayList<FileCsv> arrayfileCsv;

    public PlotterApp(ArrayList<FileCsv> arrayfileCsv) {
        this.arrayfileCsv = arrayfileCsv;
    }

    public void start() {
        FileCsv fileCsv = null;
        Iterator<FileCsv> nombreIterator = arrayfileCsv.iterator();
        while (nombreIterator.hasNext()) {
            fileCsv = nombreIterator.next();
        }
        
        PlotBuilder plotBuilder = new PlotBuilder();

        Plot[] fftPoints = getFFTPoints(plotBuilder);

        Plot[] sinPoints = getSinPoints(plotBuilder);
      
        Plot[] plotCsvFile = getCsvPoints(plotBuilder, fileCsv.getArrayListDataFileCsv());

        Runnable doAction = () -> {
            PlotApplication plotApplication = new PlotApplication(plotCsvFile[0]);
            
           
        };

        SwingUtilities.invokeLater(doAction);
    }

    private static Plot[] getFFTPoints(PlotBuilder plotBuilder) {
        Builder fftBuilder = new FFTBuilder();
        Function function = new Function(2, Math.sin(Math.PI), 0);

        return fftBuilder.build(plotBuilder.buildPlotContext("Real Plot"),
                plotBuilder.buildPlotContext("Imaginary Plot"), function.buildSinPoints(2));
    }

    private static Plot[] getSinPoints(PlotBuilder plotBuilder) {
        Sin sin = new Sin();

        return plotBuilder.build(plotBuilder.buildPlotContext("Real Plot"),
                plotBuilder.buildPlotContext("Imaginary Plot"), sin.points(512));
    }

    private static Plot[] getCsvPoints(PlotBuilder plotBuilder, ArrayList<Double> csvFilePoints) {
    	
    	PulsationsCalculations pulsations=new PulsationsCalculations(csvFilePoints);
        int quantityPulsation=pulsations.getPulsationsQuantity();
        return plotBuilder.buildPlotFromCsv(csvFilePoints,"Pulsations per minute:" +String.valueOf(quantityPulsation+ "  ArrhythmiaQuantityPoint: "+String.valueOf(pulsations.getArrhythmiaQuantityPoint())) );
    }
}