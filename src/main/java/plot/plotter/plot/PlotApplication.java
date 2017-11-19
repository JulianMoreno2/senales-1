package plot.plotter.plot;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import plot.plotter.util.StringUtilities;

@SuppressWarnings("serial")
public class PlotApplication extends PlotFrame {

    public PlotApplication() throws Exception {
        this(new String[0]);
    }

    public PlotApplication(String[] args) throws Exception {
        this(new Plot(), args);
    }

    public PlotApplication(PlotBox plot, String[] args) throws Exception {

        super("PlotApplication", plot);

        addWindowListener(new WindowClosingAdapter());

        parseArgs(args);

        if (args == null || args.length == 0) {
            samplePlot();
        }

        setVisible(true);
    }

    public PlotApplication(PlotBox plot) {

        super("PlotApplication", plot);
        addWindowListener(new WindowClosingAdapter());

        plot.fillPlot();
        plot.setYLabel("frequency");

        setVisible(true);
    }

    public static void main(final String[] args) {
        try {
            Runnable doActions = () -> {
                try {
                    new PlotApplication(new Plot(), args);
                } catch (Exception ex) {
                    System.err.println(ex.toString());
                    ex.printStackTrace();
                }
            };

            SwingUtilities.invokeAndWait(doActions);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if (test) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            StringUtilities.exit(0);
        }
    }

    public PlotApplication(PlotBox[] plots) {

        super("PlotApplication", plots);
        addWindowListener(new WindowClosingAdapter());

        setVisible(true);
    }

    /**
     * Exit the application.
     */
    @Override
    protected void close() {
        StringUtilities.exit(0);
    }

    protected int parseArgs(String[] args) throws Exception {
        int i = 0;
        int argumentsRead;
        String arg;
        String title = "Ptolemy plot";

        int width = 500; // Default width of the graph
        int height = 300; // Default height of the graph

        while (args != null && i < args.length) {
            arg = args[i++];

            if (arg.equals("-height")) {
                if (i > args.length - 1) {
                    throw new CmdLineArgException(usage());
                }

                height = Integer.parseInt(args[i++]);
                continue;
            } else if (arg.equals("-help")) {
                System.out.println(usage());
                StringUtilities.exit(0);
                continue;
            } else if (arg.equals("-printPDF")) {
                printPDF = true;
                continue;
            } else if (arg.equals("-test")) {
                test = true;
                continue;
            } else if (arg.equals("-version")) {
                System.out.println("Version " + PlotBox.PTPLOT_RELEASE
                        + ", Build $Id: PlotApplication.java 70402 2014-10-23 00:52:20Z cxh $");
                StringUtilities.exit(0);
                continue;
            } else if (arg.equals("-width")) {
                if (i > args.length - 1) {
                    throw new CmdLineArgException("-width requires an integer argument");
                }

                width = Integer.parseInt(args[i++]);
                continue;
            } else if (arg.equals("")) {
            } else if (arg.equals("-")) {
                URL base = new URL("file", null, "standard input");
                read(base, System.in);
            } else if (!arg.startsWith("-")) {
                InputStream instream;
                URL base;

                try {
                    URL inurl = new URL(null, arg);
                    base = inurl;
                    instream = inurl.openStream();
                } catch (MalformedURLException ex) {
                    File file = new File(arg);
                    instream = new FileInputStream(file);
                    this.file = new File(file.getAbsolutePath());
                    title = this.file.getName();
                    directory = this.file.getParentFile();
                    base = new URL("file", null, directory.getAbsolutePath());
                }

                read(base, instream);
            } else {
                throw new CmdLineArgException("Unrecognized option: " + arg);
            }
        }

        setSize(width, height);
        setTitle(title);

        argumentsRead = i;

        return argumentsRead;
    }

    protected String usage() {
        // We use a table here to keep things neat.
        // If we have:
        // {"-bd", "<color>", "Border", "White", "(Unsupported)"},
        // -bd - The argument
        // <color> - The description of the value of the argument
        // Border - The Xgraph file directive (not supported at this time).
        // White - The default (not supported at this time)
        // "(Unsupported)" - The string that is printed to indicate if
        // a option is unsupported.
        String[][] commandOptions = { { "-height", "<pixels>" }, { "-width", "<pixels>" }, };

        String[] commandFlags = { "-help", "-printPDF", "-test", "-version", "-", };
        StringBuffer result = new StringBuffer(
                "Usage: ptplot [ options ] [file ...]\n\n" + "Options that take values:\n");

        int i;

        for (i = 0; i < commandOptions.length; i++) {
            result.append(" " + commandOptions[i][0] + " " + commandOptions[i][1] + "\n");
        }

        result.append("\nBoolean flags:\n");

        for (i = 0; i < commandFlags.length; i++) {
            result.append(" " + commandFlags[i]);
        }

        return result.toString();
    }

    /**
     * If true, then print to PDF.
     */
    protected static boolean printPDF = false;

    /**
     * If true, then auto exit after a few seconds.
     */
    protected static boolean test = false;

    private static class WindowClosingAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            StringUtilities.exit(0);
        }
    }
}
