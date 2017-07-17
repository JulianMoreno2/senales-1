package plotter.plot;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PlotPoint implements Serializable {

    public boolean connected = false;

    public boolean errorBar = false;

    public double originalx;

    public double x;

    public double y;

    public double yLowEB;

    public double yHighEB;
}
