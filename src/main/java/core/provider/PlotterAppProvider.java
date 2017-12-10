package core.provider;

import plot.PlotterApp;
import plot.plotter.plot.PlotBuilder;

public class PlotterAppProvider {

    public static PlotterApp provide() {
        return new PlotterApp(new PlotBuilder());
    }
}
