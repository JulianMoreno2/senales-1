import plotter.plot.Plot;
import plotter.plot.PlotApplication;
import plotter.plot.PlotFrame;

import javax.swing.*;
import java.awt.*;

public class Example extends PlotFrame {

    public Plot plotGraph(){
        Plot plot = new Plot();

        setSize(800, 300);

        plot.setSize(350, 300);
        plot.setButtons(true);
        plot.setTitle("Left Plot");

        plot.setYRange(-4, 4);
        plot.setXRange(0, 100);

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

        boolean first = true;

        for (int i = 0; i <= 100; i++) {
            plot.addPoint(0, i, 5 * Math.cos((Math.PI * i) / 20), !first);
            plot.addPoint(1, i, 4.5 * Math.cos((Math.PI * i) / 25), !first);
            plot.addPoint(2, i, 4 * Math.cos((Math.PI * i) / 30), !first);
            plot.addPoint(3, i, 3.5 * Math.cos((Math.PI * i) / 35), !first);
            plot.addPoint(4, i, 3 * Math.cos((Math.PI * i) / 40), !first);
            plot.addPoint(5, i, 2.5 * Math.cos((Math.PI * i) / 45), !first);
            plot.addPoint(6, i, 2 * Math.cos((Math.PI * i) / 50), !first);
            plot.addPoint(7, i, 1.5 * Math.cos((Math.PI * i) / 55), !first);
            plot.addPoint(8, i, 1 * Math.cos((Math.PI * i) / 60), !first);
            plot.addPoint(9, i, 0.5 * Math.cos((Math.PI * i) / 65), !first);
            first = false;
        }

        plot.setSize(350, 300);

        return plot;

    }

    public static void main(String[] args) {
        Runnable doAction = () -> {
            Example example = new Example();
            PlotApplication plotApplication = new PlotApplication(example.plotGraph());
        };

        SwingUtilities.invokeLater(doAction);
    }
}