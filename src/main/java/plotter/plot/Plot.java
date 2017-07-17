package plotter.plot;

import plotter.util.RunnableExceptionCatcher;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Plot extends PlotBox implements PlotInterface {

    @Override
    public synchronized void addLegend(int dataset, String legend) {
        _checkDatasetIndex(dataset);

        if (!_reuseDatasets) {
            super.addLegend(dataset, legend);
        } else {
            String possibleLegend = getLegend(dataset);

            if (possibleLegend == null || (!possibleLegend.equals(legend))) {
                super.addLegend(dataset, legend);
            }
        }
    }

    @Override
    public synchronized void addPoint(final int dataset, final double x,
            final double y, final boolean connected) {
        Runnable doAddPoint = new RunnableExceptionCatcher(new Runnable() {
            @Override
            public void run() {
                _addPoint(dataset, x, y, 0, 0, connected, false);
            }
        });

        deferIfNecessary(doAddPoint);
    }

    @Override
    public synchronized void addPointWithErrorBars(final int dataset,
            final double x, final double y, final double yLowEB,
            final double yHighEB, final boolean connected) {
        Runnable doAddPoint = new RunnableExceptionCatcher(new Runnable() {
            @Override
            public void run() {
                _addPoint(dataset, x, y, yLowEB, yHighEB, connected, true);
            }
        });

        deferIfNecessary(doAddPoint);
    }

    @Override
    public synchronized void clear(final boolean format) {
        Runnable doClear = new RunnableExceptionCatcher(new Runnable() {
            @Override
            public void run() {
                _clear(format);
            }
        });

        deferIfNecessary(doClear);
    }

    @Override
    public synchronized void clear(final int dataset) {
        Runnable doClear = new RunnableExceptionCatcher(new Runnable() {
            @Override
            public void run() {
                _clear(dataset);
            }
        });

        deferIfNecessary(doClear);
    }

    @Override
    public synchronized void erasePoint(final int dataset, final int index) {
        Runnable doErasePoint = new RunnableExceptionCatcher(new Runnable() {
            @Override
            public void run() {
                _erasePoint(dataset, index);
            }
        });

        deferIfNecessary(doErasePoint);
    }

    @Override
    public synchronized void fillPlot() {
        Runnable doFill = new RunnableExceptionCatcher(new Runnable() {
            @Override
            public void run() {
                _fillPlot();
            }
        });

        deferIfNecessary(doFill);
    }

    @Override
    public boolean getConnected() {
        return _connected;
    }

    @Override
    public boolean getImpulses() {
        return _impulses;
    }

    @Override
    public boolean getLineStyles() {
        return _lineStyles;
    }

    @Override
    public synchronized String getMarksStyle() {
        if (_marks == 0) {
            return "none";
        } else if (_marks == 1) {
            return "points";
        } else if (_marks == 2) {
            return "dots";
        } else if (_marks == 3) {
            return "various";
        } else if (_marks == 4) {
            return "bigdots";
        } else {
            return "pixels";
        }
    }

    @Override
    @Deprecated
    public int getMaxDataSets() {
        return Integer.MAX_VALUE;
    }

    @Override
    public synchronized int getNumDataSets() {
        return _points.size();
    }

    @Override
    public boolean getReuseDatasets() {
        return _reuseDatasets;
    }

    @Override
    @Deprecated
    public void parseFile(String filespec, URL documentBase) {
        _firstInSet = true;
        _sawFirstDataSet = false;
        super.parseFile(filespec, documentBase);
    }

    @Override
    public void markDisconnections(boolean value) {
        _markDisconnections = value;
    }

    @Override
    public synchronized void read(InputStream inputStream) throws IOException {
        super.read(inputStream);
        _firstInSet = true;
        _sawFirstDataSet = false;
    }

    @Override
    public synchronized void samplePlot() {
        Runnable sample = new RunnableExceptionCatcher(() -> {
            synchronized (Plot.this) {
                clear(true);

                setTitle("Sample plot");
                setYRange(-4, 4);
                setXRange(0, 100);
                setXLabel("time");
                setYLabel("value");
                addYTick("-PI", -Math.PI);
                addYTick("-PI/2", -Math.PI / 2);
                addYTick("0", 0);
                addYTick("PI/2", Math.PI / 2);
                addYTick("PI", Math.PI);
                setMarksStyle("none");
                setImpulses(true);

                boolean first = true;

                for (int i = 0; i <= 100; i++) {
                    double xvalue = i;

                    addPoint(0, xvalue, 5 * Math.cos(Math.PI * i / 20),
                            !first);
                    addPoint(1, xvalue, 4.5 * Math.cos(Math.PI * i / 25),
                            !first);
                    addPoint(2, xvalue, 4 * Math.cos(Math.PI * i / 30),
                            !first);
                    addPoint(3, xvalue, 3.5 * Math.cos(Math.PI * i / 35),
                            !first);
                    addPoint(4, xvalue, 3 * Math.cos(Math.PI * i / 40),
                            !first);
                    addPoint(5, xvalue, 2.5 * Math.cos(Math.PI * i / 45),
                            !first);
                    addPoint(6, xvalue, 2 * Math.cos(Math.PI * i / 50),
                            !first);
                    addPoint(7, xvalue, 1.5 * Math.cos(Math.PI * i / 55),
                            !first);
                    addPoint(8, xvalue, 1 * Math.cos(Math.PI * i / 60),
                            !first);
                    addPoint(9, xvalue, 0.5 * Math.cos(Math.PI * i / 65),
                            !first);
                    first = false;
                }
            }

            repaint();
        });

        deferIfNecessary(sample);
    }

    @Override
    public void setBars(boolean on) {
        _plotImage = null;
        _bars = on;
    }

    @Override
    public synchronized void setBars(double width, double offset) {
        _plotImage = null;
        _barWidth = width;
        _barOffset = offset;
        _bars = true;
    }

    @Override
    public void setConnected(boolean on) {
        _plotImage = null;
        _connected = on;
    }

    @Override
    public synchronized void setConnected(boolean on, int dataset) {
        _plotImage = null;
        _checkDatasetIndex(dataset);

        Format fmt = _formats.get(dataset);
        fmt.connected = on;
        fmt.connectedUseDefault = false;
    }

    @Override
    public synchronized void setImpulses(boolean on) {
        _plotImage = null;
        _impulses = on;
    }

    @Override
    public synchronized void setImpulses(boolean on, int dataset) {
        _plotImage = null;
        _checkDatasetIndex(dataset);

        Format fmt = _formats.get(dataset);
        fmt.impulses = on;
        fmt.impulsesUseDefault = false;
    }

    @Override
    public synchronized void setLineStyle(String styleString, int dataset) {
        float[] dashvalues;
        _plotImage = null;
        _checkDatasetIndex(dataset);

        Format format = _formats.get(dataset);
        if (styleString.equalsIgnoreCase("solid")) {
            format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0);
        } else if (styleString.equalsIgnoreCase("dotted")) {
            dashvalues = new float[2];
            dashvalues[0] = (float) 2.0;
            dashvalues[1] = (float) 2.0;
            format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
        } else if (styleString.equalsIgnoreCase("dashed")) {
            dashvalues = new float[2];
            dashvalues[0] = (float) 8.0;
            dashvalues[1] = (float) 4.0;
            format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
        } else if (styleString.equalsIgnoreCase("dotdashed")) {
            dashvalues = new float[4];
            dashvalues[0] = (float) 2.0;
            dashvalues[1] = (float) 2.0;
            dashvalues[2] = (float) 8.0;
            dashvalues[3] = (float) 2.0;
            format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
        } else if (styleString.equalsIgnoreCase("dotdotdashed")) {
            dashvalues = new float[6];
            dashvalues[0] = (float) 2.0;
            dashvalues[1] = (float) 2.0;
            dashvalues[2] = (float) 2.0;
            dashvalues[3] = (float) 2.0;
            dashvalues[4] = (float) 8.0;
            dashvalues[5] = (float) 2.0;
            format.lineStroke = new BasicStroke(_width, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, dashvalues, 0);
        } else {
            StringBuffer results = new StringBuffer();
            for (String style : java.util.Arrays.asList(_LINE_STYLES_ARRAY)) {
                if (results.length() > 0) {
                    results.append(", ");
                }
                results.append("\"" + style + "\"");
            }
            throw new IllegalArgumentException("Line style \"" + styleString
                    + "\" is not found, style must be one of " + results);
        }
        format.lineStyle = styleString;
        format.lineStyleUseDefault = false;
    }

    @Override
    public synchronized void setLineStyles(boolean lineStyles) {
        _plotImage = null;
        _lineStyles = lineStyles;
        if (!_lineStyles) {
            for (Format fmt : _formats) {
                fmt.lineStyle = null;
                fmt.lineStroke = null;
                fmt.lineStyleUseDefault = true;
            }
        }
    }

    @Override
    public synchronized void setMarksStyle(String style) {
        _plotImage = null;

        if (style.equalsIgnoreCase("none")) {
            _marks = 0;
        } else if (style.equalsIgnoreCase("points")) {
            _marks = 1;
        } else if (style.equalsIgnoreCase("dots")) {
            _marks = 2;
        } else if (style.equalsIgnoreCase("various")) {
            _marks = 3;
        } else if (style.equalsIgnoreCase("bigdots")) {
            _marks = 4;
        } else if (style.equalsIgnoreCase("pixels")) {
            _marks = 5;
        }
    }

    @Override
    public synchronized void setMarksStyle(String style, int dataset) {
        _plotImage = null;
        _checkDatasetIndex(dataset);

        Format fmt = _formats.get(dataset);

        if (style.equalsIgnoreCase("none")) {
            fmt.marks = 0;
        } else if (style.equalsIgnoreCase("points")) {
            fmt.marks = 1;
        } else if (style.equalsIgnoreCase("dots")) {
            fmt.marks = 2;
        } else if (style.equalsIgnoreCase("various")) {
            fmt.marks = 3;
        } else if (style.equalsIgnoreCase("bigdots")) {
            fmt.marks = 4;
        } else if (style.equalsIgnoreCase("pixels")) {
            fmt.marks = 5;
        }

        fmt.marksUseDefault = false;
    }

    @Override
    @Deprecated
    public void setNumSets(int numSets) {
        _plotImage = null;

        if (numSets < 1) {
            throw new IllegalArgumentException("Number of data sets ("
                    + numSets + ") must be greater than 0.");
        }

        _currentdataset = -1;
        _points.clear();
        _bins.clear();
        _formats.clear();
        _prevxpos.clear();
        _prevypos.clear();
        _prevErasedxpos.clear();
        _prevErasedypos.clear();
        _lastPointWithExtraDot.clear();

        for (int i = 0; i < numSets; i++) {
            _points.add(new ArrayList<PlotPoint>());
            _formats.add(new Format());
            _prevxpos.add(_INITIAL_PREVIOUS_VALUE);
            _prevypos.add(_INITIAL_PREVIOUS_VALUE);
            _prevErasedxpos.add(_INITIAL_PREVIOUS_VALUE);
            _prevErasedypos.add(_INITIAL_PREVIOUS_VALUE);
            _lastPointWithExtraDot.put(i, null);
        }
    }

    @Override
    public void setPointsPersistence(int persistence) {
        _plotImage = null;
        _pointsPersistence = persistence;
    }

    @Override
    public void setReuseDatasets(boolean on) {
        _plotImage = null;
        _reuseDatasets = on;
    }

    @Override
    public void setXPersistence(double persistence) {
        _plotImage = null;
        _xPersistence = persistence;
    }

    @Override
    public synchronized void writeData(PrintWriter output) {
        super.writeData(output);

        for (int dataset = 0; dataset < _points.size(); dataset++) {
            StringBuffer options = new StringBuffer();

            Format fmt = _formats.get(dataset);

            if (!fmt.connectedUseDefault) {
                if (_isConnected(dataset)) {
                    options.append(" connected=\"yes\"");
                } else {
                    options.append(" connected=\"no\"");
                }
            }

            if (!fmt.impulsesUseDefault) {
                if (fmt.impulses) {
                    options.append(" stems=\"yes\"");
                } else {
                    output.println(" stems=\"no\"");
                }
            }

            if (!fmt.lineStyleUseDefault && fmt.lineStyle.length() > 0) {
                options.append(" lineStyle=\"" + fmt.lineStyle + "\"");
            }

            String legend = getLegend(dataset);

            if (legend != null) {
                options.append(" name=\"" + getLegend(dataset) + "\"");
            }

            output.println("<dataset" + options.toString() + ">");

            ArrayList<PlotPoint> pts = _points.get(dataset);

            for (int pointnum = 0; pointnum < pts.size(); pointnum++) {
                PlotPoint pt = pts.get(pointnum);

                if (!pt.connected) {
                    output.print("<m ");
                } else {
                    output.print("<p ");
                }

                output.print("x=\"" + pt.x + "\" y=\"" + pt.y + "\"");

                if (pt.errorBar) {
                    output.print(" lowErrorBar=\"" + pt.yLowEB
                            + "\" highErrorBar=\"" + pt.yHighEB + "\"");
                }

                output.println("/>");
            }

            output.println("</dataset>");
        }
    }

    @Override
    public synchronized void writeFormat(PrintWriter output) {
        super.writeFormat(output);

        if (_reuseDatasets) {
            output.println("<reuseDatasets/>");
        }

        StringBuffer defaults = new StringBuffer();

        if (!_connected) {
            defaults.append(" connected=\"no\"");
        }

        if (_lineStyles) {
            defaults.append(" lineStyles=\"yes\"");
        }

        switch (_marks) {
        case 0:
            break;
        case 1:
            defaults.append(" marks=\"points\"");
            break;

        case 2:
            defaults.append(" marks=\"dots\"");
            break;

        case 3:
            defaults.append(" marks=\"various\"");
            break;

        case 4:
            defaults.append(" marks=\"bigdots\"");
            break;

        case 5:
            defaults.append(" marks=\"pixels\"");
            break;
        default:
            throw new RuntimeException("Internal Error.  Mark " + "style "
                    + _marks + " not supported.");
        }

        if (_impulses) {
            defaults.append(" stems=\"yes\"");
        }

        if (defaults.length() > 0) {
            output.println("<default" + defaults.toString() + "/>");
        }

        if (_bars) {
            output.println("<barGraph width=\"" + _barWidth + "\" offset=\""
                    + _barOffset + "\"/>");
        }
    }

    protected synchronized void _checkDatasetIndex(int dataset) {
        if (dataset < 0) {
            throw new IllegalArgumentException(
                    "Plot._checkDatasetIndex: Cannot"
                            + " give a negative number for the data set index.");
        }

        while (dataset >= _points.size()) {
            _points.add(new ArrayList<PlotPoint>());
            _bins.add(new ArrayList<Bin>());
            _pointInBinOffset.add(0);
            _formats.add(new Format());
            _prevxpos.add(_INITIAL_PREVIOUS_VALUE);
            _prevypos.add(_INITIAL_PREVIOUS_VALUE);
            _prevErasedxpos.add(_INITIAL_PREVIOUS_VALUE);
            _prevErasedypos.add(_INITIAL_PREVIOUS_VALUE);
        }
    }

    protected void _drawBar(Graphics graphics, int dataset, long xpos,
            long ypos, boolean clip) {
        if (clip) {
            if (ypos < _uly) {
                ypos = _uly;
            }

            if (ypos > _lry) {
                ypos = _lry;
            }
        }

        if (ypos <= _lry && xpos <= _lrx && xpos >= _ulx) {
            int barlx = (int) (xpos - _barWidth * _xscale / 2 + dataset
                    * _barOffset * _xscale);

            int barrx = (int) (barlx + _barWidth * _xscale);

            if (barlx < _ulx) {
                barlx = _ulx;
            }

            if (barrx > _lrx) {
                barrx = _lrx;
            }

            if (barlx >= barrx) {
                barrx = barlx + 1;
            }

            long zeroypos = _lry - (long) ((0 - _yMin) * _yscale);

            if (_lry < zeroypos) {
                zeroypos = _lry;
            }

            if (_uly > zeroypos) {
                zeroypos = _uly;
            }

            if (_yMin >= 0 || ypos <= zeroypos) {
                graphics.fillRect(barlx, (int) ypos, barrx - barlx,
                        (int) (zeroypos - ypos));
            } else {
                graphics.fillRect(barlx, (int) zeroypos, barrx - barlx,
                        (int) (ypos - zeroypos));
            }
        }
    }

    protected void _drawErrorBar(Graphics graphics, int dataset, long xpos,
            long yLowEBPos, long yHighEBPos, boolean clip) {
        _drawLine(graphics, dataset, xpos - _ERRORBAR_LEG_LENGTH, yHighEBPos,
                xpos + _ERRORBAR_LEG_LENGTH, yHighEBPos, clip);
        _drawLine(graphics, dataset, xpos, yLowEBPos, xpos, yHighEBPos, clip);
        _drawLine(graphics, dataset, xpos - _ERRORBAR_LEG_LENGTH, yLowEBPos,
                xpos + _ERRORBAR_LEG_LENGTH, yLowEBPos, clip);
    }

    protected void _drawImpulse(Graphics graphics, long xpos, long ypos,
            boolean clip) {
        if (clip) {
            if (ypos < _uly) {
                ypos = _uly;
            }

            if (ypos > _lry) {
                ypos = _lry;
            }
        }

        if (ypos <= _lry && xpos <= _lrx && xpos >= _ulx) {
            double zeroypos = _lry - (long) ((0 - _yMin) * _yscale);

            if (_lry < zeroypos) {
                zeroypos = _lry;
            }

            if (_uly > zeroypos) {
                zeroypos = _uly;
            }

            _setWidth(graphics, 1f);
            graphics.drawLine((int) xpos, (int) ypos, (int) xpos,
                    (int) zeroypos);
        }
    }

    protected void _drawLine(Graphics graphics, int dataset, long startx,
            long starty, long endx, long endy, boolean clip) {
        _drawLine(graphics, dataset, startx, starty, endx, endy, clip, 1f);
    }

    protected void _drawLine(Graphics graphics, int dataset, long startx,
            long starty, long endx, long endy, boolean clip, float width) {
        _setWidth(graphics, width);

        Format format = _formats.get(dataset);
        Stroke previousStroke = null;
        if (!format.lineStyleUseDefault && graphics instanceof Graphics2D) {
            previousStroke = ((Graphics2D) graphics).getStroke();
            ((Graphics2D) graphics).setStroke(format.lineStroke);
        }

        if (clip) {
            if (!(endx <= _ulx && startx <= _ulx || endx >= _lrx
                    && startx >= _lrx || endy <= _uly && starty <= _uly || endy >= _lry
                    && starty >= _lry)) {
                if (startx != endx) {
                    if (endx < _ulx) {
                        endy = (int) (endy + (starty - endy) * (_ulx - endx)
                                / (startx - endx));
                        endx = _ulx;
                    } else if (endx > _lrx) {
                        endy = (int) (endy + (starty - endy) * (_lrx - endx)
                                / (startx - endx));
                        endx = _lrx;
                    }
                }

                if (starty != endy) {
                    if (endy < _uly) {
                        endx = (int) (endx + (startx - endx) * (_uly - endy)
                                / (starty - endy));
                        endy = _uly;
                    } else if (endy > _lry) {
                        endx = (int) (endx + (startx - endx) * (_lry - endy)
                                / (starty - endy));
                        endy = _lry;
                    }
                }

                if (startx != endx) {
                    if (startx < _ulx) {
                        starty = (int) (starty + (endy - starty)
                                * (_ulx - startx) / (endx - startx));
                        startx = _ulx;
                    } else if (startx > _lrx) {
                        starty = (int) (starty + (endy - starty)
                                * (_lrx - startx) / (endx - startx));
                        startx = _lrx;
                    }
                }

                if (starty != endy) {
                    if (starty < _uly) {
                        startx = (int) (startx + (endx - startx)
                                * (_uly - starty) / (endy - starty));
                        starty = _uly;
                    } else if (starty > _lry) {
                        startx = (int) (startx + (endx - startx)
                                * (_lry - starty) / (endy - starty));
                        starty = _lry;
                    }
                }
            }

            if (endx >= _ulx && endx <= _lrx && endy >= _uly && endy <= _lry
                    && startx >= _ulx && startx <= _lrx && starty >= _uly
                    && starty <= _lry) {
                graphics.drawLine((int) startx, (int) starty, (int) endx,
                        (int) endy);
            }
        } else {
            graphics.drawLine((int) startx, (int) starty, (int) endx,
                    (int) endy);
        }
        if (previousStroke != null) {
            ((Graphics2D) graphics).setStroke(previousStroke);
        }
    }

    @Override
    protected synchronized void _drawPlot(Graphics graphics,
            boolean clearfirst, Rectangle drawRectangle) {
        if (_graphics == null) {
            _graphics = graphics;
        } else if (graphics != _graphics) {
            _graphics = graphics;
            _prevxpos.clear();
            _prevypos.clear();
            _prevErasedxpos.clear();
            _prevErasedypos.clear();
            _lastPointWithExtraDot.clear();
            for (int dataset = 0; dataset < _points.size(); dataset++) {
                _prevxpos.add(_INITIAL_PREVIOUS_VALUE);
                _prevypos.add(_INITIAL_PREVIOUS_VALUE);
                _prevErasedxpos.add(_INITIAL_PREVIOUS_VALUE);
                _prevErasedypos.add(_INITIAL_PREVIOUS_VALUE);
                _lastPointWithExtraDot.put(dataset, null);
            }
        }

        super._drawPlot(graphics, clearfirst, drawRectangle);

        _dividePointsIntoBins();

        for (int dataset = _bins.size() - 1; dataset >= 0; dataset--) {
            ArrayList<Bin> data = _bins.get(dataset);

            int numberOfBins = data.size();

            for (int binnum = 0; binnum < numberOfBins; binnum++) {
                _drawBin(graphics, dataset, binnum);
            }

            if (_markDisconnections && _marks == 0 && numberOfBins > 0) {
                Bin bin = data.get(numberOfBins - 1);

                boolean connectedFlag = getConnected();
                ArrayList<PlotPoint> points = _points.get(dataset);

                int currentPointPosition = points.size() - 1;
                PlotPoint lastPoint = points.get(currentPointPosition);
                if (connectedFlag && lastPoint.connected) {
                    _setColorForDrawing(graphics, dataset, false);
                    long xpos = bin.xpos;
                    long ypos = _lry - (long) ((lastPoint.y - _yMin) * _yscale);
                    _drawPoint(graphics, dataset, xpos, ypos, true, 2 /*dots*/);
                    _resetColorForDrawing(graphics, false);

                    _lastPointWithExtraDot.put(dataset, lastPoint);
                }
            }
        }

        _showing = true;
    }

    @Override
    protected void _drawPoint(Graphics graphics, int dataset, long xpos,
            long ypos, boolean clip) {
        Format fmt = _formats.get(dataset);
        int marks = _marks;

        if (!fmt.marksUseDefault) {
            marks = fmt.marks;
        }
        _drawPoint(graphics, dataset, xpos, ypos, clip, marks);
    }

    @Override
    protected String _exportLatexPlotData() {
        StringBuilder result = new StringBuilder();
        Formatter formatter = null;
        try {
            formatter = new Formatter(result, Locale.US);

            for (int i = 0; i < _points.size(); i++) {
                result.append("\\pscurve[showpoints=true]{-}");
                ArrayList<PlotPoint> pts = _points.get(i);
                for (int pointnum = 0; pointnum < pts.size(); pointnum++) {
                    PlotPoint pt = pts.get(pointnum);
                    if (!pt.connected) {
                    }
                    if (pt.errorBar) {
                    } else {
                        result.append("(");
                        formatter.format("%f", pt.x);
                        result.append(",");
                        formatter.format("%f", pt.y);
                        result.append(")");
                    }
                }
                result.append("\n");
            }
        } finally {
            if (formatter != null) {
                formatter.close();
            }
        }
        return result.toString();
    }

    @Override
    protected boolean _parseLine(String line) {
        boolean connected = false;

        if (_isConnected(_currentdataset)) {
            connected = true;
        }

        if (super._parseLine(line)) {
            return true;
        } else {
            String lcLine = line.toLowerCase(Locale.getDefault());

            if (lcLine.startsWith("linestyle:")) {
                String style = line.substring(10).trim();
                setLineStyle(style, _currentdataset);
                return true;
            } else if (lcLine.startsWith("marks:")) {
                String style = line.substring(6).trim();

                if (_sawFirstDataSet) {
                    setMarksStyle(style, _currentdataset);
                } else {
                    setMarksStyle(style);
                }

                return true;
            } else if (lcLine.startsWith("numsets:")) {
                return true;
            } else if (lcLine.startsWith("reusedatasets:")) {
                if (lcLine.indexOf("off", 16) >= 0) {
                    setReuseDatasets(false);
                } else {
                    setReuseDatasets(true);
                }

                return true;
            } else if (lcLine.startsWith("dataset:")) {
                if (_reuseDatasets && lcLine.length() > 0) {
                    String tlegend = line.substring(8).trim();
                    _currentdataset = -1;

                    int i;

                    for (i = 0; i <= _maxDataset; i++) {
                        if (getLegend(i).compareTo(tlegend) == 0) {
                            _currentdataset = i;
                        }
                    }

                    if (_currentdataset != -1) {
                        return true;
                    } else {
                        _currentdataset = _maxDataset;
                    }
                }

                _firstInSet = true;
                _sawFirstDataSet = true;
                _currentdataset++;

                if (lcLine.length() > 0) {
                    String legend = line.substring(8).trim();

                    if (legend != null && legend.length() > 0) {
                        addLegend(_currentdataset, legend);
                    }
                }

                _maxDataset = _currentdataset;
                return true;
            } else if (lcLine.startsWith("lines:")) {
                if (_sawFirstDataSet) {

                    for (Format format : _formats) {

                        if (format.connectedUseDefault) {
                            format.connectedUseDefault = false;
                            format.connected = _connected;
                        }
                    }
                }

                if (lcLine.indexOf("off", 6) >= 0) {
                    setConnected(false);
                } else {
                    setConnected(true);
                }

                return true;
            } else if (lcLine.startsWith("impulses:")) {
                if (_sawFirstDataSet) {
                    if (lcLine.indexOf("off", 9) >= 0) {
                        setImpulses(false, _currentdataset);
                    } else {
                        setImpulses(true, _currentdataset);
                    }
                } else {
                    if (lcLine.indexOf("off", 9) >= 0) {
                        setImpulses(false);
                    } else {
                        setImpulses(true);
                    }
                }

                return true;
            } else if (lcLine.startsWith("bars:")) {
                if (lcLine.indexOf("off", 5) >= 0) {
                    setBars(false);
                } else {
                    setBars(true);

                    int comma = line.indexOf(",", 5);
                    String _barWidth;
                    String baroffset = null;

                    if (comma > 0) {
                        _barWidth = line.substring(5, comma).trim();
                        baroffset = line.substring(comma + 1).trim();
                    } else {
                        _barWidth = line.substring(5).trim();
                    }

                    try {
                        double bwidth = Double.parseDouble(_barWidth);
                        double boffset = _barOffset;

                        if (baroffset != null) {
                            boffset = Double.parseDouble(baroffset);
                        }

                        setBars(bwidth, boffset);
                    } catch (NumberFormatException e) {
                    }
                }

                return true;
            } else if (line.startsWith("move:")) {
                connected = false;
                line = line.substring(5, line.length()).trim();
            } else if (line.startsWith("move")) {
                connected = false;
                line = line.substring(4, line.length()).trim();
            } else if (line.startsWith("draw:")) {
                line = line.substring(5, line.length()).trim();
            } else if (line.startsWith("draw")) {
                line = line.substring(4, line.length()).trim();
            }

            line = line.trim();

            int fieldsplit = line.indexOf(",");

            if (fieldsplit == -1) {
                fieldsplit = line.indexOf(" ");
            }

            if (fieldsplit == -1) {
                fieldsplit = line.indexOf("\t");
            }

            if (fieldsplit > 0) {
                String x = line.substring(0, fieldsplit).trim();
                String y = line.substring(fieldsplit + 1).trim();

                int fieldsplit2 = y.indexOf(",");

                if (fieldsplit2 == -1) {
                    fieldsplit2 = y.indexOf(" ");
                }

                if (fieldsplit2 == -1) {
                    fieldsplit2 = y.indexOf("\t");
                }

                if (fieldsplit2 > 0) {
                    line = y.substring(fieldsplit2 + 1).trim();
                    y = y.substring(0, fieldsplit2).trim();
                }

                try {
                    double xpt = Double.parseDouble(x);
                    double ypt = Double.parseDouble(y);

                    if (fieldsplit2 > 0) {
                        int fieldsplit3 = line.indexOf(",");

                        if (fieldsplit3 == -1) {
                            fieldsplit3 = line.indexOf(" ");
                        }

                        if (fieldsplit3 > 0) {
                            String yl = line.substring(0, fieldsplit3).trim();
                            String yh = line.substring(fieldsplit3 + 1).trim();
                            double yLowEB = Double.parseDouble(yl);
                            double yHighEB = Double.parseDouble(yh);
                            connected = _addLegendIfNecessary(connected);
                            addPointWithErrorBars(_currentdataset, xpt, ypt,
                                    yLowEB, yHighEB, connected);
                            return true;
                        } else {
                            connected = _addLegendIfNecessary(connected);
                            addPoint(_currentdataset, xpt, ypt, connected);
                            return true;
                        }
                    } else {
                        connected = _addLegendIfNecessary(connected);
                        addPoint(_currentdataset, xpt, ypt, connected);
                        return true;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

        return false;
    }

    @Override
    protected void _resetScheduledTasks() {
        Runnable redraw = new RunnableExceptionCatcher(new Runnable() {
            @Override
            public void run() {
                _scheduledBinsToAdd.clear();
                _scheduledBinsToErase.clear();
            }
        });
        synchronized (this) {
            deferIfNecessary(redraw);
        }
    }

    @Override
    protected void _scheduledRedraw() {
        if (_needPlotRefill || _needBinRedraw) {
            Runnable redraw = new RunnableExceptionCatcher(() -> {
                ArrayList<Integer> scheduledBinsToAdd = new ArrayList<>();
                for (int i = 0; i < _scheduledBinsToAdd.size(); ++i) {
                    scheduledBinsToAdd.add(_scheduledBinsToAdd.get(i));
                    _scheduledBinsToAdd.set(i, 0);
                }
                ArrayList<Integer> scheduledBinsToErase = new ArrayList<>();
                for (int i = 0; i < _scheduledBinsToErase.size(); ++i) {
                    scheduledBinsToErase.add(_scheduledBinsToErase.get(i));
                    _scheduledBinsToErase.set(i, 0);
                }
                _needBinRedraw = false;
                if (_needPlotRefill) {
                    fillPlot();
                    _needPlotRefill = false;
                } else {
                    Graphics graphics = getGraphics();
                    if (graphics != null) {
                        {
                            int nbrOfDataSets = _scheduledBinsToAdd.size();
                            for (int i = 0; i < nbrOfDataSets; ++i) {
                                int nbrOfBins = _bins.get(i).size();
                                int nbrOfBinsToAdd = scheduledBinsToAdd
                                        .get(i);
                                for (int binIndex = nbrOfBins
                                        - nbrOfBinsToAdd; binIndex < nbrOfBins; ++binIndex) {
                                    assert binIndex >= 0;
                                    _drawBin(graphics, i, binIndex);
                                }
                            }
                        }
                        {
                            int nbrOfDataSets = _scheduledBinsToErase
                                    .size();
                            for (int i = 0; i < nbrOfDataSets; ++i) {
                                int nbrOfBinsToErase = scheduledBinsToErase
                                        .get(i);
                                for (int binIndex = 0; binIndex < nbrOfBinsToErase; ++binIndex) {
                                    _eraseFirstBin(i);
                                }
                            }
                        }
                    }
                }
            });
            synchronized (this) {
                deferIfNecessary(redraw);
            }
        }
    }

    protected void _setWidth(Graphics graphics, float width) {
        _width = width;

        if (graphics instanceof Graphics2D) {
            if (width == 1f) {
                ((Graphics2D) graphics).setStroke(_LINE_STROKE1);
            } else if (width == 2f) {
                ((Graphics2D) graphics).setStroke(_LINE_STROKE2);
            } else {
                ((Graphics2D) graphics).setStroke(new BasicStroke(width,
                        BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            }
        }
    }

    @Override
    @Deprecated
    protected void _writeOldSyntax(PrintWriter output) {
        super._writeOldSyntax(output);

        if (_reuseDatasets) {
            output.println("ReuseDatasets: on");
        }

        if (!_connected) {
            output.println("Lines: off");
        }

        if (_bars) {
            output.println("Bars: " + _barWidth + ", " + _barOffset);
        }

        if (_impulses) {
            output.println("Impulses: on");
        }

        switch (_marks) {
        case 0:
            break;
        case 1:
            output.println("Marks: points");
            break;

        case 2:
            output.println("Marks: dots");
            break;

        case 3:
            output.println("Marks: various");
            break;

        case 4:
            output.println("Marks: bigdots");
            break;

        case 5:
            output.println("Marks: pixelss");
            break;
        default:
            throw new RuntimeException("Internal Error.  Mark " + "style "
                    + _marks + " not supported.");
        }

        for (int dataset = 0; dataset < _points.size(); dataset++) {
            String legend = getLegend(dataset);

            if (legend != null) {
                output.println("DataSet: " + getLegend(dataset));
            } else {
                output.println("DataSet:");
            }

            Format fmt = _formats.get(dataset);

            if (!fmt.impulsesUseDefault) {
                if (fmt.impulses) {
                    output.println("Impulses: on");
                } else {
                    output.println("Impulses: off");
                }
            }

            if (!fmt.lineStyleUseDefault) {
                output.println("lineStyle: " + fmt.lineStyle);
            }

            if (!fmt.marksUseDefault) {
                switch (fmt.marks) {
                case 0:
                    output.println("Marks: none");
                    break;

                case 1:
                    output.println("Marks: points");
                    break;

                case 2:
                    output.println("Marks: dots");
                    break;

                case 3:
                    output.println("Marks: various");
                    break;

                case 4:
                    output.println("Marks: pixels");
                    break;
                }
            }

            ArrayList<PlotPoint> pts = _points.get(dataset);

            for (int pointnum = 0; pointnum < pts.size(); pointnum++) {
                PlotPoint pt = pts.get(pointnum);

                if (!pt.connected) {
                    output.print("move: ");
                }

                if (pt.errorBar) {
                    output.println(pt.x + ", " + pt.y + ", " + pt.yLowEB + ", "
                            + pt.yHighEB);
                } else {
                    output.println(pt.x + ", " + pt.y);
                }
            }
        }
    }

    protected int _currentdataset = -1;

    protected volatile int _marks;

    protected ArrayList<ArrayList<PlotPoint>> _points = new ArrayList<ArrayList<PlotPoint>>();

    private boolean _addLegendIfNecessary(boolean connected) {
        if ((!_sawFirstDataSet || _currentdataset < 0) && !_reuseDatasets) {
            _sawFirstDataSet = true;
            _currentdataset++;
        }

        if (!_sawFirstDataSet && getLegend(_currentdataset) == null) {
            _firstInSet = true;
            _sawFirstDataSet = true;
            addLegend(_currentdataset, "Set " + _currentdataset);
        }

        if (_firstInSet && !_reuseDatasets) {
            connected = false;
            _firstInSet = false;
        }

        return connected;
    }

    private void _addPoint(int dataset, double x, double y, double yLowEB,
            double yHighEB, boolean connected, boolean errorBar) {
        _plotImage = null;

        _checkDatasetIndex(dataset);

        if (_xlog) {
            if (x <= 0.0) {
                System.err.println("Can't plot non-positive X values "
                        + "when the logarithmic X axis value is specified: "
                        + x);
                return;
            }

            x = Math.log(x) * _LOG10SCALE;
        }

        if (_ylog) {
            if (y <= 0.0) {
                System.err.println("Can't plot non-positive Y values "
                        + "when the logarithmic Y axis value is specified: "
                        + y);
                return;
            }

            y = Math.log(y) * _LOG10SCALE;

            if (errorBar) {
                if (yLowEB <= 0.0 || yHighEB <= 0.0) {
                    System.err
                    .println("Can't plot non-positive Y values "
                            + "when the logarithmic Y axis value is specified: "
                            + y);
                    return;
                }

                yLowEB = Math.log(yLowEB) * _LOG10SCALE;
                yHighEB = Math.log(yHighEB) * _LOG10SCALE;
            }
        }

        ArrayList<Bin> bins = _bins.get(dataset);
        ArrayList<PlotPoint> points = _points.get(dataset);

        if (_xPersistence > 0.0) {
            int numToDelete = 0;
            int nbrOfBins = bins.size();

            while (numToDelete < nbrOfBins) {
                Bin old = bins.get(numToDelete);

                if (x - points.get(old.firstPointIndex()).originalx <= _xPersistence) {
                    break;
                }

                numToDelete++;
            }

            numToDelete = Math.min(numToDelete, nbrOfBins - 1);

            if (!_timedRepaint()) {
                for (int i = 0; i < numToDelete; i++) {
                    _eraseFirstBin(dataset);
                }
            } else {
                _scheduleBinRedrawRemove(dataset, numToDelete);
            }
        }

        int size = points.size();

        PlotPoint pt = new PlotPoint();

        pt.originalx = x;

        if (_wrap) {
            double width = _wrapHigh - _wrapLow;

            if (x < _wrapLow) {
                x += width * Math.floor(1.0 + (_wrapLow - x) / width);
            } else if (x > _wrapHigh) {
                x -= width * Math.floor(1.0 + (x - _wrapHigh) / width);

                if (Math.abs(x - _wrapLow) < 0.00001) {
                    x = _wrapHigh;
                }
            }
        }

        boolean needPlotRefill = false;

        if (x < _xBottom) {
            if (_automaticRescale() && _xTop != -Double.MAX_VALUE
                    && _xBottom != Double.MAX_VALUE) {
                needPlotRefill = true;
                _xBottom = x - (_xTop - _xBottom);
            } else {
                _xBottom = x;
            }
        }

        if (x > _xTop) {
            if (_automaticRescale() && _xTop != -Double.MAX_VALUE
                    && _xBottom != Double.MAX_VALUE) {
                needPlotRefill = true;
                _xTop = x + _xTop - _xBottom;
            } else {
                _xTop = x;
            }
        }

        if (y < _yBottom) {
            if (_automaticRescale() && _yTop != -Double.MAX_VALUE
                    && _yBottom != Double.MAX_VALUE) {
                needPlotRefill = true;
                _yBottom = y - (_yTop - _yBottom);
            } else {
                _yBottom = y;
            }
        }

        if (y > _yTop) {
            if (_automaticRescale() && _yTop != -Double.MAX_VALUE
                    && _yBottom != Double.MAX_VALUE) {
                needPlotRefill = true;
                _yTop = y + _yTop - _yBottom;
            } else {
                _yTop = y;
            }
        }

        pt.x = x;
        pt.y = y;
        pt.connected = connected && _isConnected(dataset);

        if (errorBar) {
            if (yLowEB < _yBottom) {
                _yBottom = yLowEB;
            }

            if (yLowEB > _yTop) {
                _yTop = yLowEB;
            }

            if (yHighEB < _yBottom) {
                _yBottom = yHighEB;
            }

            if (yHighEB > _yTop) {
                _yTop = yHighEB;
            }

            pt.yLowEB = yLowEB;
            pt.yHighEB = yHighEB;
            pt.errorBar = true;
        }

        if (size == 0) {
            pt.connected = false;
        } else if (_wrap) {
            PlotPoint old = points.get(size - 1);

            if (old.x > x) {
                pt.connected = false;
            }
        }

        points.add(pt);

        int nbrOfBins = dataset < _bins.size() ? _bins.get(dataset).size() : 0;
        _addPointToBin(dataset, pt, size);

        boolean binAdded = _bins.get(dataset).size() != nbrOfBins;
        if (_pointsPersistence > 0) {
            if (size > _pointsPersistence && bins.size() > 2) {
                if (!_timedRepaint()) {
                    _eraseFirstBin(dataset);
                } else {
                    _scheduleBinRedrawRemove(dataset, 1);
                }
            }
        }

        Graphics graphics = getGraphics();

        if (_showing && graphics != null) {
            if ((_pointsPersistence > 0 || _xPersistence > 0.0)
                    && isDoubleBuffered()) {
                setDoubleBuffered(false);

                Component parent = getParent();

                while (parent != null) {
                    if (parent instanceof JComponent) {
                        ((JComponent) parent).setDoubleBuffered(false);
                    }
                    parent = parent.getParent();
                }
            }

            assert _bins.get(dataset).size() > 0;

            if (!_timedRepaint()) {
                _drawBin(graphics, dataset, _bins.get(dataset).size() - 1);
            } else {
                if (needPlotRefill) {
                    _needPlotRefill = true;
                } else {
                    _scheduleBinRedrawAdd(dataset, binAdded);
                }
            }
        }

        if (_wrap && Math.abs(x - _wrapHigh) < 0.00001) {
            _addPoint(dataset, _wrapLow, y, yLowEB, yHighEB, false, errorBar);
        }
    }

    private void _addPointToBin(int dataset, PlotPoint point, int pointIndex) {
        ArrayList<Bin> bins = _bins.get(dataset);
        long xpos = _ulx + (long) ((point.x - _xMin) * _xscale);
        long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
        int nbrOfBins = bins.size();

        Bin lastBin = nbrOfBins > 0 ? bins.get(nbrOfBins - 1) : null;

        if (nbrOfBins == 0 || lastBin.xpos != xpos) {
            lastBin = new Bin(xpos, dataset);
            bins.add(lastBin);
        }
        lastBin.addPoint(point, pointIndex, ypos);
    }

    private void _clear(boolean format) {
        _plotImage = null;
        super.clear(format);
        _currentdataset = -1;
        _points.clear();
        for (ArrayList<Bin> data : _bins) {
            data.clear();
        }
        _bins.clear();
        _prevxpos.clear();
        _prevypos.clear();
        _prevErasedxpos.clear();
        _prevErasedypos.clear();
        _maxDataset = -1;
        _firstInSet = true;
        _sawFirstDataSet = false;
        _xyInvalid = true;
        _resetScheduledTasks();

        if (format) {
            _showing = false;
            _formats.clear();
            _marks = 0;
            _pointsPersistence = 0;
            _xPersistence = 0;
            _bars = false;
            _barWidth = 0.5;
            _barOffset = 0.05;
            _connected = true;
            _impulses = false;
            _reuseDatasets = false;
        }
    }

    private void _clear(int dataset) {
        _plotImage = null;
        _checkDatasetIndex(dataset);
        _xyInvalid = true;

        ArrayList<PlotPoint> points = _points.get(dataset);

        points.clear();

        _points.set(dataset, points);
        _bins.get(dataset).clear();

        _lastPointWithExtraDot.clear();
        repaint();
    }

    private void _dividePointsIntoBins() {
        for (int i = 0; i < _scheduledBinsToAdd.size(); ++i) {
            _scheduledBinsToAdd.set(i, 0);
        }
        for (int i = 0; i < _scheduledBinsToErase.size(); ++i) {
            _scheduledBinsToErase.set(i, 0);
        }
        _needBinRedraw = false;

        _bins.clear();
        _pointInBinOffset.clear();
        int nbrOfDataSets = _points.size();
        for (int i = 0; i < nbrOfDataSets; ++i) {
            _bins.add(new ArrayList<Bin>());
            _pointInBinOffset.add(0);
        }

        for (int dataset = 0; dataset < nbrOfDataSets; ++dataset) {
            ArrayList<PlotPoint> points = _points.get(dataset);
            int numberOfPoints = points.size();
            for (int pointIndex = 0; pointIndex < numberOfPoints; ++pointIndex) {
                _addPointToBin(dataset, points.get(pointIndex), pointIndex);
            }
        }
    }

    private void _drawBin(Graphics graphics, int dataset, int binIndex) {

        _setColorForDrawing(graphics, dataset, false);

        if (_lineStyles) {
            int lineStyle = dataset % _LINE_STYLES_ARRAY.length;
            setLineStyle(_LINE_STYLES_ARRAY[lineStyle], dataset);
        }

        assert dataset < _bins.size();
        ArrayList<Bin> bins = _bins.get(dataset);
        assert binIndex < bins.size();
        Bin bin = bins.get(binIndex);
        long xpos = bin.xpos;

        if (!bin.needReplot()) {
            return;
        }

        boolean connectedFlag = getConnected();

        int startPosition = bin.nextPointToPlot();
        int endPosition = bin.afterLastPointIndex();

        ArrayList<PlotPoint> points = _points.get(dataset);

        int marks = _marks;

        Format fmt = _formats.get(dataset);

        if (!fmt.marksUseDefault) {
            marks = fmt.marks;
        }

        if (_markDisconnections && marks == 0 && endPosition > startPosition
                && startPosition > 0) {
            PlotPoint previousPoint = points.get(startPosition - 1);
            if (!(connectedFlag && points.get(startPosition).connected)) {

                if (connectedFlag && previousPoint.connected) {
                    if (_lastPointWithExtraDot.get(dataset) != previousPoint) {
                        long prevypos = _prevypos.get(dataset);
                        long prevxpos = _prevxpos.get(dataset);
                        _drawPoint(graphics, dataset, prevxpos, prevypos, true,
                                2 /*dots*/);
                    } else {
                        _lastPointWithExtraDot.put(dataset, null);
                    }
                }
            } else {
                if (_lastPointWithExtraDot.get(dataset) == previousPoint) {
                    long prevypos = _prevypos.get(dataset);
                    long prevxpos = _prevxpos.get(dataset);

                    _setColorForDrawing(graphics, dataset, true);
                    _drawPoint(graphics, dataset, prevxpos, prevypos, true, 2 /*dots*/);
                    _resetColorForDrawing(graphics, true);
                    _setColorForDrawing(graphics, dataset, false);
                }
            }
        }

        if (connectedFlag && bin.needConnectionWithPreviousBin()) {
            Bin previousBin = bins.get(binIndex - 1);
            _drawLine(graphics, dataset, xpos, bin.firstYPos(),
                    previousBin.xpos, previousBin.lastYPos(), true,
                    _DEFAULT_WIDTH);
        }

        if (connectedFlag && bin.isConnected() && bin.rangeChanged()
                && bin.minYPos() != bin.maxYPos()) {
            _drawLine(graphics, dataset, xpos, bin.minYPos(), xpos,
                    bin.maxYPos(), true, _DEFAULT_WIDTH);
        }

        if (fmt.impulsesUseDefault && _impulses || !fmt.impulsesUseDefault
                && fmt.impulses) {
            long prevypos = _prevypos.get(dataset);
            long prevxpos = _prevxpos.get(dataset);

            for (int i = startPosition; i < endPosition; ++i) {
                PlotPoint point = points.get(i);
                long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                if (prevypos != ypos || prevxpos != xpos) {
                    _drawImpulse(graphics, xpos, ypos, true);
                    prevypos = ypos;
                    prevxpos = xpos;
                }
            }
        }

        {
            long prevypos = _prevypos.get(dataset);
            long prevxpos = _prevxpos.get(dataset);

            for (int i = startPosition; i < endPosition; ++i) {
                PlotPoint point = points.get(i);

                if (marks != 0 || _markDisconnections
                        && !(connectedFlag && point.connected)) {
                    long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                    if (prevypos != ypos || prevxpos != xpos) {
                        int updatedMarks = marks;
                        if (!(connectedFlag && point.connected) && marks == 0) {
                            updatedMarks = 2;
                        }
                        _drawPoint(graphics, dataset, xpos, ypos, true,
                                updatedMarks);
                        prevypos = ypos;
                        prevxpos = xpos;
                    }
                }

            }
        }
        if (_bars) {
            long prevypos = _prevypos.get(dataset);
            long prevxpos = _prevxpos.get(dataset);
            for (int i = startPosition; i < endPosition; ++i) {
                PlotPoint point = points.get(i);
                long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                if (prevypos != ypos || prevxpos != xpos) {
                    _drawBar(graphics, dataset, xpos, ypos, true);
                    prevypos = ypos;
                    prevxpos = xpos;
                }
            }
        }

        if (bin.errorBar()) {
            long prevypos = _prevypos.get(dataset);
            long prevxpos = _prevxpos.get(dataset);
            for (int i = startPosition; i < endPosition; ++i) {
                PlotPoint point = points.get(i);
                if (point.errorBar) {
                    long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                    if (prevypos != ypos || prevxpos != xpos) {
                        _drawErrorBar(
                                graphics,
                                dataset,
                                xpos,
                                _lry
                                - (long) ((point.yLowEB - _yMin) * _yscale),
                                _lry
                                - (long) ((point.yHighEB - _yMin) * _yscale),
                                true);
                        prevypos = ypos;
                        prevxpos = xpos;

                    }
                }
            }
        }

        _prevxpos.set(dataset, xpos);
        _prevypos.set(dataset, bin.lastYPos());

        bin.resetDisplayStateAfterPlot();

        _resetColorForDrawing(graphics, false);
    }

    private void _drawPoint(Graphics graphics, int dataset, long xpos,
            long ypos, boolean clip, final int marks) {

        boolean pointinside = ypos <= _lry && ypos >= _uly && xpos <= _lrx
                && xpos >= _ulx;

        if (!clip || pointinside) {
            int xposi = (int) xpos;
            int yposi = (int) ypos;

            if (!pointinside && marks != 3 && _isConnected(dataset)
                    && (graphics instanceof EPSGraphics || !_usecolor)) {
                _drawLine(graphics, dataset, xposi - 6, yposi, xposi + 6,
                        yposi, false, _width);
            } else {
                switch (marks) {
                case 0:
                    graphics.fillRect(xposi - 6, yposi - 6, 6, 6);
                    break;
                case 1:
                    graphics.fillOval(xposi - 1, yposi - 1, 3, 3);
                    break;
                case 2:
                    graphics.fillOval(xposi - _radius, yposi - _radius,
                            _diameter, _diameter);
                    break;
                case 3:
                    int[] xpoints;
                    int[] ypoints;
                    int mark = dataset % _MAX_MARKS;
                    switch (mark) {
                    case 0:
                        graphics.fillOval(xposi - _radius, yposi - _radius,
                                _diameter, _diameter);
                        break;
                    case 1:
                        graphics.drawLine(xposi - _radius, yposi - _radius,
                                xposi + _radius, yposi + _radius);
                        graphics.drawLine(xposi + _radius, yposi - _radius,
                                xposi - _radius, yposi + _radius);
                        break;
                    case 2:
                        graphics.drawRect(xposi - _radius, yposi - _radius,
                                _diameter, _diameter);
                        break;
                    case 3:
                        xpoints = new int[4];
                        ypoints = new int[4];
                        xpoints[0] = xposi;
                        ypoints[0] = yposi - _radius;
                        xpoints[1] = xposi + _radius;
                        ypoints[1] = yposi + _radius;
                        xpoints[2] = xposi - _radius;
                        ypoints[2] = yposi + _radius;
                        xpoints[3] = xposi;
                        ypoints[3] = yposi - _radius;
                        graphics.fillPolygon(xpoints, ypoints, 4);
                        break;
                    case 4:
                        xpoints = new int[5];
                        ypoints = new int[5];
                        xpoints[0] = xposi;
                        ypoints[0] = yposi - _radius;
                        xpoints[1] = xposi + _radius;
                        ypoints[1] = yposi;
                        xpoints[2] = xposi;
                        ypoints[2] = yposi + _radius;
                        xpoints[3] = xposi - _radius;
                        ypoints[3] = yposi;
                        xpoints[4] = xposi;
                        ypoints[4] = yposi - _radius;
                        graphics.drawPolygon(xpoints, ypoints, 5);
                        break;
                    case 5:
                        graphics.drawOval(xposi - _radius, yposi - _radius,
                                _diameter, _diameter);
                        break;
                    case 6:
                        graphics.drawLine(xposi, yposi - _radius, xposi, yposi
                                + _radius);
                        graphics.drawLine(xposi - _radius, yposi, xposi
                                + _radius, yposi);
                        break;
                    case 7:
                        graphics.fillRect(xposi - _radius, yposi - _radius,
                                _diameter, _diameter);
                        break;
                    case 8:
                        xpoints = new int[4];
                        ypoints = new int[4];
                        xpoints[0] = xposi;
                        ypoints[0] = yposi - _radius;
                        xpoints[1] = xposi + _radius;
                        ypoints[1] = yposi + _radius;
                        xpoints[2] = xposi - _radius;
                        ypoints[2] = yposi + _radius;
                        xpoints[3] = xposi;
                        ypoints[3] = yposi - _radius;
                        graphics.drawPolygon(xpoints, ypoints, 4);
                        break;
                    case 9:
                        xpoints = new int[5];
                        ypoints = new int[5];
                        xpoints[0] = xposi;
                        ypoints[0] = yposi - _radius;
                        xpoints[1] = xposi + _radius;
                        ypoints[1] = yposi;
                        xpoints[2] = xposi;
                        ypoints[2] = yposi + _radius;
                        xpoints[3] = xposi - _radius;
                        ypoints[3] = yposi;
                        xpoints[4] = xposi;
                        ypoints[4] = yposi - _radius;
                        graphics.fillPolygon(xpoints, ypoints, 5);
                        break;
                    default:
                        throw new RuntimeException("Internal Error.  Mark "
                                + "style " + mark + " not supported.");
                    }
                    break;
                case 4:
                    if (graphics instanceof Graphics2D) {
                        Object obj = ((Graphics2D) graphics)
                                .getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                        ((Graphics2D) graphics).setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics.fillOval(xposi - 4, yposi - 4, 8, 8);
                        ((Graphics2D) graphics).setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, obj);
                    } else {
                        graphics.fillOval(xposi - 4, yposi - 4, 8, 8);
                    }
                    break;
                case 5:
                    graphics.fillRect(xposi, yposi, 1, 1);
                    break;
                default:
                    throw new RuntimeException("Internal Error.  Mark "
                            + "style " + marks + " not supported.");
                }
            }
        }
    }

    private void _eraseFirstBin(int dataset) {
        _plotImage = null;

        _checkDatasetIndex(dataset);

        Graphics graphics = getGraphics();

        ArrayList<PlotPoint> points = _points.get(dataset);
        ArrayList<Bin> bins = _bins.get(dataset);
        Bin bin = bins.get(0);
        int nbrOfBins = bins.size();
        assert nbrOfBins > 0;

        long xpos = bin.xpos;
        int startPosition = bin.firstPointIndex();
        assert startPosition >= 0;
        int endPosition = bin.afterLastPointIndex();
        assert endPosition > startPosition;

        if (_showing && graphics != null) {
            _setColorForDrawing(graphics, dataset, false);
            long minYPos = bin.minYPos();
            long maxYPos = bin.maxYPos();
            boolean connectedFlag = getConnected();

            if (connectedFlag && bin.isConnected() && minYPos != maxYPos) {
                _drawLine(graphics, dataset, xpos, minYPos, xpos, maxYPos,
                        true, _DEFAULT_WIDTH);
            }

            if (nbrOfBins > 1) {
                Bin nextBin = bins.get(1);
                long nextx = nextBin.xpos;

                if (connectedFlag && nextBin.isConnectedWithPreviousBin()) {
                    _drawLine(graphics, dataset, nextx, nextBin.firstYPos(),
                            xpos, bin.lastYPos(), true, 2f);
                }
            }

            Format fmt = _formats.get(dataset);

            if (fmt.impulsesUseDefault && _impulses || !fmt.impulsesUseDefault
                    && fmt.impulses) {
                long prevypos = _prevErasedypos.get(dataset);
                long prevxpos = _prevErasedxpos.get(dataset);
                for (int i = startPosition; i < endPosition; ++i) {
                    PlotPoint point = points.get(i);
                    long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                    if (prevypos != ypos || prevxpos != xpos) {
                        _drawImpulse(graphics, xpos, ypos, true);
                        prevypos = ypos;
                        prevxpos = xpos;
                    }
                }
            }
            int marks = _marks;

            if (!fmt.marksUseDefault) {
                marks = fmt.marks;
            }
            {
                long prevypos = _prevErasedypos.get(dataset);
                long prevxpos = _prevErasedxpos.get(dataset);

                for (int i = startPosition; i < endPosition; ++i) {
                    PlotPoint point = points.get(i);
                    if (marks != 0 || !(connectedFlag && point.connected)) {
                        long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                        if (prevypos != ypos || prevxpos != xpos) {
                            int updatedMarks = marks;
                            if (!(connectedFlag && point.connected)
                                    && marks == 0) {
                                updatedMarks = 2;
                            }

                            _drawPoint(graphics, dataset, xpos, ypos, true,
                                    updatedMarks);
                            prevypos = ypos;
                            prevxpos = xpos;
                        }
                    }
                }
            }

            if (_markDisconnections && marks == 0
                    && endPosition > startPosition
                    && endPosition < points.size()) {
                PlotPoint point = points.get(endPosition - 1);
                if (connectedFlag && point.connected) {

                    PlotPoint nextPoint = points.get(endPosition);
                    if (!(connectedFlag && nextPoint.connected)) {
                        long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                        _drawPoint(graphics, dataset, xpos, ypos, true, 2 /*dots*/);
                    }
                }
            }

            if (_bars) {
                long prevypos = _prevErasedypos.get(dataset);
                long prevxpos = _prevErasedxpos.get(dataset);

                for (int i = startPosition; i < endPosition; ++i) {
                    PlotPoint point = points.get(i);
                    long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                    if (prevypos != ypos || prevxpos != xpos) {
                        _drawBar(graphics, dataset, xpos, ypos, true);
                        prevypos = ypos;
                        prevxpos = xpos;
                    }
                }
            }

            if (bin.errorBar()) {
                long prevypos = _prevErasedypos.get(dataset);
                long prevxpos = _prevErasedxpos.get(dataset);

                for (int i = startPosition; i < endPosition; ++i) {
                    PlotPoint point = points.get(i);
                    if (point.errorBar) {
                        long ypos = _lry - (long) ((point.y - _yMin) * _yscale);
                        if (prevypos != ypos || prevxpos != xpos) {
                            _drawErrorBar(
                                    graphics,
                                    dataset,
                                    xpos,
                                    _lry
                                    - (long) ((point.yLowEB - _yMin) * _yscale),
                                    _lry
                                    - (long) ((point.yHighEB - _yMin) * _yscale),
                                    true);
                            prevypos = ypos;
                            prevxpos = xpos;
                        }
                    }
                }
            }

            _resetColorForDrawing(graphics, false);
        }

        if (nbrOfBins > 1) {
            Bin nextBin = bins.get(1);
            nextBin.setNotConnectedWithPreviousBin();
        }

        if (nbrOfBins == 1) {
            _prevxpos.set(dataset, _INITIAL_PREVIOUS_VALUE);
            _prevypos.set(dataset, _INITIAL_PREVIOUS_VALUE);
        }

        if (xpos == _xBottom || xpos == _xTop || bin.minYPos() == _yBottom
                || bin.maxYPos() == _yTop) {
            _xyInvalid = true;
        }

        assert startPosition == 0;
        for (int i = startPosition; i < endPosition; ++i) {
            points.remove(startPosition);
        }
        assert bin.firstPointIndex() >= 0;

        _pointInBinOffset.set(dataset,
                _pointInBinOffset.get(dataset) + bin.afterLastPointIndex()
                - bin.firstPointIndex());
        if (nbrOfBins > 1) {
            Bin nextBin = bins.get(1);
            assert nextBin.firstPointIndex() >= 0;
            assert nextBin.firstPointIndex() == 0;

        }

        _prevErasedxpos.set(dataset, xpos);
        _prevErasedypos.set(dataset, bin.lastYPos());

        bins.remove(0);
    }

    private void _erasePoint(int dataset, int index) {
        _points.get(dataset).remove(index);
        repaint();
    }

    private void _fillPlot() {
        if (_xyInvalid) {
            _xBottom = Double.MAX_VALUE;
            _xTop = -Double.MAX_VALUE;
            _yBottom = Double.MAX_VALUE;
            _yTop = -Double.MAX_VALUE;

            for (int dataset = 0; dataset < _points.size(); dataset++) {
                ArrayList<PlotPoint> points = _points.get(dataset);

                for (int index = 0; index < points.size(); index++) {
                    PlotPoint pt = points.get(index);

                    if (pt.x < _xBottom) {
                        _xBottom = pt.x;
                    }

                    if (pt.x > _xTop) {
                        _xTop = pt.x;
                    }

                    if (pt.y < _yBottom) {
                        _yBottom = pt.y;
                    }

                    if (pt.y > _yTop) {
                        _yTop = pt.y;
                    }
                }
            }
        }

        _xyInvalid = false;

        if (_bars) {
            if (_yBottom > 0.0) {
                _yBottom = 0.0;
            }

            if (_yTop < 0.0) {
                _yTop = 0.0;
            }
        }

        super.fillPlot();
    }

    private boolean _isConnected(int dataset) {
        if (dataset < 0) {
            return _connected;
        }

        _checkDatasetIndex(dataset);

        Format fmt = _formats.get(dataset);

        if (fmt.connectedUseDefault) {
            return _connected;
        } else {
            return fmt.connected;
        }
    }

    private void _resetColorForDrawing(Graphics graphics,
            boolean forceExorWithBackground) {
        graphics.setColor(_foreground);

        if (_pointsPersistence > 0 || _xPersistence > 0.0
                || forceExorWithBackground) {
            graphics.setPaintMode();
        }
    }

    private void _scheduleBinRedrawAdd(int dataset, boolean binAdded) {
        while (_scheduledBinsToAdd.size() <= dataset) {
            _scheduledBinsToAdd.add(0);
        }
        int previousCount = _scheduledBinsToAdd.get(dataset);
        if (binAdded || previousCount == 0) {
            _scheduledBinsToAdd.set(dataset, previousCount + 1);
            _needBinRedraw = true;
        }
    }

    private void _scheduleBinRedrawRemove(int dataset, int nbrOfElementsToErase) {
        while (_scheduledBinsToErase.size() <= dataset) {
            _scheduledBinsToErase.add(0);
        }
        _scheduledBinsToErase.set(
                dataset,
                Math.max(nbrOfElementsToErase,
                        _scheduledBinsToErase.get(dataset)));
        _needBinRedraw = true;
    }

    private void _setColorForDrawing(Graphics graphics, int dataset,
            boolean forceExorWithBackground) {
        if (_pointsPersistence > 0 || _xPersistence > 0.0
                || forceExorWithBackground) {
            if (_background == null) {
                graphics.setXORMode(getBackground());
            } else {
                graphics.setXORMode(_background);
            }
        }

        if (_usecolor) {
            int color = dataset % _colors.length;
            graphics.setColor(_colors[color]);
        } else {
            graphics.setColor(_foreground);
        }
    }

    private boolean _markDisconnections = false;

    private volatile double _barOffset = 0.05;

    private boolean _bars = false;

    private volatile double _barWidth = 0.5;

    private ArrayList<ArrayList<Bin>> _bins = new ArrayList<ArrayList<Bin>>();

    private boolean _connected = true;

    private int _diameter = 6;

    private static final float _DEFAULT_WIDTH = 2f;

    private static final int _ERRORBAR_LEG_LENGTH = 5;

    private boolean _firstInSet = true;

    private ArrayList<Format> _formats = new ArrayList<Format>();

    private Graphics _graphics = null;

    private boolean _impulses = false;

    private static final Long _INITIAL_PREVIOUS_VALUE = Long.MIN_VALUE;

    private HashMap<Integer, PlotPoint> _lastPointWithExtraDot = new HashMap<Integer, PlotPoint>();

    private static final BasicStroke _LINE_STROKE1 = new BasicStroke(1f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

    private static final BasicStroke _LINE_STROKE2 = new BasicStroke(2f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

    private boolean _lineStyles = false;

    private static String[] _LINE_STYLES_ARRAY = { "solid", "dotted", "dashed",
        "dotdashed", "dotdotdashed" };

    private int _maxDataset = -1;

    private static final int _MAX_MARKS = 10;

    private boolean _needBinRedraw = false;

    private boolean _needPlotRefill = false;

    private ArrayList<Integer> _pointInBinOffset = new ArrayList<Integer>();

    private int _pointsPersistence = 0;

    private ArrayList<Long> _prevxpos = new ArrayList<Long>();

    private ArrayList<Long> _prevypos = new ArrayList<Long>();

    private ArrayList<Long> _prevErasedxpos = new ArrayList<Long>();

    private ArrayList<Long> _prevErasedypos = new ArrayList<Long>();

    private int _radius = 3;

    private boolean _reuseDatasets = false;

    private boolean _sawFirstDataSet = false;

    private ArrayList<Integer> _scheduledBinsToAdd = new ArrayList<Integer>();

    private ArrayList<Integer> _scheduledBinsToErase = new ArrayList<Integer>();

    private boolean _showing = false;

    private double _xPersistence = 0.0;

    private boolean _xyInvalid = true;

    private float _width = _DEFAULT_WIDTH;

    private class Bin {
        public Bin(long xPos, int dataset) {
            _dataset = dataset;
            xpos = xPos;
        }

        public void addPoint(PlotPoint point, int pointIndex, long ypos) {
            int absolutePointIndex = pointIndex
                    + _pointInBinOffset.get(_dataset);

            if (_maxYPos < ypos) {
                _maxYPos = ypos;
                _rangeChanged = true;
            }
            if (_minYPos > ypos) {
                _minYPos = ypos;
                _rangeChanged = true;
            }

            if (_firstPointIndex == -1) {
                _needConnectionWithPreviousBin = point.connected;
                _firstYPos = ypos;
                _firstPointIndex = absolutePointIndex;
                _nextPointToPlot = _firstPointIndex;
            } else {
                _isConnected |= point.connected;

                assert _afterLastPointIndex == absolutePointIndex;
            }

            _afterLastPointIndex = absolutePointIndex + 1;
            _lastYPos = ypos;

            _errorBar |= point.errorBar;
        }

        public int afterLastPointIndex() {
            assert _firstPointIndex != -1;
            return _afterLastPointIndex - _pointInBinOffset.get(_dataset);
        }

        public boolean errorBar() {
            return _errorBar;
        }

        public int firstPointIndex() {
            assert _firstPointIndex != -1;
            return _firstPointIndex - _pointInBinOffset.get(_dataset);
        }

        public long firstYPos() {
            assert _firstPointIndex != -1;
            return _firstYPos;
        }

        public long minYPos() {
            assert _firstPointIndex != -1;
            return _minYPos;
        }

        public long lastYPos() {
            assert _firstPointIndex != -1;
            return _lastYPos;
        }

        public long maxYPos() {
            assert _firstPointIndex != -1;
            return _maxYPos;
        }

        public boolean needConnectionWithPreviousBin() {
            return _needConnectionWithPreviousBin;
        }

        public boolean isConnectedWithPreviousBin() {
            return _isConnectedWithPreviousBin;
        }

        public boolean isConnected() {
            return _isConnected;
        }

        public boolean needReplot() {
            return _needConnectionWithPreviousBin || _rangeChanged
                    || _nextPointToPlot != _afterLastPointIndex;
        }

        public int nextPointToPlot() {
            return _nextPointToPlot - _pointInBinOffset.get(_dataset);
        }

        public boolean rangeChanged() {
            return _rangeChanged;
        }

        public void resetDisplayStateAfterPlot() {
            if (_needConnectionWithPreviousBin) {
                _isConnectedWithPreviousBin = true;
                _needConnectionWithPreviousBin = false;
            }
            _rangeChanged = false;
            _nextPointToPlot = _afterLastPointIndex;
        }

        public void setNotConnectedWithPreviousBin() {
            _needConnectionWithPreviousBin = false;
            _isConnectedWithPreviousBin = false;
            _points.get(_dataset).get(
                    _firstPointIndex - _pointInBinOffset.get(_dataset)).connected = false;
        }

        public final long xpos;

        private int _afterLastPointIndex = 0;

        private int _dataset = 0;

        private boolean _errorBar = false;

        private int _firstPointIndex = -1;

        private long _firstYPos = java.lang.Long.MIN_VALUE;

        private boolean _isConnected = false;
        private boolean _isConnectedWithPreviousBin = false;

        private long _lastYPos = java.lang.Long.MIN_VALUE;

        private long _maxYPos = java.lang.Long.MIN_VALUE;
        private long _minYPos = java.lang.Long.MAX_VALUE;

        private boolean _needConnectionWithPreviousBin = false;

        private boolean _rangeChanged = false;
        private int _nextPointToPlot = 0;
    }

    private static class Format implements Serializable {

        public boolean connected;

        public boolean connectedUseDefault = true;

        public boolean impulses;

        public boolean impulsesUseDefault = true;

        public BasicStroke lineStroke;

        public String lineStyle;

        public boolean lineStyleUseDefault = true;

        public int marks;

        public boolean marksUseDefault = true;
    }
}
