package plot.plotter.plot;

import plot.builder.Builder;
import core.model.Complex;

import java.util.ArrayList;

public class PlotBuilder extends Builder{

    private Plot buildPlotContext(String title) {

        Plot plot = new Plot();

        plot.setSize(350, 300);
        plot.setButtons(true);
        plot.setTitle(title);

        plot.setXRange(-10, 10);
        plot.setYRange(-10, 10);

        plot.setXLabel("time");
        plot.setYLabel("value");

        plot.addYTick("-PI", -Math.PI);
        plot.addYTick("-PI/2", -Math.PI / 2);
        plot.addYTick("0", 0);
        plot.addYTick("PI/2", Math.PI / 2);
        plot.addYTick("PI", Math.PI);

        plot.setMarksStyle("none");
        plot.setImpulses(true);

        plot.setConnected(false, 1);

        return plot;
    }

    @Override
    public Plot[] build(Plot realPlot, Plot imaginaryPlot, Complex[] functionPoints) {
        return buildPlotFunction(functionPoints, new Plot[]{realPlot, imaginaryPlot});
    }

    public Plot[] buildPlotFromCsv(ArrayList<Double> csvFilePoints,String name) {
        Complex[] functionPoints = new Complex[csvFilePoints.size()];

        for(int i = 0; i < functionPoints.length; i++){
            functionPoints[i] = new Complex(csvFilePoints.get(i), 0);
        }

        return build(buildPlotContext(name),
                buildPlotContext("Imaginary Plot"), functionPoints);
    }
}
