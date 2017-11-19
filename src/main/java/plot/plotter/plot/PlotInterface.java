package plot.plotter.plot;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public interface PlotInterface extends PlotBoxInterface {

    @Override
    void addLegend(int dataset, String legend);

    void addPoint(final int dataset, final double x, final double y,
            final boolean connected);

    void addPointWithErrorBars(final int dataset, final double x,
            final double y, final double yLowEB, final double yHighEB,
            final boolean connected);

    @Override
    void clear(final boolean format);

    void clear(final int dataset);

    void erasePoint(final int dataset, final int index);

    @Override
    void fillPlot();

    boolean getConnected();

    boolean getImpulses();

    boolean getLineStyles();

    String getMarksStyle();

    int getNumDataSets();

    boolean getReuseDatasets();

    void markDisconnections(boolean value);

    @Override
    void read(InputStream inputStream) throws IOException;

    @Override
    void samplePlot();

    void setBars(boolean on);

    void setBars(double width, double offset);

    void setConnected(boolean on);

    void setConnected(boolean on, int dataset);

    void setImpulses(boolean on);

    void setImpulses(boolean on, int dataset);

    void setLineStyle(String styleString, int dataset);

    void setLineStyles(boolean lineStyles);

    void setMarksStyle(String style);

    void setMarksStyle(String style, int dataset);

    void setPointsPersistence(int persistence);

    void setReuseDatasets(boolean on);

    void setXPersistence(double persistence);

    @Override
    void writeData(PrintWriter output);

    @Override
    void writeFormat(PrintWriter output);
}
