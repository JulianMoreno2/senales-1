package plotter.plot;

public class PlotBuilder {

    public Plot buildPlotContext(String title) {

        Plot plot = new Plot();

        plot.setSize(350, 300);
        plot.setButtons(true);
        plot.setTitle(title);

        plot.setXRange(0, 100);
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
}
