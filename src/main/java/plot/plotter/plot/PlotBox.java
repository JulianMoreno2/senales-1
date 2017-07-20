package plot.plotter.plot;

import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;

import plot.plotter.util.StringUtilities;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Timer;

@SuppressWarnings("serial")
public class PlotBox extends JPanel implements PlotBoxInterface {

    public PlotBox() {
        setOpaque(true);
        fillPlot();
        setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        addMouseListener(new ZoomListener());
        addKeyListener(new CommandListener());
        addMouseMotionListener(new DragListener());

        _measureFonts();
    }

    @Override
    public synchronized void addCaptionLine(String captionLine) {
        _plotImage = null;
        _captionStrings.addElement(captionLine);
    }

    @Override
    public synchronized void addLegend(int dataset, String legend) {
        _plotImage = null;

        if (legend == null || legend.equals("")) {
            return;
        }

        _legendStrings.addElement(legend);
        _legendDatasets.addElement(Integer.valueOf(dataset));
    }

    @Override
    public synchronized void addXTick(String label, double position) {
        _plotImage = null;

        if (_xticks == null) {
            _xticks = new Vector();
            _xticklabels = new Vector();
        }

        _xticks.addElement(Double.valueOf(position));
        _xticklabels.addElement(label);
    }

    @Override
    public synchronized void addYTick(String label, double position) {
        _plotImage = null;

        if (_yticks == null) {
            _yticks = new Vector();
            _yticklabels = new Vector();
        }

        _yticks.addElement(Double.valueOf(position));
        _yticklabels.addElement(label);
    }

    @Override
    public synchronized void clear(boolean axes) {
        _plotImage = null;

        _xBottom = Double.MAX_VALUE;
        _xTop = -Double.MAX_VALUE;
        _yBottom = Double.MAX_VALUE;
        _yTop = -Double.MAX_VALUE;

        if (axes) {
            _yMax = 0;
            _yMin = 0;
            _xMax = 0;
            _xMin = 0;
            _xRangeGiven = false;
            _yRangeGiven = false;
            _originalXRangeGiven = false;
            _originalYRangeGiven = false;
            _rangesGivenByZooming = false;
            _xlog = false;
            _ylog = false;
            _grid = true;
            _wrap = false;
            _usecolor = true;

            _filespec = null;
            _xlabel = null;
            _ylabel = null;
            _title = null;
            _legendStrings = new Vector();
            _legendDatasets = new Vector();
            _xticks = null;
            _xticklabels = null;
            _yticks = null;
            _yticklabels = null;
        }
    }

    @Override
    public synchronized void clearCaptions() {
        _plotImage = null;
        _captionStrings = new Vector();
    }

    @Override
    public synchronized void clearLegends() {
        _plotImage = null;

        _legendStrings = new Vector();
        _legendDatasets = new Vector();
    }

    @Override
    public void deferIfNecessary(Runnable action) {
        if (EventQueue.isDispatchThread()) {
            action.run();
        } else {

            _deferredActions.add(action);

            if (!_actionsDeferred) {
                Runnable doActions = () -> _executeDeferredActions();

                try {
                    _actionsDeferred = true;
                    SwingUtilities.invokeLater(doActions);
                } catch (Throwable throwable) {
                }

            }
        }
    }

    @Override
    public void destroy() {
        clear(true);
        setAutomaticRescale(false);
        setTimedRepaint(false);

        if (_printButton != null) {
            ActionListener[] listeners = _printButton.getActionListeners();
            for (ActionListener listener : listeners) {
                _printButton.removeActionListener(listener);
            }
            _printButton = null;
        }
        if (_resetButton != null) {
            ActionListener[] listeners = _resetButton.getActionListeners();
            for (ActionListener listener : listeners) {
                _resetButton.removeActionListener(listener);
            }
            _resetButton = null;
        }
        if (_formatButton != null) {
            ActionListener[] listeners = _formatButton.getActionListeners();
            for (ActionListener listener : listeners) {
                _formatButton.removeActionListener(listener);
            }
            _formatButton = null;
        }
        if (_pulsations != null) {
            ActionListener[] listeners = _pulsations.getActionListeners();
            for (ActionListener listener : listeners) {
                _pulsations.removeActionListener(listener);
            }
            _pulsations = null;
        }

        removeAll();
    }

    public synchronized BufferedImage exportImage() {
        Rectangle rectangle = new Rectangle(_preferredWidth, _preferredHeight);
        return exportImage(new BufferedImage(rectangle.width, rectangle.height,
                BufferedImage.TYPE_INT_ARGB), rectangle,
                _defaultImageRenderingHints(), false);
    }

    public synchronized BufferedImage exportImage(Rectangle rectangle) {
        return exportImage(new BufferedImage(rectangle.width, rectangle.height,
                BufferedImage.TYPE_INT_ARGB), rectangle,
                _defaultImageRenderingHints(), false);
    }

    public synchronized BufferedImage exportImage(BufferedImage bufferedImage,
            Rectangle rectangle, RenderingHints hints, boolean transparent) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.addRenderingHints(_defaultImageRenderingHints());

        if (!transparent) {
            graphics.setColor(Color.white);
            graphics.fill(rectangle);
        }

