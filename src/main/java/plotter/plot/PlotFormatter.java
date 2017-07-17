package plotter.plot;

import plotter.gui.ComponentDialog;
import plotter.gui.Query;
import plotter.gui.QueryListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

@SuppressWarnings("serial")
public class PlotFormatter extends JPanel {

    public PlotFormatter(PlotBox plot) {
        super();
        _plot = plot;

        setLayout(new BorderLayout());
        _wideQuery = new Query();
        add(_wideQuery, BorderLayout.WEST);
        _narrowQuery = new Query();
        add(_narrowQuery, BorderLayout.EAST);

        _wideQuery.setTextWidth(20);
        _originalTitle = plot.getTitle();
        _wideQuery.addLine("title", "Title", _originalTitle);

        _originalCaptions = plot.getCaptions();
        StringBuffer captionsString = new StringBuffer();
        for (Enumeration captions = _originalCaptions.elements(); captions
                .hasMoreElements();) {
            if (captionsString.length() > 0) {
                captionsString.append('\n');
            }
            captionsString.append((String) captions.nextElement());
        }
        _wideQuery.addTextArea("caption", "Caption", captionsString.toString());

        _originalXLabel = plot.getXLabel();
        _wideQuery.addLine("xlabel", "X Label", _originalXLabel);
        _originalYLabel = plot.getYLabel();
        _wideQuery.addLine("ylabel", "Y Label", _originalYLabel);
        _originalXRange = plot.getXRange();
        _wideQuery.addLine("xrange", "X Range", "" + _originalXRange[0] + ", "
                + _originalXRange[1]);
        _originalYRange = plot.getYRange();
        _wideQuery.addLine("yrange", "Y Range", "" + _originalYRange[0] + ", "
                + _originalYRange[1]);

        String[] marks = { "none", "points", "dots", "various", "bigdots",
        "pixels" };
        _originalMarks = "none";

        if (plot instanceof Plot) {
            _originalMarks = ((Plot) plot).getMarksStyle();
            _wideQuery.addRadioButtons("marks", "Marks", marks, _originalMarks);
        }

        _originalXTicks = plot.getXTicks();
        _originalXTicksSpec = "";

        if (_originalXTicks != null) {
            StringBuffer buffer = new StringBuffer();
            Vector positions = _originalXTicks[0];
            Vector labels = _originalXTicks[1];

            for (int i = 0; i < labels.size(); i++) {
                if (buffer.length() > 0) {
                    buffer.append(", ");
                }

                buffer.append(labels.elementAt(i).toString());
                buffer.append(" ");
                buffer.append(positions.elementAt(i).toString());
            }

            _originalXTicksSpec = buffer.toString();
        }

        _wideQuery.addLine("xticks", "X Ticks", _originalXTicksSpec);

        _originalYTicks = plot.getYTicks();
        _originalYTicksSpec = "";

        if (_originalYTicks != null) {
            StringBuffer buffer = new StringBuffer();
            Vector positions = _originalYTicks[0];
            Vector labels = _originalYTicks[1];

            for (int i = 0; i < labels.size(); i++) {
                if (buffer.length() > 0) {
                    buffer.append(", ");
                }

                buffer.append(labels.elementAt(i).toString());
                buffer.append(" ");
                buffer.append(positions.elementAt(i).toString());
            }

            _originalYTicksSpec = buffer.toString();
        }

        _wideQuery.addLine("yticks", "Y Ticks", _originalYTicksSpec);

        _originalGrid = plot.getGrid();
        _narrowQuery.addCheckBox("grid", "Grid", _originalGrid);
        _originalStems = false;
        _originalConnected = null;

        if (plot instanceof Plot) {
            _originalStems = ((Plot) plot).getImpulses();
            _narrowQuery.addCheckBox("stems", "Stems", _originalStems);
            _saveConnected();
            _narrowQuery.addCheckBox("connected", "Connect",
                    ((Plot) plot).getConnected());
        }

        _originalColor = plot.getColor();
        _narrowQuery.addCheckBox("color", "Use Color", _originalColor);

        if (plot instanceof Plot) {
            _originalLineStyles = ((Plot) plot).getLineStyles();
            _narrowQuery.addCheckBox("lineStyles", "Use Line Styles",
                    _originalLineStyles);
        }
        _wideQuery.addQueryListener(new QueryListener() {
            @Override
            public void changed(String name) {
                if (name.equals("title")) {
                    _plot.setTitle(_wideQuery.getStringValue("title"));
                } else if (name.equals("caption")) {
                    _plot.clearCaptions();
                    String newCaption = _wideQuery.getStringValue("caption");
                    String[] captionsArray = newCaption.split("\\n");
                    for (String element : captionsArray) {
                        _plot.read("captions: " + element);
                    }
                } else if (name.equals("xlabel")) {
                    _plot.setXLabel(_wideQuery.getStringValue("xlabel"));
                } else if (name.equals("ylabel")) {
                    _plot.setYLabel(_wideQuery.getStringValue("ylabel"));
                } else if (name.equals("xrange")) {
                    _plot.read("XRange: " + _wideQuery.getStringValue("xrange"));
                } else if (name.equals("xticks")) {
                    String spec = _wideQuery.getStringValue("xticks").trim();
                    _plot.read("XTicks: " + spec);

                } else if (name.equals("yticks")) {
                    String spec = _wideQuery.getStringValue("yticks").trim();
                    _plot.read("YTicks: " + spec);

                } else if (name.equals("yrange")) {
                    _plot.read("YRange: " + _wideQuery.getStringValue("yrange"));
                } else if (name.equals("marks")) {
                    ((Plot) _plot).setMarksStyle(_wideQuery
                            .getStringValue("marks"));
                }

                _plot.repaint();
            }
        });

        _narrowQuery.addQueryListener(name -> {
            if (name.equals("grid")) {
                _plot.setGrid(_narrowQuery.getBooleanValue("grid"));
            } else if (name.equals("stems")) {
                ((Plot) _plot).setImpulses(_narrowQuery
                        .getBooleanValue("stems"));
                _plot.repaint();
            } else if (name.equals("color")) {
                _plot.setColor(_narrowQuery.getBooleanValue("color"));

            } else if (name.equals("connected")) {
                _setConnected(_narrowQuery.getBooleanValue("connected"));
            } else if (name.equals("lineStyles")) {
                ((Plot) _plot).setLineStyles(_narrowQuery
                        .getBooleanValue("lineStyles"));
            }

            _plot.repaint();
        });
    }

