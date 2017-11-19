package plot.plotter.plot;

import java.io.IOException;
import java.io.OutputStream;

interface ImageExportable {
    void writeImage(OutputStream stream, String format) throws IOException;
}
