package plot.builder;

import plot.model.Complex;
import plot.plotter.plot.Plot;

public abstract class Builder {

    public abstract Plot[] build(Plot realPlot, Plot imaginaryPlot, Complex[] x);

    protected Plot[] buildPlotFunction(Complex[] functionPoints, Plot[] complexPlot) {
        return new Plot[]{createRealPlot(functionPoints, complexPlot[0]), createImaginaryPlot(functionPoints, complexPlot[1])};
    }

    private Plot createImaginaryPlot(Complex[] functionPoints, Plot plot) {
        boolean first = true;

        for (int i = 0; i < functionPoints.length; i++) {
            plot.addPoint(0, i, functionPoints[i].im(), !first);
            first = false;
        }

        return plot;
    }

    private Plot createRealPlot(Complex[] functionPoints, Plot plot) {
        boolean first = true;
        for (int i = 0; i < functionPoints.length; i++) {
            for(int k = 0; k < 8; k++){
                plot.addPoint(0, i, functionPoints[i].re(), !first);
            }
            first = false;
        }

        return plot;
    }
}
