package builder;

import function.FFT;
import model.Complex;
import model.Function;
import plotter.plot.Plot;

public class FFTBuilder extends Builder{

    public Plot[] build(Function function, Plot realPlot, Plot imaginaryPlot){

        Complex[] fft = FFT.build(function.buildPoints());

        return buildPlotFunction(fft, new Plot[]{realPlot, imaginaryPlot});
    }

}
