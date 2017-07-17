package plotter.plot;

import plotter.util.StringUtilities;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;

@SuppressWarnings("serial")
public class PlotFrame extends JFrame implements PropertyChangeListener, ImageExportable {

    public PlotFrame() {
        this("Ptolemy Plot Frame");
    }


    public PlotFrame(String title) {
        this(title, null);
    }


    public PlotFrame(String title, PlotBox plotArg) {
        super(title);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable throwable) {
        }

        if (plotArg == null) {
            plot = new Plot();
        } else {
            plot = plotArg;
        }

        plot.setBackground(new Color(0xe5e5e5));

        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);
        specialMenu.setMnemonic(KeyEvent.VK_S);

        JMenuItem[] fileMenuItems = { new JMenuItem("Open", KeyEvent.VK_O),
                new JMenuItem("Save", KeyEvent.VK_S),
                new JMenuItem("SaveAs", KeyEvent.VK_A),
                new JMenuItem("Export", KeyEvent.VK_E),
                new JMenuItem("Print", KeyEvent.VK_P),
                new JMenuItem("Close", KeyEvent.VK_C), };

        // Open button = ctrl-o.
        fileMenuItems[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Event.CTRL_MASK));

        // Save button = ctrl-s.
        fileMenuItems[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Event.CTRL_MASK));

        // Print button = ctrl-p.
        fileMenuItems[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                Event.CTRL_MASK));

        // Close button = ctrl-w.
        fileMenuItems[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                Event.CTRL_MASK));

        FileMenuListener fml = new FileMenuListener();

        for (JMenuItem fileMenuItem : fileMenuItems) {
            fileMenuItem.setActionCommand(fileMenuItem.getText());
            fileMenuItem.addActionListener(fml);
            fileMenu.add(fileMenuItem);
        }

        menubar.add(fileMenu);

        JMenuItem format = new JMenuItem("Format", KeyEvent.VK_F);
        FormatListener formatListener = new FormatListener();
        format.addActionListener(formatListener);
        editMenu.add(format);
        menubar.add(editMenu);

        JMenuItem[] specialMenuItems = { new JMenuItem("About", KeyEvent.VK_A),
                new JMenuItem("Help", KeyEvent.VK_H),
                new JMenuItem("Clear", KeyEvent.VK_C),
                new JMenuItem("Fill", KeyEvent.VK_F),
                new JMenuItem("Reset axes", KeyEvent.VK_R),
                new JMenuItem("Sample plot", KeyEvent.VK_S), };
        SpecialMenuListener sml = new SpecialMenuListener();

        for (JMenuItem specialMenuItem : specialMenuItems) {
            specialMenuItem.setActionCommand(specialMenuItem.getText());
            specialMenuItem.addActionListener(sml);
            specialMenu.add(specialMenuItem);
        }

        menubar.add(specialMenu);

        setJMenuBar(menubar);

        getContentPane().add(plot, BorderLayout.CENTER);

        setSize(500, 300);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        setLocation(x, y);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        Object source = event.getSource();
        if (source instanceof JFileChooser) {
            FileFilter filter = ((JFileChooser) source).getFileFilter();
            JFileChooser fileDialog = (JFileChooser) source;
            if (filter instanceof EPSFileFilter) {
                fileDialog.setSelectedFile(new File(fileDialog
                        .getCurrentDirectory(), "plot.eps"));
            } else if (filter instanceof FilterForGIF) {
                fileDialog.setSelectedFile(new File(fileDialog
                        .getCurrentDirectory(), "plot.gif"));
            } else {
                fileDialog.setSelectedFile(new File(fileDialog
                        .getCurrentDirectory(), "plot"));
            }
        }
    }

    public void samplePlot() {
        file = null;
        directory = null;
        plot.samplePlot();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        editMenu.setBackground(menubar.getBackground());
        fileMenu.setBackground(menubar.getBackground());
        specialMenu.setBackground(menubar.getBackground());
    }

    @Override
    public void writeImage(OutputStream stream, String format)
            throws PrinterException, IOException {
        if (plot == null) {
            throw new IOException("No plot to write image from!");
        }
        plot.exportImage(stream, format);
    }

    public PlotBox plot;

    protected File directory = null;

    protected JMenu editMenu = new JMenu("Edit");

    protected File file = null;

    protected JMenu fileMenu = new JMenu("File");

    protected JMenuBar menubar = new JMenuBar();

    protected JMenu specialMenu = new JMenu("Special");

    protected void about() {
        JOptionPane
        .showMessageDialog(
                this,
                "PlotFrame class\n"
                        + "Only Academic Uses\n",
                        "About Ptolemy Plot", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void close() {
        dispose();
    }

    protected void editFormat() {
        PlotFormatter fmt = new PlotFormatter(plot);
        fmt.openModal();
    }

    protected void export() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.addChoosableFileFilter(new FolderForLatex());
        fileDialog.addChoosableFileFilter(new EPSFileFilter());
        fileDialog.addChoosableFileFilter(new FilterForGIF());
        fileDialog.setDialogTitle("Export to...");

        if (directory != null) {
            fileDialog.setCurrentDirectory(directory);
        } else {
            String cwd = StringUtilities.getProperty("user.dir");

            if (cwd != null) {
                fileDialog.setCurrentDirectory(new File(cwd));
            }
        }

        fileDialog.setSelectedFile(new File(fileDialog.getCurrentDirectory(),
                "plot.gif"));

        fileDialog.addPropertyChangeListener(
                JFileChooser.FILE_FILTER_CHANGED_PROPERTY, this);

        int returnVal = fileDialog.showDialog(this, "Export");

        fileDialog.removePropertyChangeListener(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileDialog.getSelectedFile();
            try {
                FileOutputStream fout = null;

                try {
                    String name = file.getName();
                    int position = name.lastIndexOf('.');
                    String extension = "";
                    if (position > 0) {
                        extension = name.substring(position + 1);
                    }
                    if (extension.equals("")) {
                        plot.exportLatex(file);
                    } else if (extension.equalsIgnoreCase("eps")) {
                        fout = new FileOutputStream(file);
                        plot.export(fout);
                    } else {
                        fout = new FileOutputStream(file);
                        plot.exportImage(fout, "gif");
                    }
                } finally {
                    try {
                        if (fout != null) {
                            fout.close();
                        }
                    } catch (Throwable throwable) {
                        System.out.println("Ignoring failure to close stream "
                                + "on " + file);
                        throwable.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting plot: "
                        + ex, "Ptolemy II Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    protected void help() {
        JOptionPane.showMessageDialog(this,
                "PlotFrame is a plot in a top-level window.\n"
                        + "  File formats understood: Ptplot ASCII.\n"
                        + "  Left mouse button: Zooming.",
                        "About Ptolemy Plot", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void open() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle("Select a plot file");

        fileDialog.addChoosableFileFilter(new PLTOrXMLFileFilter());

        if (directory != null) {
            fileDialog.setCurrentDirectory(directory);
        } else {
            String cwd = StringUtilities.getProperty("user.dir");

            if (cwd != null) {
                fileDialog.setCurrentDirectory(new File(cwd));
            }
        }

        int returnVal = fileDialog.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileDialog.getSelectedFile();
            setTitle(file.getName());
            directory = fileDialog.getCurrentDirectory();

            FileInputStream input = null;
            try {
                plot.clear(true);
                input = new FileInputStream(file);
                read(new URL("file", null, directory.getAbsolutePath()),
                        input);
                plot.repaint();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "File not found:\n" + ex.toString(),
                        "Ptolemy Plot Error", JOptionPane.WARNING_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading input:\n"
                        + ex.toString(), "Ptolemy Plot Error",
                        JOptionPane.WARNING_MESSAGE);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    protected void print() {
        // If you are using $PTII/bin/vergil, under bash, set this property:
        // export JAVAFLAGS=-Dptolemy.ptII.print.platform=CrossPlatform
        // and then run $PTII/bin/vergil
        if (StringUtilities.getProperty("ptolemy.ptII.print.platform").equals(
                "CrossPlatform")) {
            printCrossPlatform();
        } else {
            printNative();
        }
    }

    protected void printCrossPlatform() {
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        job.setPrintable(plot);

        if (job.printDialog(aset)) {
            try {
                job.print(aset);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Printing failed:\n" + ex.toString(), "Print Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    protected void printPDF() throws PrinterException {
        boolean foundPDFPrinter = false;

        PrintService pdfPrintService = null;
        PrintService printServices[] = PrinterJob.lookupPrintServices();
        for (PrintService printService : printServices) {
            if (printService.getName().indexOf("PDF") != -1) {
                foundPDFPrinter = true;
                pdfPrintService = printService;
            }
        }

        if (pdfPrintService == null) {
            throw new PrinterException("Could not find a printer with the "
                    + "string \"PDF\" in its name.");
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(pdfPrintService);
        job.setPrintable(plot, job.defaultPage());

        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        Destination destination = new Destination(new File("plot.pdf").toURI());
        aset.add(destination);

        job.print(aset);
        if (foundPDFPrinter) {
            System.out
            .println("Plot printed from command line. "
                    + "Under MacOSX, look for "
                    + "~/Desktop/Java Printing.pdf");
        }
    }

    protected void printNative() {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pageFormat = job.pageDialog(job.defaultPage());
        job.setPrintable(plot, pageFormat);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Printing failed:\n" + ex.toString(), "Print Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    protected void read(URL base, InputStream in) throws IOException {
        plot.read(in);
    }

    protected void save() {
        if (file != null) {
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(file);
                plot.write(output);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error writing file:\n"
                        + ex.toString(), "Ptolemy Plot Error",
                        JOptionPane.WARNING_MESSAGE);
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            saveAs();
        }
    }

    protected void saveAs() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.addChoosableFileFilter(new PLTOrXMLFileFilter());
        fileDialog.setDialogTitle("Save plot as...");

        if (directory != null) {
            fileDialog.setCurrentDirectory(directory);
        } else {
            String cwd = StringUtilities.getProperty("user.dir");

            if (cwd != null) {
                fileDialog.setCurrentDirectory(new File(cwd));
            }
        }

        fileDialog.setSelectedFile(new File(fileDialog.getCurrentDirectory(),
                "plot.xml"));

        int returnVal = fileDialog.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileDialog.getSelectedFile();
            setTitle(file.getName());
            directory = fileDialog.getCurrentDirectory();
            save();
        }
    }

    class FileMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem target = (JMenuItem) e.getSource();
            String actionCommand = target.getActionCommand();

            try {
                if (actionCommand.equals("Open")) {
                    open();
                } else if (actionCommand.equals("Save")) {
                    save();
                } else if (actionCommand.equals("SaveAs")) {
                    saveAs();
                } else if (actionCommand.equals("Export")) {
                    export();
                } else if (actionCommand.equals("Print")) {
                    print();
                } else if (actionCommand.equals("Close")) {
                    close();
                }
            } catch (Throwable throwable) {
                JOptionPane.showMessageDialog(null, "File Menu Exception:\n"
                        + throwable, "Ptolemy Plot Error",
                        JOptionPane.WARNING_MESSAGE);
            }
            repaint();
        }
    }

    class FormatListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                editFormat();
            } catch (Exception exception) {
                System.out.println("Format Exception: " + exception);
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Format Exception:\n"
                        + exception.toString(), "Ptolemy Plot Error",
                        JOptionPane.WARNING_MESSAGE);

            }

            repaint();
        }
    }

    class SpecialMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem target = (JMenuItem) e.getSource();
            String actionCommand = target.getActionCommand();

            try {
                if (actionCommand.equals("About")) {
                    about();
                } else if (actionCommand.equals("Help")) {
                    help();
                } else if (actionCommand.equals("Fill")) {
                    plot.fillPlot();
                } else if (actionCommand.equals("Reset axes")) {
                    plot.resetAxes();
                } else if (actionCommand.equals("Clear")) {
                    plot.clear(false);
                    plot.repaint();
                } else if (actionCommand.equals("Sample plot")) {
                    plot.clear(true);
                    samplePlot();
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, "Special Menu Exception:\n"
                        + exception.toString(), "Ptolemy Plot Error",
                        JOptionPane.WARNING_MESSAGE);
            }

            repaint();
        }
    }

    static class EPSFileFilter extends FileFilter {
        @Override
        public boolean accept(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory()) {
                return true;
            }

            String fileOrDirectoryName = fileOrDirectory.getName();
            int dotIndex = fileOrDirectoryName.lastIndexOf('.');

            if (dotIndex == -1) {
                return false;
            }

            String extension = fileOrDirectoryName.substring(dotIndex);

            if (extension.equalsIgnoreCase(".eps")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return "Encapsulated PostScript (.eps) files";
        }
    }

    static class FolderForLatex extends FileFilter {
        @Override
        public boolean accept(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory()) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Latex Export to a Folder";
        }
    }

    static class FilterForGIF extends FileFilter {
        @Override
        public boolean accept(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory()) {
                return true;
            }

            String fileOrDirectoryName = fileOrDirectory.getName();
            int dotIndex = fileOrDirectoryName.lastIndexOf('.');

            if (dotIndex < 0) {
                return false;
            }

            String extension = fileOrDirectoryName.substring(dotIndex);

            if (extension.equalsIgnoreCase(".gif")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return "GIF Image File (.gif)";
        }
    }

    static class PLTOrXMLFileFilter extends FileFilter {
        @Override
        public boolean accept(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory()) {
                return true;
            }

            String fileOrDirectoryName = fileOrDirectory.getName();
            int dotIndex = fileOrDirectoryName.lastIndexOf('.');

            if (dotIndex == -1) {
                return false;
            }

            String extension = fileOrDirectoryName.substring(dotIndex);

            if (extension.equalsIgnoreCase(".plt")
                    || extension.equalsIgnoreCase(".xml")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return ".plt and .xml files";
        }
    }
}
