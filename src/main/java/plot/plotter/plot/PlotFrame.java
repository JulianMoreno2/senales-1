package plot.plotter.plot;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import plot.plotter.util.StringUtilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;

@SuppressWarnings("serial")
public class PlotFrame extends JFrame implements PropertyChangeListener, ImageExportable {

    PlotFrame() {
        this("Default title");
    }


    private PlotFrame(String title) {
        this(title, (PlotBox[]) null);
    }


    PlotFrame(String title, PlotBox plotArg) {
        super(title);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable ignored) {
        }

        plot = new PlotBox[1];
        if (plotArg == null) {
            plot[0] = new Plot();
        } else {
            plot[0] = plotArg;
        }

        plot[0].setBackground(new Color(0xe5e5e5));

        setPlotWindows();

        getContentPane().add(plot[0], BorderLayout.CENTER);

        setDimension();
    }

    PlotFrame(String title, PlotBox[] plotBoxes){

        super(title);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable ignored) {
        }

        if (plotBoxes == null) {
            plot = new PlotBox[2];
        } else {
            plot = plotBoxes;
        }

        plot[0].setBackground(new Color(0xe5e5e5));
        plot[1].setBackground(new Color(0xe5e5e5));

        setPlotWindows();

        getContentPane().add(plot[0], BorderLayout.CENTER);
        getContentPane().add(plot[1], BorderLayout.CENTER);

        setDimension();

    }

    private void setDimension() {
        setSize(1000, 700);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;
        setLocation(x, y);
    }

    private void setPlotWindows() {
        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem[] fileMenuItems = { new JMenuItem("Open", KeyEvent.VK_O),
                new JMenuItem("Save", KeyEvent.VK_S),
                new JMenuItem("SaveAs", KeyEvent.VK_A),
                new JMenuItem("Export", KeyEvent.VK_E),
                new JMenuItem("Close", KeyEvent.VK_C), };

        // Open button = ctrl-o.
        fileMenuItems[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Event.CTRL_MASK));

        // Save button = ctrl-s.
        fileMenuItems[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
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

        setJMenuBar(menubar);
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

    void samplePlot() {
        file = null;
        directory = null;
        plot[0].samplePlot();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        editMenu.setBackground(menubar.getBackground());
        fileMenu.setBackground(menubar.getBackground());
    }

    @Override
    public void writeImage(OutputStream stream, String format)
            throws IOException {
        if (plot == null) {
            throw new IOException("No plot to write image from!");
        }
        plot[0].exportImage(stream, format);
    }

    private final PlotBox[] plot;

    File directory = null;

    private final JMenu editMenu = new JMenu("Edit");

    File file = null;

    private final JMenu fileMenu = new JMenu("File");

    private final JMenuBar menubar = new JMenuBar();

    void close() {
        dispose();
    }

    private void editFormat() {
        PlotFormatter fmt = new PlotFormatter(plot[0]);
        fmt.openModal();
    }

    private void export() {
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
                        plot[0].exportLatex(file);
                    } else {
                        fout = new FileOutputStream(file);
                        plot[0].exportImage(fout, "gif");
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

    private void open() {
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
                plot[0].clear(true);
                input = new FileInputStream(file);
                read(new URL("file", null, directory.getAbsolutePath()),
                        input);
                plot[0].repaint();
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

    void read(URL base, InputStream in) throws IOException {
        plot[0].read(in);
    }

    private void save() {
        if (file != null) {
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(file);
                plot[0].write(output);
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

    private void saveAs() {
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
                switch (actionCommand) {
                    case "Open":
                        open();
                        break;
                    case "Save":
                        save();
                        break;
                    case "SaveAs":
                        saveAs();
                        break;
                    case "Export":
                        export();
                        break;
                    case "Close":
                        close();
                        break;
                }
            } catch (Throwable throwable) {
                JOptionPane.showMessageDialog(null, "File Menu Exception:\n"
                        + throwable, "Plot Error",
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

            return extension.equalsIgnoreCase(".eps");
        }

        @Override
        public String getDescription() {
            return "Encapsulated PostScript (.eps) files";
        }
    }

    static class FolderForLatex extends FileFilter {
        @Override
        public boolean accept(File fileOrDirectory) {
            return fileOrDirectory.isDirectory();
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

            return extension.equalsIgnoreCase(".gif");
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

            return extension.equalsIgnoreCase(".plt")
                    || extension.equalsIgnoreCase(".xml");
        }

        @Override
        public String getDescription() {
            return ".plt and .xml files";
        }
    }
}
