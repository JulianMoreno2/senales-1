package plot.plotter.plot;

import java.io.*;
import java.net.URL;
import java.util.Vector;

public interface PlotBoxInterface {

    public void addCaptionLine(String captionLine);

    public void addLegend(int dataset, String legend);

    public void addXTick(String label, double position);

    public void addYTick(String label, double position);

    public void clear(boolean axes);

    public void clearCaptions();

    public void clearLegends();

    public void deferIfNecessary(Runnable action);

    public void destroy();

    public void fillPlot();

    public Vector getCaptions();

    public boolean getColor();

    public Object[] getColors();

    public boolean getGrid();

    public String getLegend(int dataset);

    public int getLegendDataset(String legend);

    public Object getPlotRectangle();

    public String getTitle();

    public double[] getXAutoRange();

    public String getXLabel();

    public boolean getXLog();

    public double[] getXRange();

    public Vector[] getXTicks();

    public double[] getYAutoRange();

    public String getYLabel();

    public boolean getYLog();

    public double[] getYRange();

    public Vector[] getYTicks();

    public void read(InputStream in) throws IOException;

    public void read(String command);

    public void removeLegend(int dataset);

    public void renameLegend(int dataset, String newName);

    public void repaint();

    public void resetAxes();

    public void samplePlot();

    public void setAutomaticRescale(boolean automaticRescale);

    public void setBackground(Object background);

    public void setButtons(boolean visible);

    public void setCaptions(Vector captionStrings);

    public void setColor(boolean useColor);

    public void setColors(Object[] colors);

    public void setForeground(Object foreground);

    public void setGrid(boolean grid);

    public void setLabelFont(String name);

    public void setPlotRectangle(Object rectangle);

    public void setSize(int width, int height);

    public void setTimedRepaint(boolean timedRepaint);

    public void setTitle(String title);

    public void setTitleFont(String name);

    public void setWrap(boolean wrap);

    public void setXLabel(String label);

    public void setXLog(boolean xlog);

    public void setXRange(double min, double max);

    public void setYLabel(String label);

    public void setYLog(boolean ylog);

    public void setYRange(double min, double max);

    public void write(OutputStream out);

    public void write(OutputStream out, String dtd);

    public void write(Writer out, String dtd);

    public void writeData(PrintWriter output);

    public void writeFormat(PrintWriter output);

    public void zoom(double lowx, double lowy, double highx, double highy);
}
