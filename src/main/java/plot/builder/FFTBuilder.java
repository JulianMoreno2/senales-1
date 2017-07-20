package plot.builder;

import plot.function.FFT;
import plot.model.Complex;
import plot.plotter.plot.Plot;

public class FFTBuilder extends Builder{

    public Plot[] build(Plot realPlot, Plot imaginaryPlot, Complex[] functionPoints){

        Complex[] fft = FFT.build(functionPoints);

        return buildPlotFunction(fft, new Plot[]{realPlot, imaginaryPlot});
    }

}
