package plot.plotter.plot;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.ImageObserver;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import plot.plotter.util.StringUtilities;

class EPSGraphics extends Graphics {

    private EPSGraphics(OutputStream out, int width, int height) {
        _width = width;
        _height = height;
        _out = out;
        _buffer.append("%!PS-Adobe-3.0 EPSF-3.0\n");
        _buffer.append("%%Creator: UC Berkeley Plot Package\n");
        _buffer.append("%%BoundingBox: 50 50 ").append(50 + width).append(" ").append(50 + height).append("\n");
        _buffer.append("%%Pages: 1\n");
        _buffer.append("%%Page: 1 1\n");
        _buffer.append("%%LanguageLevel: 2\n");
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    @Override
    public Graphics create() {
        return new EPSGraphics(_out, _width, _height);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor,
            ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer) {
        return true;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        Point start = _convert(x1, y1);
        Point end = _convert(x2, y2);
        _buffer.append("newpath ").append(start.x).append(" ").append(start.y).append(" moveto\n");
        _buffer.append("").append(end.x).append(" ").append(end.y).append(" lineto\n");
        _buffer.append("stroke\n");
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        if (_polygon(xPoints, yPoints, nPoints)) {
            _buffer.append("closepath stroke\n");
        }
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        int radius = width / 2;
        Point center = _convert(x + radius, y + radius);
        _buffer.append("newpath ").append(center.x).append(" ").append(center.y).append(" ").append(radius).append(" 0 360 arc closepath stroke\n");
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        Point start = _convert(x, y);
        _buffer.append("newpath ").append(start.x).append(" ").append(start.y).append(" moveto\n");
        _buffer.append("0 ").append(-height).append(" rlineto\n");
        _buffer.append("").append(width).append(" 0 rlineto\n");
        _buffer.append("0 ").append(height).append(" rlineto\n");
        _buffer.append("").append(-width).append(" 0 rlineto\n");
        _buffer.append("closepath stroke\n");
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
    }

    @Override
    public void drawString(java.text.AttributedCharacterIterator iterator,
            int x, int y) {
        throw new RuntimeException(
                "Sorry, drawString(java.text.AttributedCharacterIterator, "
                        + "int , int) is not implemented in EPSGraphics");
    }

    @Override
    public void drawString(String str, int x, int y) {
        Point start = _convert(x, y);
        _buffer.append("").append(start.x).append(" ").append(start.y).append(" moveto\n");

        if (str.contains("(")) {
            str = StringUtilities.substitute(str, "(", "\\(");
        }

        if (str.contains(")") && !str.contains("\\)")) {
            str = StringUtilities.substitute(str, ")", "\\)");
        }

        _buffer.append("(").append(str).append(") show\n");
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        if (_polygon(xPoints, yPoints, nPoints)) {
            _buffer.append("closepath fill\n");
        }
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        int radius = width / 2;
        Point center = _convert(x + radius, y + radius);
        _buffer.append("newpath ").append(center.x).append(" ").append(center.y).append(" ").append(radius).append(" 0 360 arc closepath fill\n");
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        Point start = _convert(x, y);
        _fillPattern();
        _buffer.append("newpath ").append(start.x).append(" ").append(start.y).append(" moveto\n");
        _buffer.append("0 ").append(-height).append(" rlineto\n");
        _buffer.append("").append(width).append(" 0 rlineto\n");
        _buffer.append("0 ").append(height).append(" rlineto\n");
        _buffer.append("").append(-width).append(" 0 rlineto\n");
        _buffer.append("closepath gsave fill grestore\n");
        _buffer.append("0.5 setlinewidth 0 setgray [] 0 setdash stroke\n");

        _buffer.append("1 setlinewidth\n");
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
    }

    @Override
    public Shape getClip() {
        return null;
    }

    @Override
    public Rectangle getClipBounds() {
        return null;
    }

    @Override
    public Color getColor() {
        return _currentColor;
    }

    @Override
    public Font getFont() {
        return _currentFont;
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return null;
    }

    @Override
    public void setFont(Font font) {
        if (font == null) {
            return;
        }

        int size = font.getSize();
        boolean bold = font.isBold();

        if (bold) {
            _buffer.append("/Helvetica-Bold findfont\n");
        } else {
            _buffer.append("/Helvetica findfont\n");
        }

        _buffer.append("").append(size).append(" scalefont setfont\n");
        _currentFont = font;
    }

    @Override
    public void setClip(Shape clip) {
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
    }

    @Override
    public void setColor(Color c) {
        if (c == Color.black) {
            _buffer.append("0 setgray\n");
            _buffer.append("[] 0 setdash\n");
            _buffer.append("1 setlinewidth\n");
        } else if (c == Color.white) {
            _buffer.append("1 setgray\n");
            _buffer.append("[] 0 setdash\n");
            _buffer.append("1 setlinewidth\n");
        } else if (c == Color.lightGray) {
            _buffer.append("0.9 setgray\n");
            _buffer.append("[] 0 setdash\n");
            _buffer.append("0.5 setlinewidth\n");
        } else {
            if (_linepattern.containsKey(c)) {
                _buffer.append(_linepattern.get(c)).append(" 0 setdash\n");
                _buffer.append("1 setlinewidth\n");
            } else {
                _buffer.append("0 setgray\n");

                if (_patternIndex >= _patterns.length) {
                    _patternIndex = 0;
                }

                _buffer.append(_patterns[_patternIndex]).append(" 0 setdash\n");
                _buffer.append("1 setlinewidth\n");
                _linepattern.put(c, _patterns[_patternIndex]);
                _patternIndex++;
            }
        }

        _currentColor = c;
    }

    @Override
    public void setPaintMode() {
    }

    @Override
    public void setXORMode(Color c1) {
    }

    @Override
    public void translate(int x, int y) {
    }

    private Point _convert(int x, int y) {
        return new Point(x + 50, _height + 50 - y);
    }

    private boolean _polygon(int[] xPoints, int[] yPoints, int nPoints) {
        if (nPoints < 3 || xPoints.length < nPoints || yPoints.length < nPoints) {
            return false;
        }

        Point start = _convert(xPoints[0], yPoints[0]);
        _buffer.append("newpath ").append(start.x).append(" ").append(start.y).append(" moveto\n");

        for (int i = 1; i < nPoints; i++) {
            Point vertex = _convert(xPoints[i], yPoints[i]);
            _buffer.append("").append(vertex.x).append(" ").append(vertex.y).append(" lineto\n");
        }

        return true;
    }

    private void _fillPattern() {
        int red = _currentColor.getRed();
        int green = _currentColor.getGreen();
        int blue = _currentColor.getBlue();

        double bluescale = 0.6; // darkest
        double redscale = 0.8;
        double greenscale = 1.0; // lightest
        double fullscale = Math.sqrt(255.0 * 255.0 * (bluescale * bluescale
                + redscale * redscale + greenscale * greenscale));
        double graylevel = Math.sqrt(red * red * redscale * redscale + blue
                * blue * bluescale * bluescale + green * green * greenscale
                * greenscale)
                / fullscale;
        _buffer.append("").append(graylevel).append(" setgray\n");

        _buffer.append("%---- rgb: ").append(red).append(" ").append(green).append(" ").append(blue).append("\n");
    }

    private Color _currentColor = Color.black;

    private Font _currentFont;

    private final int _width;

    private final int _height;

    private final Hashtable _linepattern = new Hashtable();

    private final OutputStream _out;

    private final StringBuffer _buffer = new StringBuffer();

    private Clipboard _clipboard;

    static private final String[] _patterns = { "[]", "[1 1]", "[4 4]", "[4 4 1 4]",
        "[2 2]", "[4 2 1 2 1 2]", "[5 3 2 3]", "[3 3]", "[4 2 1 2 2 2]",
        "[1 2 5 2 1 2 1 2]", "[4 1 2 1]", };

    private int _patternIndex = 0;
}
