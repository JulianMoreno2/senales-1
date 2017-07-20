import builder.FFTBuilder;
import model.Function;
import plotter.plot.*;

import javax.swing.*;

public class PlotterApp {

    public static void main(String[] args) {

        FFTBuilder fftBuilder = new FFTBuilder();
        PlotBuilder plotBuilder = new PlotBuilder();

        Function function = new Function(16, -2 * Math.random() + 1, 0);
        Plot[] plots = fftBuilder.plotFFT(function, plotBuilder.buildPlotContext("Real Plot"), plotBuilder.buildPlotContext("Imaginary Plot"));

        Runnable doAction = () -> {
            PlotApplication plotApplication = new PlotApplication(plots[0]);
        };

        SwingUtilities.invokeLater(doAction);
    }
}