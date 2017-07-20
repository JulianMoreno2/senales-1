package plot.plotter.plot;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.OutputStream;

public interface ImageExportable {
    void writeImage(OutputStream stream, String format) throws PrinterException, IOException;
}
