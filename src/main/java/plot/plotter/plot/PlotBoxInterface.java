package plot.plotter.plot;

import java.io.*;
import java.util.Vector;

interface PlotBoxInterface {

    void addCaptionLine(String captionLine);

    void addLegend(int dataset, String legend);

    void addXTick(String label, double position);

    void addYTick(String label, double position);

    void clear(boolean axes);

    void clearCaptions();

    void clearLegends();

    void deferIfNecessary(Runnable action);

    void destroy();

    void fillPlot();

    Vector getCaptions();

    boolean getColor();

    Object[] getColors();

    boolean getGrid();

    String getLegend(int dataset);

    int getLegendDataset(String legend);

    Object getPlotRectangle();

    String getTitle();

    double[] getXAutoRange();

    String getXLabel();

    boolean getXLog();

    double[] getXRange();

    Vector[] getXTicks();

    double[] getYAutoRange();

    String getYLabel();

    boolean getYLog();

    double[] getYRange();

    Vector[] getYTicks();

    void read(InputStream in) throws IOException;

    void read(String command);

    void removeLegend(int dataset);

    void renameLegend(int dataset, String newName);

    void repaint();

    void resetAxes();

    void samplePlot();

    void setAutomaticRescale(boolean automaticRescale);

    void setBackground(Object background);

    void setButtons(boolean visible);

    void setCaptions(Vector captionStrings);

    void setColor(boolean useColor);

    void setColors(Object[] colors);

    void setForeground(Object foreground);

    void setGrid(boolean grid);

    void setLabelFont(String name);

    void setPlotRectangle(Object rectangle);

    void setSize(int width, int height);

    void setTimedRepaint(boolean timedRepaint);

    void setTitle(String title);

    void setTitleFont(String name);

    void setWrap(boolean wrap);

    void setXLabel(String label);

    void setXLog(boolean xlog);

    void setXRange(double min, double max);

    void setYLabel(String label);

    void setYLog(boolean ylog);

    void setYRange(double min, double max);

    void write(OutputStream out);

    void write(OutputStream out, String dtd);

    void write(Writer out, String dtd);

    void writeData(PrintWriter output);

    void writeFormat(PrintWriter output);

    void zoom(double lowx, double lowy, double highx, double highy);
}
