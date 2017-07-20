package builder;

import model.Complex;
import model.Function;
import plotter.plot.Plot;

public abstract class Builder {

    public abstract Plot[] build(Function function, Plot realPlot, Plot imaginaryPlot);

    protected Plot[] buildPlotFunction(Complex[] function, Plot[] complexPlot) {
        return new Plot[]{createRealPlot(function, complexPlot[0]), createImaginaryPlot(function, complexPlot[1])};
    }

    protected Plot createImaginaryPlot(Complex[] function, Plot plot) {
        boolean first = true;

        for (int i = 0; i < function.length; i++) {
            plot.addPoint(0, i, function[i].im(), !first);
            first = false;
        }

        return plot;

    }

    protected Plot createRealPlot(Complex[] function, Plot plot) {
        boolean first = true;
        for (int i = 0; i < function.length; i++) {
            plot.addPoint(0, i, function[i].re(), !first);
            first = false;
        }

        return plot;
    }
}