        _drawPlot(graphics, false, rectangle);
        return bufferedImage;
    }

    public synchronized BufferedImage exportImage(BufferedImage bufferedImage) {
        return exportImage(
                bufferedImage,
                new Rectangle(bufferedImage.getWidth(), bufferedImage
                        .getHeight()), _defaultImageRenderingHints(), true);
    }

    public synchronized void exportLatex(File directory) {
        try {
            if (!directory.isDirectory()) {
                if (!directory.mkdir()) {
                    throw new RuntimeException("Failed to create " + directory);
                }
            }

            String makefileContents = "# Makefile for Latex files generated by the Ptolemy II plotter.\n"
                    + "# This makes several assumptions:\n"
                    +
                    "#   - latex, dvips, ps2pdf, and open are all in the path\n"
                    + "#   - pstricks is installed on the local latex.\n"
                    + "#\n"
                    + "FILENAME="
                    + directory.getName()
                    + "\n"
                    + "all:    $(FILENAME).tex\n"
                    + "\tlatex $(FILENAME);\n"
                    + "\tdvips $(FILENAME);\n"
                    + "\tps2pdf $(FILENAME).ps;\n"
                    + "\topen $(FILENAME).pdf\n";
            File makefile = new File(directory, "makefile");
            PrintStream stream = new PrintStream(makefile);
            stream.print(makefileContents);
            stream.close();

            File latexFile = new File(directory, directory.getName() + ".tex");
            PrintStream out = new PrintStream(latexFile);
            out.println("% Plot output generated by ptplot.");
            out.println("\\documentclass[12pt]{article}");
            out.println("\\usepackage{pstricks}");
            out.println("\\begin{document}");
            out.println("\\thispagestyle{empty}");
            double xScale = 6.0 / (_xMax - _xMin);
            double yScale = 4.0 / (_yMax - _yMin);
            double xOrigin = -3.0;
            double yOrigin = 0.0;
            out.println("\\begin{pspicture}[" + "xunit=" + xScale + "in,"
                    + "yunit=" + yScale + "in," + "origin={" + xOrigin + ","
                    + yOrigin + "}," + "showgrid=" + getGrid() + "]" + "("
                    + _xMin + "," + _yMin + ")" + "(" + _xMax + "," + _yMax
                    + ")");

            out.println(_exportLatexPlotData());
            out.println("\\end{pspicture}");
            out.println("\\end{document}");
            out.close();

            String message = "Apologies, but export to Latex is not implemented yet.";
            JOptionPane.showMessageDialog(this, message,
                    "Ptolemy Plot Message", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (Throwable throwable) {
            String message = "Export failed: " + throwable.getMessage();
            JOptionPane.showMessageDialog(this, message,
                    "Ptolemy Plot Message", JOptionPane.ERROR_MESSAGE);

            throw (RuntimeException) throwable.fillInStackTrace();
        }
    }

    public synchronized void exportImage(OutputStream out, String formatName) {
        try {
            boolean match = false;
            String[] supportedFormats = ImageIO.getWriterFormatNames();
            for (String supportedFormat : supportedFormats) {
                if (formatName.equalsIgnoreCase(supportedFormat)) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                throw new Exception("Format " + formatName + " not supported.");
            }
            BufferedImage image = exportImage();
            if (out == null) {
                String message = "Copy to the clipboard is not implemented yet.";
                JOptionPane.showMessageDialog(this, message,
                        "Ptolemy Plot Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ImageIO.write(image, formatName, out);
        } catch (Exception ex) {
            String message = "Export failed: " + ex.getMessage();
            JOptionPane.showMessageDialog(this, message,
                    "Ptolemy Plot Message", JOptionPane.ERROR_MESSAGE);

            throw (RuntimeException) ex.fillInStackTrace();
        }
    }

    @Override
    public synchronized void fillPlot() {
        setXRange(_xBottom, _xTop);
        setYRange(_yBottom, _yTop);
        repaint();
    }

    @Override
    public Vector getCaptions() {
        return _captionStrings;
    }

    @Override
    public boolean getColor() {
        return _usecolor;
    }

    @Override
    public Color[] getColors() {
        return _colors;
    }

    public static Color getColorByName(String name) {
        try {
            if (name.startsWith("#")) {
                name = name.substring(1);
            }

            Color col = new Color(Integer.parseInt(name, 16));
            return col;
        } catch (NumberFormatException e) {
        }

        String[][] names = { { "black", "00000" }, { "white", "ffffff" },
                { "red", "ff0000" }, { "green", "00ff00" },
                { "blue", "0000ff" } };

        for (String[] name2 : names) {
            if (name.equals(name2[0])) {
                try {
                    Color col = new Color(Integer.parseInt(name2[1], 16));
                    return col;
                } catch (NumberFormatException e) {
                }
            }
        }

        return null;
    }

    @Override
    public boolean getGrid() {
        return _grid;
    }

    @Override
    public synchronized String getLegend(int dataset) {
        int idx = _legendDatasets.indexOf(Integer.valueOf(dataset), 0);

        if (idx != -1) {
            return (String) _legendStrings.elementAt(idx);
        } else {
            return null;
        }
    }

    @Override
    public synchronized int getLegendDataset(String legend) {
        int index = _legendStrings.indexOf(legend);

        if (index == -1) {
            return -1;
        }

        return ((Integer) _legendDatasets.get(index)).intValue();
    }

    @Override
    public Rectangle getPlotRectangle() {
        return new Rectangle(_ulx, _uly, _lrx - _ulx, _lry - _uly);
    }

    @Override
    public synchronized Dimension getPreferredSize() {
        return new Dimension(_preferredWidth, _preferredHeight);
    }

    @Override
    public synchronized String getTitle() {
        if (_title == null) {
            return "";
        }

        return _title;
    }

    @Override
    public synchronized double[] getXAutoRange() {
        double[] result = new double[2];
        result[0] = _xBottom;
        result[1] = _xTop;
        return result;
    }

    @Override
    public synchronized String getXLabel() {
        return _xlabel;
    }

    @Override
    public boolean getXLog() {
        return _xlog;
    }

    @Override
    public synchronized double[] getXRange() {
        double[] result = new double[2];

        if (_xRangeGiven) {
            result[0] = _xlowgiven;
            result[1] = _xhighgiven;
        } else {
            result[0] = _xMin + (_xMax - _xMin) * _padding;
            result[1] = _xMax - (_xMax - _xMin) * _padding;
            ;
        }

        return result;
    }

    @Override
    public synchronized Vector[] getXTicks() {
        if (_xticks == null) {
            return null;
        }

        Vector[] result = new Vector[2];
        result[0] = _xticks;
        result[1] = _xticklabels;
        return result;
    }

    @Override
    public synchronized double[] getYAutoRange() {
        double[] result = new double[2];
        result[0] = _yBottom;
        result[1] = _yTop;
        return result;
    }

    @Override
    public String getYLabel() {
        return _ylabel;
    }

    @Override
    public boolean getYLog() {
        return _ylog;
    }

    @Override
    public synchronized double[] getYRange() {
        double[] result = new double[2];

        if (_yRangeGiven) {
            result[0] = _ylowgiven;
            result[1] = _yhighgiven;
        } else {
            result[0] = _yMin + (_yMax - _yMin) * _padding;
            result[1] = _yMax - (_yMax - _yMin) * _padding;
            ;
        }

        return result;
    }

    @Override
    public synchronized Vector[] getYTicks() {
        if (_yticks == null) {
            return null;
        }

        Vector[] result = new Vector[2];
        result[0] = _yticks;
        result[1] = _yticklabels;
        return result;
    }

    @Override
    public synchronized void paintComponent(Graphics graphics) {
        BufferedImage newPlotImage = _plotImage;

        if (newPlotImage == null) {
            Rectangle bounds = getBounds();
            newPlotImage = new BufferedImage(bounds.width, bounds.height,
                    BufferedImage.TYPE_3BYTE_BGR);
            _plotImage = newPlotImage;

            Graphics2D offScreenGraphics = newPlotImage.createGraphics();
            super.paintComponent(offScreenGraphics);
            _drawPlot(offScreenGraphics, true);
        }

        graphics.drawImage(newPlotImage, 0, 0, null);
    }

    @Override
    public synchronized void read(InputStream in) throws IOException {
        try {
            BufferedReader din = new BufferedReader(new InputStreamReader(in));

            try {
                String line = din.readLine();

                while (line != null) {
                    _parseLine(line);
                    line = din.readLine();
                }
            } finally {
                din.close();
            }
        } catch (IOException e) {
            _errorMsg = new String[2];
            _errorMsg[0] = "Failure reading input data.";
            _errorMsg[1] = e.getMessage();
            throw e;
        }
    }

    @Override
    public synchronized void read(String command) {
        _parseLine(command);
    }

    @Override
    public synchronized void removeLegend(int dataset) {
        final int len = _legendDatasets.size();
        int foundIndex = -1;
        boolean found = false;

        for (int i = 0; i < len && !found; ++i) {
            if (((Integer) _legendDatasets.get(i)).intValue() == dataset) {
                foundIndex = i;
                found = true;
            }
        }

        if (found) {
            _legendDatasets.remove(foundIndex);
            _legendStrings.remove(foundIndex);
        }
    }

    @Override
    public synchronized void renameLegend(int dataset, String newName) {
        int index = _legendDatasets.indexOf(Integer.valueOf(dataset), 0);

        if (index != -1) {
            _legendStrings.setElementAt(newName, index);
            _plotImage = null;
        }
    }

    @Override
    public synchronized void resetAxes() {
        setXRange(_originalXlow, _originalXhigh);
        setYRange(_originalYlow, _originalYhigh);
        repaint();
    }

    @Override
    public void samplePlot() {
    }

    @Override
    public void setAutomaticRescale(boolean automaticRescale) {
        _automaticRescale = automaticRescale;
        if (automaticRescale) {
            if (_timerTask == null) {
                _timerTask = new TimedRepaint();
            }
            _timerTask.addListener(this);
        } else if (!_timedRepaint) {
            _resetScheduledTasks();
            if (_timerTask != null) {
                _timerTask.removeListener(this);
                _timerTask = null;
            }
        }
    }

    @Override
    public synchronized void setBackground(Color background) {
        _plotImage = null;
        _background = background;
        super.setBackground(_background);
    }

    @Override
    public void setBackground(Object background) {
        setBackground((Color) background);
    }

    @Override
    public synchronized void setBounds(int x, int y, int width, int height) {
        _width = width;
        _height = height;
        _plotImage = null;

        super.setBounds(x, y, _width, _height);
    }

    @Override
    public synchronized void setButtons(boolean visible) {

        _plotImage = null;

        _resetButton = new JButton("Reset");
        _resetButton.setBorderPainted(false);
        _resetButton.setPreferredSize(new Dimension(80, 20));
        _resetButton
        .setToolTipText("Reset X and Y ranges to their original values");
        _resetButton.addActionListener(new ButtonListener());
        add(_resetButton);
        _resetButton.setVisible(true);

        _formatButton = new JButton("Format");
        _formatButton.setBorderPainted(false);
        _formatButton.setPreferredSize(new Dimension(100, 20));
        _formatButton.setToolTipText("Set the plot format");
        _formatButton.addActionListener(new ButtonListener());
        add(_formatButton);
        _formatButton.setVisible(visible);

        _pulsations = new JButton("Pulsations");
        
      
        _pulsations.setBorderPainted(false);
        _pulsations.setPreferredSize(new Dimension(60, 20));
        _pulsations.setToolTipText("Pulsation per minute");
        _pulsations.addActionListener(new ButtonListener());
        add(_pulsations);
        _pulsations.setVisible(false);
    }

    @Override
    public void setCaptions(Vector captionStrings) {
        _plotImage = null;
        _captionStrings = captionStrings;
    }

    @Override
    public synchronized void setColor(boolean useColor) {
        _plotImage = null;
        _usecolor = useColor;
    }

    public synchronized void setColors(Color[] colors) {
        _plotImage = null;
        _colors = colors;
    }

    @Override
    public void setColors(Object[] colors) {
        setColors((Color[]) colors);
    }

    @Override
    public synchronized void setForeground(Color foreground) {
        _plotImage = null;
        _foreground = foreground;
        super.setForeground(_foreground);
    }

    @Override
    public void setForeground(Object foreground) {
        setForeground((Color) foreground);
    }

    @Override
    public synchronized void setGrid(boolean grid) {
        _plotImage = null;
        _grid = grid;
    }

    @Override
    public synchronized void setLabelFont(String name) {
        _plotImage = null;
        _labelFont = Font.decode(name);
        _labelFontMetrics = getFontMetrics(_labelFont);
    }

    public synchronized void setPlotRectangle(Rectangle rectangle) {
        _plotImage = null;
        _specifiedPlotRectangle = rectangle;
    }

    @Override
    public void setPlotRectangle(Object rectangle) {
        setPlotRectangle((Rectangle) rectangle);
    }

    @Override
    public synchronized void setSize(int width, int height) {
        _plotImage = null;

        _width = width;
        _height = height;
        _preferredWidth = width;
        _preferredHeight = height;
        super.setSize(width, height);
    }

    @Override
    public void setTimedRepaint(boolean timedRepaint) {
        _timedRepaint = timedRepaint;
        if (timedRepaint) {
            if (_timerTask == null) {
                _timerTask = new TimedRepaint();
            }
            _timerTask.addListener(this);
        } else if (!_automaticRescale) {
            if (_timerTask != null) {
                _timerTask.removeListener(this);
                _timerTask = null;
            }
            _resetScheduledTasks();
        }
    }

    @Override
    public synchronized void setTitle(String title) {
        _plotImage = null;
        _title = title;
    }

    @Override
    public synchronized void setTitleFont(String name) {
        _plotImage = null;
        _titleFont = Font.decode(name);
        _titleFontMetrics = getFontMetrics(_titleFont);
    }

    @Override
    public synchronized void setWrap(boolean wrap) {
        _plotImage = null;
        _wrap = wrap;

        if (!_xRangeGiven) {
            if (_xBottom > _xTop) {
                setXRange(0, 0);
            } else {
                setXRange(_xBottom, _xTop);
            }
        }

        _wrapLow = _xlowgiven;
        _wrapHigh = _xhighgiven;
    }

    @Override
    public synchronized void setXLabel(String label) {
        _plotImage = null;
        _xlabel = label;
    }

    @Override
    public synchronized void setXLog(boolean xlog) {
        _plotImage = null;
        _xlog = xlog;
    }

    @Override
    public synchronized void setXRange(double min, double max) {
        _plotImage = null;
        _xRangeGiven = true;
        _xlowgiven = min;
        _xhighgiven = max;
        _setXRange(min, max);
    }

    @Override
    public synchronized void setYLabel(String label) {
        _plotImage = null;
        _ylabel = label;
    }

    @Override
    public synchronized void setYLog(boolean ylog) {
        _plotImage = null;
        _ylog = ylog;
    }

    @Override
    public synchronized void setYRange(double min, double max) {
        _plotImage = null;
        _yRangeGiven = true;
        _ylowgiven = min;
        _yhighgiven = max;
        _setYRange(min, max);
    }

    @Override
    public void write(OutputStream out) {
        write(out, null);
    }

    @Override
    public synchronized void write(OutputStream out, String dtd) {
        write(new OutputStreamWriter(out), dtd);
    }

    @Override
    public synchronized void write(Writer out, String dtd) {
        PrintWriter output = new PrintWriter(new BufferedWriter(out), false);

        if (dtd == null) {
            output.println("<?xml version=\"1.0\" standalone=\"yes\"?>");
            output.println("<!DOCTYPE plot PUBLIC \"-//UC Berkeley//DTD PlotML 1//EN\"");
            output.println("    \"http://ptolemy.eecs.berkeley.edu/xml/dtd/PlotML_1.dtd\">");
        } else {
            output.println("<?xml version=\"1.0\" standalone=\"no\"?>");
            output.println("<!DOCTYPE plot SYSTEM \"" + dtd + "\">");
        }

        output.println("<plot>");
        output.println("<!-- Ptolemy plot, version " + PTPLOT_RELEASE
                + " , PlotML format. -->");
        writeFormat(output);
        writeData(output);
        output.println("</plot>");
        output.flush();
    }

    @Override
    public synchronized void writeData(PrintWriter output) {
    }

    @Override
    public synchronized void writeFormat(PrintWriter output) {
        if (_title != null) {
            output.println("<title>" + _title + "</title>");
        }

        if (_captionStrings != null) {
            for (@SuppressWarnings("rawtypes")
			Enumeration captions = _captionStrings.elements(); captions
                    .hasMoreElements();) {
                String captionLine = (String) captions.nextElement();
                output.println("<caption>" + captionLine + "</caption>");
            }
        }

        if (_xlabel != null) {
            output.println("<xLabel>" + _xlabel + "</xLabel>");
        }

        if (_ylabel != null) {
            output.println("<yLabel>" + _ylabel + "</yLabel>");
        }

        if (_xRangeGiven) {
            output.println("<xRange min=\"" + _xlowgiven + "\" max=\""
                    + _xhighgiven + "\"/>");
        }

        if (_yRangeGiven) {
            output.println("<yRange min=\"" + _ylowgiven + "\" max=\""
                    + _yhighgiven + "\"/>");
        }

        if (_xticks != null && _xticks.size() > 0) {
            output.println("<xTicks>");

            int last = _xticks.size() - 1;

            for (int i = 0; i <= last; i++) {
                output.println("  <tick label=\""
                        + (String) _xticklabels.elementAt(i) + "\" position=\""
                        + _xticks.elementAt(i) + "\"/>");
            }

            output.println("</xTicks>");
        }

        if (_yticks != null && _yticks.size() > 0) {
            output.println("<yTicks>");

            int last = _yticks.size() - 1;

            for (int i = 0; i <= last; i++) {
                output.println("  <tick label=\""
                        + (String) _yticklabels.elementAt(i) + "\" position=\""
                        + _yticks.elementAt(i) + "\"/>");
            }

            output.println("</yTicks>");
        }

        if (_xlog) {
            output.println("<xLog/>");
        }

        if (_ylog) {
            output.println("<yLog/>");
        }

        if (!_grid) {
            output.println("<noGrid/>");
        }

        if (_wrap) {
            output.println("<wrap/>");
        }

        if (!_usecolor) {
            output.println("<noColor/>");
        }
    }

    @Override
    public synchronized void zoom(double lowx, double lowy, double highx,
            double highy) {
        setXRange(lowx, highx);
        setYRange(lowy, highy);
        repaint();
    }

    public static final String PTPLOT_RELEASE = "5.10";

    protected boolean _automaticRescale() {
        return _automaticRescale;
    }

    protected synchronized void _drawPlot(Graphics graphics, boolean clearfirst) {
        Rectangle bounds = getBounds();
        _drawPlot(graphics, clearfirst, bounds);
    }

    protected synchronized void _drawPlot(Graphics graphics,
            boolean clearfirst, Rectangle drawRect) {
        if (graphics == null) {
            return;
        }

        graphics.setPaintMode();

        if (_errorMsg != null) {
            int fheight = _labelFontMetrics.getHeight() + 2;
            int msgy = fheight;
            graphics.setColor(Color.black);

            for (String element : _errorMsg) {
                graphics.drawString(element, 10, msgy);
                msgy += fheight;
                System.err.println(element);
            }

            return;
        }

        if (!_xRangeGiven) {
            if (_xBottom > _xTop) {
                _setXRange(0, 0);
            } else {
                _setXRange(_xBottom, _xTop);
            }
        }

        if (!_yRangeGiven) {
            if (_yBottom > _yTop) {
                _setYRange(0, 0);
            } else {
                _setYRange(_yBottom, _yTop);
            }
        }

        Rectangle workingPlotRectangle = null;

        if (_specifiedPlotRectangle != null) {
            workingPlotRectangle = new Rectangle(Math.max(0,
                    _specifiedPlotRectangle.x), Math.max(0,
                            _specifiedPlotRectangle.y), Math.min(drawRect.width,
                                    _specifiedPlotRectangle.width), Math.min(drawRect.height,
                                            _specifiedPlotRectangle.height));
        }

        int titley = 0;
        int titlefontheight = _titleFontMetrics.getHeight();

        if (_title == null) {
            _title = "";
        }

        titley = titlefontheight + _topPadding;

        int captionHeight = _captionStrings.size()
                * _captionFontMetrics.getHeight();
        if (captionHeight > 0) {
            captionHeight += 5;
        }

        Font previousFont = graphics.getFont();
        graphics.setFont(_labelFont);
        graphics.setColor(_foreground);

        int labelheight = _labelFontMetrics.getHeight();
        int halflabelheight = labelheight / 2;

        int ySPos = drawRect.height - captionHeight - 5;
        int xSPos = drawRect.width - _rightPadding;

        if (_xlog) {
            _xExp = (int) Math.floor(_xtickMin);
        }

        if (_xExp != 0 && _xticks == null) {
            String superscript = Integer.toString(_xExp);
            xSPos -= _superscriptFontMetrics.stringWidth(superscript);
            graphics.setFont(_superscriptFont);

            if (!_xlog) {
                graphics.drawString(superscript, xSPos, ySPos - halflabelheight);
                xSPos -= _labelFontMetrics.stringWidth("x10");
                graphics.setFont(_labelFont);
                graphics.drawString("x10", xSPos, ySPos);
            }

            _bottomPadding = 3 * labelheight / 2 + 5;
        }

        if (_xlabel != null && _bottomPadding < captionHeight + labelheight + 5) {
            _bottomPadding = captionHeight + labelheight + 5;
        }

        if (workingPlotRectangle != null) {
            _uly = workingPlotRectangle.y;
        } else {
            _uly = titley + 5;
        }

        if (workingPlotRectangle != null) {
            _lry = workingPlotRectangle.y + workingPlotRectangle.height;
        } else {
            _lry = drawRect.height - labelheight - _bottomPadding - 3;
        }

        int height = _lry - _uly;
        _yscale = height / (_yMax - _yMin);
        double _ytickscale = height / (_ytickMax - _ytickMin);

        int ny = 2 + height / (labelheight + 10);

        double yStep = _roundUp((_ytickMax - _ytickMin) / ny);

        double yStart = yStep * Math.ceil(_ytickMin / yStep);

        int widesty = 0;

        String[] ylabels = new String[ny];
        int[] ylabwidth = new int[ny];

        int ind = 0;

        if (_yticks == null) {
            Vector ygrid = null;

            if (_ylog) {
                ygrid = _gridInit(yStart, yStep, true, null);
            }

            int numfracdigits = _numFracDigits(yStep);

            double yTmpStart = yStart;

            if (_ylog) {
                yTmpStart = _gridStep(ygrid, yStart, yStep, _ylog);
            }

            for (double ypos = yTmpStart; ypos <= _ytickMax; ypos = _gridStep(
                    ygrid, ypos, yStep, _ylog)) {
                if (ind >= ny) {
                    break;
                }

                String yticklabel;

                if (_ylog) {
                    yticklabel = _formatLogNum(ypos, numfracdigits);
                } else {
                    yticklabel = _formatNum(ypos, numfracdigits);
                }

                ylabels[ind] = yticklabel;

                int lw = _labelFontMetrics.stringWidth(yticklabel);
                ylabwidth[ind++] = lw;

                if (lw > widesty) {
                    widesty = lw;
                }
            }
        } else {
            Enumeration nl = _yticklabels.elements();

            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                int lw = _labelFontMetrics.stringWidth(label);

                if (lw > widesty) {
                    widesty = lw;
                }
            }
        }

        if (workingPlotRectangle != null) {
            _ulx = workingPlotRectangle.x;
        } else {
            if (_ylabel != null) {
                _ulx = widesty + _labelFontMetrics.stringWidth("W")
                        + _leftPadding;
            } else {
                _ulx = widesty + _leftPadding;
            }
        }

        int legendwidth = _drawLegend(graphics, drawRect.width - _rightPadding,
                _uly);

        if (workingPlotRectangle != null) {
            _lrx = workingPlotRectangle.x + workingPlotRectangle.width;
        } else {
            _lrx = drawRect.width - legendwidth - _rightPadding;
        }

        int width = _lrx - _ulx;
        _xscale = width / (_xMax - _xMin);

        double _xtickscale = width / (_xtickMax - _xtickMin);

        graphics.setColor(Color.white);
        graphics.fillRect(_ulx, _uly, width, height);

        graphics.setColor(_foreground);
        graphics.drawRect(_ulx, _uly, width, height);

        int tickLength = 5;
        int xCoord1 = _ulx + tickLength;
        int xCoord2 = _lrx - tickLength;

        if (_yticks == null) {
            Vector ygrid = null;
            double yTmpStart = yStart;

            if (_ylog) {
                ygrid = _gridInit(yStart, yStep, true, null);
                yTmpStart = _gridStep(ygrid, yStart, yStep, _ylog);
                ny = ind;
            }

            ind = 0;

            boolean needExponent = _ylog;

            for (double ypos = yTmpStart; ypos <= _ytickMax; ypos = _gridStep(
                    ygrid, ypos, yStep, _ylog)) {
                if (ind >= ny) {
                    break;
                }

                int yCoord1 = _lry - (int) ((ypos - _ytickMin) * _ytickscale);

                int offset = 0;

                if (ind > 0 && !_ylog) {
                    offset = halflabelheight;
                }

                graphics.drawLine(_ulx, yCoord1, xCoord1, yCoord1);
                graphics.drawLine(_lrx, yCoord1, xCoord2, yCoord1);

                if (_grid && yCoord1 != _uly && yCoord1 != _lry) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1, yCoord1, xCoord2, yCoord1);
                    graphics.setColor(_foreground);
                }

                if (_ylog && ylabels[ind].indexOf('e') != -1) {
                    needExponent = false;
                }

                graphics.drawString(ylabels[ind], _ulx - ylabwidth[ind++] - 4,
                        yCoord1 + offset);
            }

            if (_ylog) {
                Vector unlabeledgrid = _gridInit(yStart, yStep, false, ygrid);

                if (unlabeledgrid.size() > 0) {
                    double tmpStep = yStep > 1.0 ? 1.0 : yStep;

                    for (double ypos = _gridStep(unlabeledgrid, yStart,
                            tmpStep, _ylog); ypos <= _ytickMax; ypos = _gridStep(
                                    unlabeledgrid, ypos, tmpStep, _ylog)) {
                        int yCoord1 = _lry
                                - (int) ((ypos - _ytickMin) * _ytickscale);

                        if (_grid && yCoord1 != _uly && yCoord1 != _lry) {
                            graphics.setColor(Color.lightGray);
                            graphics.drawLine(_ulx + 1, yCoord1, _lrx - 1,
                                    yCoord1);
                            graphics.setColor(_foreground);
                        }
                    }
                }

                if (needExponent) {
                    _yExp = (int) Math.floor(yTmpStart);
                } else {
                    _yExp = 0;
                }
            }

            if (_yExp != 0) {
                graphics.drawString("x10", 2, titley);
                graphics.setFont(_superscriptFont);
                graphics.drawString(Integer.toString(_yExp),
                        _labelFontMetrics.stringWidth("x10") + 2, titley
                        - halflabelheight);
                graphics.setFont(_labelFont);
            }
        } else {
            Enumeration nt = _yticks.elements();
            Enumeration nl = _yticklabels.elements();

            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                double ypos = ((Double) nt.nextElement()).doubleValue();

                if (ypos > _yMax || ypos < _yMin) {
                    continue;
                }

                int yCoord1 = _lry - (int) ((ypos - _yMin) * _yscale);
                int offset = 0;

                if (ypos < _lry - labelheight) {
                    offset = halflabelheight;
                }

                graphics.drawLine(_ulx, yCoord1, xCoord1, yCoord1);
                graphics.drawLine(_lrx, yCoord1, xCoord2, yCoord1);

                if (_grid && yCoord1 != _uly && yCoord1 != _lry) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1, yCoord1, xCoord2, yCoord1);
                    graphics.setColor(_foreground);
                }
                graphics.drawString(label,
                        _ulx - _labelFontMetrics.stringWidth(label) - 3,
                        yCoord1 + offset);
            }
        }

        int yCoord1 = _uly + tickLength;
        int yCoord2 = _lry - tickLength;
        int charwidth = _labelFontMetrics.stringWidth("8");

        if (_xticks == null) {
            int nx = 10;
            double xStep = 0.0;
            int numfracdigits = 0;

            if (_xlog) {
                nx = 2 + width / (charwidth * 6 + 10);
            } else {
                int count = 0;

                while (count++ <= 10) {
                    xStep = _roundUp((_xtickMax - _xtickMin) / nx);

                    numfracdigits = _numFracDigits(xStep);

                    int intdigits = _numIntDigits(_xtickMax);
                    int inttemp = _numIntDigits(_xtickMin);

                    if (intdigits < inttemp) {
                        intdigits = inttemp;
                    }
                    int maxlabelwidth = charwidth
                            * (numfracdigits + 2 + intdigits);

                    int savenx = nx;

                    nx = 2 + width / (maxlabelwidth + 10);

                    if (nx - savenx <= 1 || savenx - nx <= 1) {
                        break;
                    }
                }
            }

            xStep = _roundUp((_xtickMax - _xtickMin) / nx);
            numfracdigits = _numFracDigits(xStep);

            double xStart = xStep * Math.ceil(_xtickMin / xStep);

            Vector xgrid = null;
            double xTmpStart = xStart;

            if (_xlog) {
                xgrid = _gridInit(xStart, xStep, true, null);

                xTmpStart = _gridRoundUp(xgrid, xStart);
            }

            boolean needExponent = _xlog;

            for (double xpos = xTmpStart; xpos <= _xtickMax; xpos = _gridStep(
                    xgrid, xpos, xStep, _xlog)) {
                String xticklabel;

                if (_xlog) {
                    xticklabel = _formatLogNum(xpos, numfracdigits);

                    if (xticklabel.indexOf('e') != -1) {
                        needExponent = false;
                    }
                } else {
                    xticklabel = _formatNum(xpos, numfracdigits);
                }

                xCoord1 = _ulx + (int) ((xpos - _xtickMin) * _xtickscale);
                graphics.drawLine(xCoord1, _uly, xCoord1, yCoord1);
                graphics.drawLine(xCoord1, _lry, xCoord1, yCoord2);

                if (_grid && xCoord1 != _ulx && xCoord1 != _lrx) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1, yCoord1, xCoord1, yCoord2);
                    graphics.setColor(_foreground);
                }

                int labxpos = xCoord1
                        - _labelFontMetrics.stringWidth(xticklabel) / 2;

                graphics.drawString(xticklabel, labxpos, _lry + 3 + labelheight);
            }

            if (_xlog) {
                double tmpStep = xStep > 1.0 ? 1.0 : xStep;

                xTmpStart = tmpStep * Math.ceil(_xtickMin / tmpStep);

                Vector unlabeledgrid = _gridInit(xTmpStart, tmpStep, false,
                        xgrid);

                if (unlabeledgrid.size() > 0) {
                    for (double xpos = _gridStep(unlabeledgrid, xTmpStart,
                            tmpStep, _xlog); xpos <= _xtickMax; xpos = _gridStep(
                                    unlabeledgrid, xpos, tmpStep, _xlog)) {
                        xCoord1 = _ulx
                                + (int) ((xpos - _xtickMin) * _xtickscale);

                        if (_grid && xCoord1 != _ulx && xCoord1 != _lrx) {
                            graphics.setColor(Color.lightGray);
                            graphics.drawLine(xCoord1, _uly + 1, xCoord1,
                                    _lry - 1);
                            graphics.setColor(_foreground);
                        }
                    }
                }

                if (needExponent) {
                    _xExp = (int) Math.floor(xTmpStart);
                    graphics.setFont(_superscriptFont);
                    graphics.drawString(Integer.toString(_xExp), xSPos, ySPos
                            - halflabelheight);
                    xSPos -= _labelFontMetrics.stringWidth("x10");
                    graphics.setFont(_labelFont);
                    graphics.drawString("x10", xSPos, ySPos);
                } else {
                    _xExp = 0;
                }
            }
        } else {
            Enumeration nt = _xticks.elements();
            Enumeration nl = _xticklabels.elements();

            double preLength = 0.0;

            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                double xpos = ((Double) nt.nextElement()).doubleValue();

                if (xpos > _xMax || xpos < _xMin) {
                    continue;
                }

                xCoord1 = _ulx + (int) ((xpos - _xMin) * _xscale);

                int labxpos = xCoord1 - _labelFontMetrics.stringWidth(label)
                        / 2;

                if (labxpos > preLength) {
                    preLength = xCoord1 + _labelFontMetrics.stringWidth(label)
                            / 2 + 10;

                    graphics.drawString(label, labxpos, _lry + 3 + labelheight);

                    graphics.drawLine(xCoord1, _uly, xCoord1, yCoord1);
                    graphics.drawLine(xCoord1, _lry, xCoord1, yCoord2);

                    if (_grid && xCoord1 != _ulx && xCoord1 != _lrx) {
                        graphics.setColor(Color.lightGray);
                        graphics.drawLine(xCoord1, yCoord1, xCoord1, yCoord2);
                        graphics.setColor(_foreground);
                    }
                }
            }
        }

        graphics.setColor(_foreground);

        if (_title != null) {
            graphics.setFont(_titleFont);

            int titlex = _ulx + (width - _titleFontMetrics.stringWidth(_title))
                    / 2;
            graphics.drawString(_title, titlex, titley);
        }

        graphics.setFont(_labelFont);

        if (_xlabel != null) {
            int labelx = _ulx
                    + (width - _labelFontMetrics.stringWidth(_xlabel)) / 2;
            graphics.drawString(_xlabel, labelx, ySPos);
        }

        int charcenter = 2 + _labelFontMetrics.stringWidth("W") / 2;

        if (_ylabel != null) {
            int yl = _ylabel.length();

            if (graphics instanceof Graphics2D) {
                int starty = _uly + (_lry - _uly) / 2
                        + _labelFontMetrics.stringWidth(_ylabel) / 2
                        - charwidth;
                Graphics2D g2d = (Graphics2D) graphics;

                int startx = charcenter + halflabelheight - 2;
                g2d.rotate(Math.toRadians(-90), startx, starty);
                g2d.drawString(_ylabel, startx, starty);
                g2d.rotate(Math.toRadians(90), startx, starty);
            } else {
                int starty = _uly + (_lry - _uly) / 2 - yl * halflabelheight
                        + labelheight;

                for (int i = 0; i < yl; i++) {
                    String nchar = _ylabel.substring(i, i + 1);
                    int cwidth = _labelFontMetrics.stringWidth(nchar);
                    graphics.drawString(nchar, charcenter - cwidth / 2, starty);
                    starty += labelheight;
                }
            }
        }

        graphics.setFont(_captionFont);
        int fontHt = _captionFontMetrics.getHeight();
        int yCapPosn = drawRect.height - captionHeight + 14;
        for (Enumeration captions = _captionStrings.elements(); captions
                .hasMoreElements();) {
            String captionLine = (String) captions.nextElement();
            int labelx = _ulx
                    + (width - _captionFontMetrics.stringWidth(captionLine))
                    / 2;
            graphics.drawString(captionLine, labelx, yCapPosn);
            yCapPosn += fontHt;
        }
        graphics.setFont(previousFont);
    }

    protected void _drawPoint(Graphics graphics, int dataset, long xpos,
            long ypos, boolean clip) {
        if (graphics == null) {
            return;
        }

        boolean pointinside = ypos <= _lry && ypos >= _uly && xpos <= _lrx
                && xpos >= _ulx;

                if (!pointinside && clip) {
                    return;
                }

                graphics.fillRect((int) xpos - 6, (int) ypos - 6, 6, 6);
    }
    protected String _exportLatexPlotData() {
        return "";
    }

    protected void _help() {
        String message = "Ptolemy plot package\n"
                + "Only Academic Uses\n";
        JOptionPane.showMessageDialog(this, message,
                "Ptolemy Plot Help Window", JOptionPane.INFORMATION_MESSAGE);
    }

    protected boolean _parseLine(String line) {
        String lcLine = line.toLowerCase(Locale.getDefault());

        if (lcLine.startsWith("#")) {
            return true;
        } else if (lcLine.startsWith("titletext:")) {
            setTitle(line.substring(10).trim());
            return true;
        } else if (lcLine.startsWith("title:")) {
            setTitle(line.substring(6).trim());
            return true;
        } else if (lcLine.startsWith("xlabel:")) {
            setXLabel(line.substring(7).trim());
            return true;
        } else if (lcLine.startsWith("ylabel:")) {
            setYLabel(line.substring(7).trim());
            return true;
        } else if (lcLine.startsWith("xrange:")) {
            int comma = line.indexOf(",", 7);

            if (comma > 0) {
                String min = line.substring(7, comma).trim();
                String max = line.substring(comma + 1).trim();

                try {
                    Double dmin = Double.valueOf(min);
                    Double dmax = Double.valueOf(max);
                    setXRange(dmin.doubleValue(), dmax.doubleValue());
                } catch (NumberFormatException e) {
                }
            }

            return true;
        } else if (lcLine.startsWith("yrange:")) {
            int comma = line.indexOf(",", 7);

            if (comma > 0) {
                String min = line.substring(7, comma).trim();
                String max = line.substring(comma + 1).trim();

                try {
                    Double dmin = Double.valueOf(min);
                    Double dmax = Double.valueOf(max);
                    setYRange(dmin.doubleValue(), dmax.doubleValue());
                } catch (NumberFormatException e) {
                }
            }

            return true;
        } else if (lcLine.startsWith("xticks:")) {
            _parsePairs(line.substring(7), true);
            return true;
        } else if (lcLine.startsWith("yticks:")) {
            _parsePairs(line.substring(7), false);
            return true;
        } else if (lcLine.startsWith("xlog:")) {
            if (lcLine.indexOf("off", 5) >= 0) {
                _xlog = false;
            } else {
                _xlog = true;
            }

            return true;
        } else if (lcLine.startsWith("ylog:")) {
            if (lcLine.indexOf("off", 5) >= 0) {
                _ylog = false;
            } else {
                _ylog = true;
            }

            return true;
        } else if (lcLine.startsWith("grid:")) {
            if (lcLine.indexOf("off", 5) >= 0) {
                _grid = false;
            } else {
                _grid = true;
            }

            return true;
        } else if (lcLine.startsWith("wrap:")) {
            if (lcLine.indexOf("off", 5) >= 0) {
                _wrap = false;
            } else {
                _wrap = true;
            }

            return true;
        } else if (lcLine.startsWith("color:")) {
            if (lcLine.indexOf("off", 6) >= 0) {
                _usecolor = false;
            } else {
                _usecolor = true;
            }

            return true;
        } else if (lcLine.startsWith("captions:")) {
            addCaptionLine(line.substring(10));
            return true;
        }

        return false;
    }

    protected void _resetScheduledTasks() {
    }

    protected void _scheduledRedraw() {
    }

    @Deprecated
    protected void _setButtonsVisibility(boolean visibility) {
        _plotImage = null;
        _printButton.setVisible(visibility);
        _pulsations.setVisible(visibility);
        _formatButton.setVisible(visibility);
        _resetButton.setVisible(visibility);
    }

    protected void _setPadding(double padding) {
        _plotImage = null;

        _padding = padding;
    }

    protected boolean _timedRepaint() {
        return _timedRepaint || _automaticRescale;
    }

    @Deprecated
    protected void _writeOldSyntax(PrintWriter output) {
        output.println("# Plot");

        if (_title != null) {
            output.println("TitleText: " + _title);
        }

        if (_captionStrings != null) {
            for (Enumeration captions = _captionStrings.elements(); captions
                    .hasMoreElements();) {
                String captionLine = (String) captions.nextElement();
                output.println("Caption: " + captionLine);
            }
        }

        if (_xlabel != null) {
            output.println("XLabel: " + _xlabel);
        }

        if (_ylabel != null) {
            output.println("YLabel: " + _ylabel);
        }

        if (_xRangeGiven) {
            output.println("XRange: " + _xlowgiven + ", " + _xhighgiven);
        }

        if (_yRangeGiven) {
            output.println("YRange: " + _ylowgiven + ", " + _yhighgiven);
        }

        if (_xticks != null && _xticks.size() > 0) {
            output.print("XTicks: ");

            int last = _xticks.size() - 1;

            for (int i = 0; i < last; i++) {
                output.print("\"" + (String) _xticklabels.elementAt(i) + "\" "
                        + _xticks.elementAt(i) + ", ");
            }

            output.println("\"" + (String) _xticklabels.elementAt(last) + "\" "
                    + _xticks.elementAt(last));
        }

        if (_yticks != null && _yticks.size() > 0) {
            output.print("YTicks: ");

            int last = _yticks.size() - 1;

            for (int i = 0; i < last; i++) {
                output.print("\"" + (String) _yticklabels.elementAt(i) + "\" "
                        + _yticks.elementAt(i) + ", ");
            }

            output.println("\"" + (String) _yticklabels.elementAt(last) + "\" "
                    + _yticks.elementAt(last));
        }

        if (_xlog) {
            output.println("XLog: on");
        }

        if (_ylog) {
            output.println("YLog: on");
        }

        if (!_grid) {
            output.println("Grid: off");
        }

        if (_wrap) {
            output.println("Wrap: on");
        }

        if (!_usecolor) {
            output.println("Color: off");
        }
    }

    protected transient volatile double _yMax = 0;

    protected transient volatile double _yMin = 0;

    protected transient volatile double _xMax = 0;

    protected transient volatile double _xMin = 0;

    protected volatile double _padding = 0.05;

    protected transient BufferedImage _plotImage = null;

    protected transient boolean _xRangeGiven = false;

    protected transient boolean _yRangeGiven = false;

    protected transient boolean _rangesGivenByZooming = false;

    protected double _xlowgiven;

    protected double _xhighgiven;

    protected double _ylowgiven;

    protected double _yhighgiven;

    protected double _xBottom = Double.MAX_VALUE;

    protected double _xTop = -Double.MAX_VALUE;

    protected double _yBottom = Double.MAX_VALUE;

    protected double _yTop = -Double.MAX_VALUE;

    protected boolean _xlog = false;

    protected boolean _ylog = false;

    protected static final double _LOG10SCALE = 1 / Math.log(10);

    protected boolean _grid = true;

    protected boolean _wrap = false;

    protected double _wrapHigh;

    protected double _wrapLow;

    protected Color _background = Color.white;

    protected Color _foreground = Color.black;

    protected int _topPadding = 10;

    protected int _bottomPadding = 5;

    protected int _rightPadding = 10;

    protected int _leftPadding = 10;

    protected int _ulx = 1;

    protected int _uly = 1;

    protected int _lrx = 100;

    protected int _lry = 100;

    protected Rectangle _specifiedPlotRectangle = null;

    protected double _yscale = 1.0;

    protected double _xscale = 1.0;

    protected volatile boolean _usecolor = true;

    static protected Color[] _colors = {
            new Color(0xff0000), // red
            new Color(0x0000ff), // blue
            new Color(0x00aaaa), // cyan-ish
            new Color(0x000000), // black
            new Color(0xffa500), // orange
            new Color(0x53868b), // cadetblue4
            new Color(0xff7f50), // coral
            new Color(0x45ab1f), // dark green-ish
            new Color(0x90422d), // sienna-ish
            new Color(0xa0a0a0), // grey-ish
            new Color(0x14ff14), // green-ish
    };

    protected int _width = 500;

    protected int _height = 300;

    protected int _preferredWidth = 500;

    protected int _preferredHeight = 300;

    public URL _documentBase = null;

    private int _drawLegend(Graphics graphics, int urx, int ury) {
        if (graphics == null) {
            return 0;
        }

        Font previousFont = graphics.getFont();
        graphics.setFont(_labelFont);

        int spacing = _labelFontMetrics.getHeight();

        Enumeration v = _legendStrings.elements();
        Enumeration i = _legendDatasets.elements();
        int ypos = ury + spacing;
        int maxwidth = 0;

        while (v.hasMoreElements()) {
            String legend = (String) v.nextElement();

            int dataset = ((Integer) i.nextElement()).intValue();

            if (dataset >= 0) {
                if (_usecolor) {
                    int color = dataset % _colors.length;
                    graphics.setColor(_colors[color]);
                }

                _drawPoint(graphics, dataset, urx - 3, ypos - 3, false);

                graphics.setColor(_foreground);

                int width = _labelFontMetrics.stringWidth(legend);

                if (width > maxwidth) {
                    maxwidth = width;
                }

                graphics.drawString(legend, urx - 15 - width, ypos);
                ypos += spacing;
            }
        }

        graphics.setFont(previousFont);
        return 22 + maxwidth;
    }

    private synchronized void _executeDeferredActions() {
        try {
            for (Runnable action : _deferredActions) {
                action.run();
            }
        } finally {
            _actionsDeferred = false;
            _deferredActions.clear();
        }
    }

    private String _formatLogNum(double num, int numfracdigits) {
        String results;
        int exponent = (int) num;

        if (exponent >= 0 && exponent < 10) {
            results = "0" + exponent;
        } else {
            if (exponent < 0 && exponent > -10) {
                results = "-0" + -exponent;
            } else {
                results = Integer.toString(exponent);
            }
        }

        if (num >= 0.0) {
            if (num - (int) num < 0.001) {
                results = "1e" + results;
            } else {
                results = _formatNum(Math.pow(10.0, num - (int) num),
                        numfracdigits);
            }
        } else {
            if (-num - (int) -num < 0.001) {
                results = "1e" + results;
            } else {
                results = _formatNum(Math.pow(10.0, num - (int) num) * 10,
                        numfracdigits);
            }
        }

        return results;
    }

    private String _formatNum(double num, int numfracdigits) {
        double fudge = 0.5;

        if (num < 0.0) {
            fudge = -0.5;
        }

        String numString = Double.toString(num + fudge
                * Math.pow(10.0, -numfracdigits));

        int dpt = numString.lastIndexOf(".");
        StringBuffer result = new StringBuffer();

        if (dpt < 0) {
            if (numfracdigits <= 0) {
                result.append(numString);
                return result.toString();
            }

            result.append(".");

            for (int i = 0; i < numfracdigits; i++) {
                result.append("0");
            }

            return result.toString();
        } else {
            int shortby = numfracdigits - (numString.length() - dpt - 1);

            if (shortby <= 0) {
                int numtocopy = dpt + numfracdigits + 1;

                if (numfracdigits == 0) {
                    numtocopy -= 1;
                }

                result.append(numString.substring(0, numtocopy));
                return result.toString();
            } else {
                result.append(numString);

                for (int i = 0; i < shortby; i++) {
                    result.append("0");
                }

                return result.toString();
            }
        }
    }

    private Vector _gridInit(double low, double step, boolean labeled,
            Vector oldgrid) {
        Vector grid = new Vector(10);
        double ratio = Math.pow(10.0, step);
        int ngrid = 1;

        if (labeled) {
            if (ratio <= 3.5) {
                if (ratio > 2.0) {
                    ngrid = 2;
                } else if (ratio > 1.26) {
                    ngrid = 5;
                } else if (ratio > 1.125) {
                    ngrid = 10;
                } else {
                    ngrid = (int) Math.rint(1.0 / step);
                }
            }
        } else {
            if (ratio > 10.0) {
                ngrid = 1;
            } else if (ratio > 3.0) {
                ngrid = 2;
            } else if (ratio > 2.0) {
                ngrid = 5;
            } else if (ratio > 1.125) {
                ngrid = 10;
            } else {
                ngrid = 100;
            }
        }

        int oldgridi = 0;

        for (int i = 0; i < ngrid; i++) {
            double gridval = i * 1.0 / ngrid * 10;
            double logval = _LOG10SCALE * Math.log(gridval);

            if (logval == Double.NEGATIVE_INFINITY) {
                logval = 0.0;
            }

            if (oldgrid != null && oldgridi < oldgrid.size()) {
                while (oldgridi < oldgrid.size()
                        && ((Double) oldgrid.elementAt(oldgridi)).doubleValue() < logval) {
                    oldgridi++;
                }

                if (oldgridi < oldgrid.size()) {
                    if (Math.abs(((Double) oldgrid.elementAt(oldgridi))
                            .doubleValue() - logval) > 0.00001) {
                        grid.addElement(Double.valueOf(logval));
                    }
                } else {
                    grid.addElement(Double.valueOf(logval));
                }
            } else {
                grid.addElement(Double.valueOf(logval));
            }
        }

        _gridCurJuke = 0;

        if (low == -0.0) {
            low = 0.0;
        }

        _gridBase = Math.floor(low);

        double x = low - _gridBase;

        for (_gridCurJuke = -1; _gridCurJuke + 1 < grid.size()
                && x >= ((Double) grid.elementAt(_gridCurJuke + 1))
                .doubleValue(); _gridCurJuke++) {
        }
        return grid;
    }

    private double _gridRoundUp(Vector grid, double pos) {
        double x = pos - Math.floor(pos);
        int i;

        for (i = 0; i < grid.size()
                && x >= ((Double) grid.elementAt(i)).doubleValue(); i++) {
        }

        if (i >= grid.size()) {
            return pos;
        } else {
            return Math.floor(pos) + ((Double) grid.elementAt(i)).doubleValue();
        }
    }

    private double _gridStep(Vector grid, double pos, double step,
            boolean logflag) {
        if (logflag) {
            if (++_gridCurJuke >= grid.size()) {
                _gridCurJuke = 0;
                _gridBase += Math.ceil(step);
            }

            if (_gridCurJuke >= grid.size()) {
                return pos + step;
            }

            return _gridBase
                    + ((Double) grid.elementAt(_gridCurJuke)).doubleValue();
        } else {
            return pos + step;
        }
    }

    private void _measureFonts() {
        if (_captionFont == null) {
            _captionFont = new Font("Helvetica", Font.PLAIN, 12);
        }

        if (_labelFont == null) {
            _labelFont = new Font("Helvetica", Font.PLAIN, 12);
        }

        if (_superscriptFont == null) {
            _superscriptFont = new Font("Helvetica", Font.PLAIN, 9);
        }

        if (_titleFont == null) {
            _titleFont = new Font("Helvetica", Font.BOLD, 14);
        }

        _captionFontMetrics = getFontMetrics(_captionFont);
        _labelFontMetrics = getFontMetrics(_labelFont);
        _superscriptFontMetrics = getFontMetrics(_superscriptFont);
        _titleFontMetrics = getFontMetrics(_titleFont);
    }

    private int _numFracDigits(double num) {
        int numdigits = 0;

        while (numdigits <= 15 && num != Math.floor(num)) {
            num *= 10.0;
            numdigits += 1;
        }

        return numdigits;
    }

    private int _numIntDigits(double num) {
        int numdigits = 0;

        while (numdigits <= 15 && (int) num != 0.0) {
            num /= 10.0;
            numdigits += 1;
        }

        return numdigits;
    }

    private void _parsePairs(String line, boolean xtick) {
        if (xtick) {
            _xticks = null;
            _xticklabels = null;
        } else {
            _yticks = null;
            _yticklabels = null;
        }

        int start = 0;
        boolean cont = true;

        while (cont) {
            int comma = line.indexOf(",", start);
            String pair = null;

            if (comma > start) {
                pair = line.substring(start, comma).trim();
            } else {
                pair = line.substring(start).trim();
                cont = false;
            }

            int close = -1;
            int open = 0;

            if (pair.startsWith("\"")) {
                close = pair.indexOf("\"", 1);
                open = 1;
            } else {
                close = pair.indexOf(" ");
            }

            if (close > 0) {
                String label = pair.substring(open, close);
                String index = pair.substring(close + 1).trim();

                try {
                    double idx = Double.valueOf(index).doubleValue();

                    if (xtick) {
                        addXTick(label, idx);
                    } else {
                        addYTick(label, idx);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning from PlotBox: "
                            + "Unable to parse ticks: " + e.getMessage());
                }
            }

            start = comma + 1;
            comma = line.indexOf(",", start);
        }
    }

    private RenderingHints _defaultImageRenderingHints() {
        RenderingHints hints = new RenderingHints(null);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return hints;
    }

    private double _roundUp(double val) {
        int exponent = (int) Math.floor(Math.log(val) * _LOG10SCALE);
        val *= Math.pow(10, -exponent);

        if (val > 5.0) {
            val = 10.0;
        } else if (val > 2.0) {
            val = 5.0;
        } else if (val > 1.0) {
            val = 2.0;
        }

        val *= Math.pow(10, exponent);
        return val;
    }

    private void _setXRange(double min, double max) {
        _plotImage = null;

        if (!_originalXRangeGiven) {
            _originalXlow = min;
            _originalXhigh = max;
            _originalXRangeGiven = true;
        }

        if (min > max) {
            min = -1.0;
            max = 1.0;
        } else if (min == max) {
            min -= 1.0;
            max += 1.0;
        }

        _xMin = min - (max - min) * _padding;
        _xMax = max + (max - min) * _padding;

        double largest = Math.max(Math.abs(_xMin), Math.abs(_xMax));
        _xExp = (int) Math.floor(Math.log(largest) * _LOG10SCALE);

        if (_xExp > 1 || _xExp < -1) {
            double xs = 1.0 / Math.pow(10.0, _xExp);
            _xtickMin = _xMin * xs;
            _xtickMax = _xMax * xs;
        } else {
            _xtickMin = _xMin;
            _xtickMax = _xMax;
            _xExp = 0;
        }
    }

    private void _setYRange(double min, double max) {
        _plotImage = null;

        if (!_originalYRangeGiven) {
            _originalYlow = min;
            _originalYhigh = max;
            _originalYRangeGiven = true;
        }

        if (min > max) {
            min = -1.0;
            max = 1.0;
        } else if (min == max) {
            min -= 0.1;
            max += 0.1;
        }

        _yMin = min - (max - min) * _padding;
        _yMax = max + (max - min) * _padding;

        double largest = Math.max(Math.abs(_yMin), Math.abs(_yMax));
        _yExp = (int) Math.floor(Math.log(largest) * _LOG10SCALE);

        if (_yExp > 1 || _yExp < -1) {
            double ys = 1.0 / Math.pow(10.0, _yExp);
            _ytickMin = _yMin * ys;
            _ytickMax = _yMax * ys;
        } else {
            _ytickMin = _yMin;
            _ytickMax = _yMax;
            _yExp = 0;
        }
    }

    void _zoom(int x, int y) {
        _zooming = false;

        Graphics graphics = getGraphics();

        if (graphics == null) {
            return;
        }

        if (_zoomin == true && _drawn == true) {
            if (_zoomxn != -1 || _zoomyn != -1) {
                int minx = Math.min(_zoomx, _zoomxn);
                int maxx = Math.max(_zoomx, _zoomxn);
                int miny = Math.min(_zoomy, _zoomyn);
                int maxy = Math.max(_zoomy, _zoomyn);
                graphics.setXORMode(_boxColor);
                graphics.drawRect(minx, miny, maxx - minx, maxy - miny);
                graphics.setPaintMode();

                if (y > _lry) {
                    y = _lry;
                }

                if (y < _uly) {
                    y = _uly;
                }

                if (x > _lrx) {
                    x = _lrx;
                }

                if (x < _ulx) {
                    x = _ulx;
                }

                if (Math.abs(_zoomx - x) > 5 && Math.abs(_zoomy - y) > 5) {
                    double a = _xMin + (_zoomx - _ulx) / _xscale;
                    double b = _xMin + (x - _ulx) / _xscale;

                    if (a < b) {
                        setXRange(a, b);
                    } else {
                        setXRange(b, a);
                    }

                    a = _yMax - (_zoomy - _uly) / _yscale;
                    b = _yMax - (y - _uly) / _yscale;

                    if (a < b) {
                        setYRange(a, b);
                    } else {
                        setYRange(b, a);
                    }
                }

                repaint();
            }
        } else if (_zoomout == true && _drawn == true) {
            graphics.setXORMode(_boxColor);

            int x_diff = Math.abs(_zoomx - _zoomxn);
            int y_diff = Math.abs(_zoomy - _zoomyn);
            graphics.drawRect(_zoomx - 15 - x_diff, _zoomy - 15 - y_diff,
                    30 + x_diff * 2, 30 + y_diff * 2);
            graphics.setPaintMode();

            double a = Math.abs(_zoomx - x) / 30.0;
            double b = Math.abs(_zoomy - y) / 30.0;
            double newx1 = _xMax + (_xMax - _xMin) * a;
            double newx2 = _xMin - (_xMax - _xMin) * a;

            double newy1 = _yMax + (_yMax - _yMin) * b;
            double newy2 = _yMin - (_yMax - _yMin) * b;

            zoom(newx2, newy2, newx1, newy1);
            repaint();
        } else if (_drawn == false) {
            repaint();
        }

        _drawn = false;
        _zoomin = _zoomout = false;
        _zoomxn = _zoomyn = _zoomx = _zoomy = -1;
    }

    void _zoomBox(int x, int y) {
        if (!_zooming) {
            return;
        }

        Graphics graphics = getGraphics();

        if (graphics == null) {
            return;
        }
        if (y > _lry) {
            y = _lry;
        }
        if (y < _uly) {
            y = _uly;
        }
        if (x > _lrx) {
            x = _lrx;
        }
        if (x < _ulx) {
            x = _ulx;
        }
        if (_zoomx != -1 || _zoomy != -1) {
            if (_zoomin == false && _zoomout == false) {
                if (y < _zoomy) {
                    _zoomout = true;
                    graphics.setXORMode(_boxColor);
                    graphics.drawRect(_zoomx - 15, _zoomy - 15, 30, 30);
                } else if (y > _zoomy) {
                    _zoomin = true;
                }
            }

            if (_zoomin == true) {
                if ((_zoomxn != -1 || _zoomyn != -1) && _drawn == true) {
                    int minx = Math.min(_zoomx, _zoomxn);
                    int maxx = Math.max(_zoomx, _zoomxn);
                    int miny = Math.min(_zoomy, _zoomyn);
                    int maxy = Math.max(_zoomy, _zoomyn);
                    graphics.setXORMode(_boxColor);
                    graphics.drawRect(minx, miny, maxx - minx, maxy - miny);
                }

                if (y > _zoomy) {
                    _zoomxn = x;
                    _zoomyn = y;

                    int minx = Math.min(_zoomx, _zoomxn);
                    int maxx = Math.max(_zoomx, _zoomxn);
                    int miny = Math.min(_zoomy, _zoomyn);
                    int maxy = Math.max(_zoomy, _zoomyn);
                    graphics.setXORMode(_boxColor);
                    graphics.drawRect(minx, miny, maxx - minx, maxy - miny);
                    _drawn = true;
                    return;
                } else {
                    _drawn = false;
                }
            } else if (_zoomout == true) {
                if ((_zoomxn != -1 || _zoomyn != -1) && _drawn == true) {
                    int x_diff = Math.abs(_zoomx - _zoomxn);
                    int y_diff = Math.abs(_zoomy - _zoomyn);
                    graphics.setXORMode(_boxColor);
                    graphics.drawRect(_zoomx - 15 - x_diff, _zoomy - 15
                            - y_diff, 30 + x_diff * 2, 30 + y_diff * 2);
                }

                if (y < _zoomy) {
                    _zoomxn = x;
                    _zoomyn = y;

                    int x_diff = Math.abs(_zoomx - _zoomxn);
                    int y_diff = Math.abs(_zoomy - _zoomyn);
                    graphics.setXORMode(_boxColor);
                    graphics.drawRect(_zoomx - 15 - x_diff, _zoomy - 15
                            - y_diff, 30 + x_diff * 2, 30 + y_diff * 2);
                    _drawn = true;
                    return;
                } else {
                    _drawn = false;
                }
            }
        }

        graphics.setPaintMode();
    }

    void _zoomStart(int x, int y) {
        if (y > _lry) {
            y = _lry;
        }

        if (y < _uly) {
            y = _uly;
        }

        if (x > _lrx) {
            x = _lrx;
        }

        if (x < _ulx) {
            x = _ulx;
        }

        _zoomx = x;
        _zoomy = y;
        _zooming = true;
    }

    private volatile boolean _actionsDeferred = false;

    private boolean _automaticRescale = false;

    private LinkedList<Runnable> _deferredActions = new LinkedList<Runnable>();

    private String _filespec = null;

    private static final Color _boxColor = Color.orange;

    private double _ytickMax = 0.0;

    private double _ytickMin = 0.0;

    private double _xtickMax = 0.0;

    private double _xtickMin = 0.0;

    private int _yExp = 0;

    private int _xExp = 0;

    private Font _captionFont = null;

    private Font _labelFont = null;

    private Font _superscriptFont = null;

    private Font _titleFont = null;

    private FontMetrics _captionFontMetrics = null;

    private FontMetrics _labelFontMetrics = null;

    private FontMetrics _superscriptFontMetrics = null;

    private FontMetrics _titleFontMetrics = null;

    private transient int _gridCurJuke = 0;

    private transient double _gridBase = 0.0;

    private transient String[] _errorMsg;

    private String _xlabel;

    private String _ylabel;

    private String _title;

    private Vector _captionStrings = new Vector();

    private Vector _legendStrings = new Vector();

    private Vector _legendDatasets = new Vector();

    private Vector _xticks = null;

    private Vector _xticklabels = null;

    private Vector _yticks = null;

    private Vector _yticklabels = null;

    private transient JButton _pulsations = null;

    private transient JButton _formatButton = null;

    boolean _originalXRangeGiven = false;

    boolean _originalYRangeGiven = false;

    double _originalXlow = 0.0;

    double _originalXhigh = 0.0;

    double _originalYlow = 0.0;

    double _originalYhigh = 0.0;

    private transient JButton _printButton = null;

    private transient JButton _resetButton = null;

    private boolean _timedRepaint = false;

    static private volatile TimedRepaint _timerTask = null;

    private transient int _zoomx = -1;

    private transient int _zoomy = -1;

    private transient int _zoomxn = -1;

    private transient int _zoomyn = -1;

    private transient boolean _zoomin = false;

    private transient boolean _zoomout = false;

    private transient boolean _drawn = false;

    private transient boolean _zooming = false;

    class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == _pulsations) {
                fillPlot();
            } else if (event.getSource() == _resetButton) {
                resetAxes(); fillPlot();
            } else if (event.getSource() == _formatButton) {
                PlotFormatter fmt = new PlotFormatter(PlotBox.this);
                fmt.openModal();
            }
        }
    }

    public class ZoomListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent event) {
            requestFocus();
        }

        @Override
        public void mouseEntered(MouseEvent event) {
        }

        @Override
        public void mouseExited(MouseEvent event) {
        }

        @Override
        public void mousePressed(MouseEvent event) {
            if ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                    && (event.getModifiers() & InputEvent.BUTTON3_MASK) == 0
                    || event.getModifiers() == 0) {
                PlotBox.this._zoomStart(event.getX(), event.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            if ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                    && (event.getModifiers() & InputEvent.BUTTON3_MASK) == 0
                    || event.getModifiers() == 0) {
                PlotBox.this._zoom(event.getX(), event.getY());
            }
        }
    }

    public class DragListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent event) {
            if ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                    && (event.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
                PlotBox.this._zoomBox(event.getX(), event.getY());
            }
        }

        @Override
        public void mouseMoved(MouseEvent event) {
        }
    }

    class CommandListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            int keycode = e.getKeyCode();

            switch (keycode) {
            case KeyEvent.VK_CONTROL:
                _control = true;
                break;

            case KeyEvent.VK_SHIFT:
                _shift = true;
                break;

            case KeyEvent.VK_C:

                if (_control) {
                    exportImage(null, "gif");

                    String message = "GIF image exported to clipboard.";
                    JOptionPane.showMessageDialog(PlotBox.this, message,
                            "Ptolemy Plot Message",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                break;

            case KeyEvent.VK_D:

                if (!_control && _shift) {
                    write(System.out);

                    String message = "Plot data sent to standard out.";
                    JOptionPane.showMessageDialog(PlotBox.this, message,
                            "Ptolemy Plot Message",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                if (_control) {
                    StringUtilities.exit(1);
                }

                break;

            case KeyEvent.VK_F:

                if (!_control && _shift) {
                    fillPlot();
                }

                break;

            case KeyEvent.VK_H:

                if (!_control && _shift) {
                    _help();
                }

                break;

            case KeyEvent.VK_Q:

                if (!_control) {
                    StringUtilities.exit(1);
                }

                break;

            case KeyEvent.VK_SLASH:

                if (_shift) {
                    _help();
                }

                break;

            default:
                break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keycode = e.getKeyCode();

            switch (keycode) {
            case KeyEvent.VK_CONTROL:
                _control = false;
                break;

            case KeyEvent.VK_SHIFT:
                _shift = false;
                break;

            default:
                break;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        private boolean _control = false;

        private boolean _shift = false;
    }

    private static class TimedRepaint extends Timer {
        static int _REPAINT_TIME_INTERVAL = 30;

        public synchronized void addListener(PlotBox plotBox) {
            _listeners.add(plotBox);
            if (_listeners.size() == 1) {
                scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            for (PlotBox plot : _listeners) {
                                if (plot._timedRepaint()) {
                                    plot._scheduledRedraw();
                                }
                            }
                        }
                    }
                }, 0, _REPAINT_TIME_INTERVAL);
            }
        }

        public synchronized void removeListener(PlotBox plotBox) {
            _listeners.remove(plotBox);
            if (_listeners.isEmpty()) {
                purge();
            }
        }

        private Set<PlotBox> _listeners = new HashSet<PlotBox>();
    }

    private static boolean _printedSecurityExceptionMessage = false;
}