    public void apply() {
        _plot.setTitle(_wideQuery.getStringValue("title"));
        _plot.setXLabel(_wideQuery.getStringValue("xlabel"));
        _plot.setYLabel(_wideQuery.getStringValue("ylabel"));
        _plot.read("XRange: " + _wideQuery.getStringValue("xrange"));
        _plot.read("YRange: " + _wideQuery.getStringValue("yrange"));
        _plot.setGrid(_narrowQuery.getBooleanValue("grid"));
        _plot.setColor(_narrowQuery.getBooleanValue("color"));
        if (_plot instanceof Plot) {
            Plot cplot = (Plot) _plot;
            cplot.setLineStyles(_narrowQuery.getBooleanValue("lineStyles"));
            cplot.setMarksStyle(_wideQuery.getStringValue("marks"));
            cplot.setImpulses(_narrowQuery.getBooleanValue("stems"));
            _setConnected(_narrowQuery.getBooleanValue("connected"));
        }

        _plot.repaint();
    }

    public void openModal() {
        String[] buttons = { "Apply", "Cancel" };

        Container toplevel = _plot.getTopLevelAncestor();
        Frame frame = null;

        if (toplevel instanceof Frame) {
            frame = (Frame) toplevel;
        }

        ComponentDialog dialog = new ComponentDialog(frame, "Set plot format",
                this, buttons);

        if (dialog.buttonPressed().equals("Apply")) {
            apply();
        } else {
            restore();
        }
    }

    public void restore() {
        _plot.setTitle(_originalTitle);
        _plot.setCaptions(_originalCaptions);
        _plot.setXLabel(_originalXLabel);
        _plot.setYLabel(_originalYLabel);
        _plot.setXRange(_originalXRange[0], _originalXRange[1]);
        _plot.setYRange(_originalYRange[0], _originalYRange[1]);
        _plot.setGrid(_originalGrid);
        _plot.setColor(_originalColor);
        if (_plot instanceof Plot) {
            Plot cplot = (Plot) _plot;
            cplot.setLineStyles(_originalLineStyles);
            cplot.setMarksStyle(_originalMarks);
            cplot.setImpulses(_originalStems);
            _restoreConnected();
        }
        _plot.repaint();
    }

    protected final PlotBox _plot;

    private void _saveConnected() {
        ArrayList<ArrayList<PlotPoint>> points = ((Plot) _plot)._points;
        _originalConnected = new boolean[points.size()][];
        _originalPoints = new PlotPoint[points.size()][];

        for (int dataset = 0; dataset < points.size(); dataset++) {
            ArrayList<PlotPoint> pts = points.get(dataset);
            _originalConnected[dataset] = new boolean[pts.size()];
            _originalPoints[dataset] = new PlotPoint[pts.size()];

            for (int i = 0; i < pts.size(); i++) {
                PlotPoint pt = pts.get(i);
                _originalConnected[dataset][i] = pt.connected;
                _originalPoints[dataset][i] = pt;
            }
        }
    }

    private void _setConnected(boolean value) {
        ((Plot) _plot).setConnected(value);
    }

    private void _restoreConnected() {
        for (int dataset = 0; dataset < _originalPoints.length; dataset++) {
            for (int i = 0; i < _originalPoints[dataset].length; i++) {
                PlotPoint pt = _originalPoints[dataset][i];
                pt.connected = _originalConnected[dataset][i];
            }
        }
    }

    private Query _wideQuery;

    private Query _narrowQuery;

    private String _originalTitle;

    private Vector _originalCaptions;

    private String _originalXLabel;

    private String _originalYLabel;

    private String _originalMarks;

    private String _originalXTicksSpec;

    private String _originalYTicksSpec;

    private double[] _originalXRange;

    private double[] _originalYRange;

    private Vector[] _originalXTicks;

    private Vector[] _originalYTicks;

    private boolean _originalGrid;

    private boolean _originalLineStyles;

    private boolean _originalStems;

    private boolean _originalColor;

    private boolean[][] _originalConnected;

    private PlotPoint[][] _originalPoints;
}
