package plot.plotter.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class Query extends JPanel {

    public Query() {
        _grid = new GridBagLayout();
        _constraints = new GridBagConstraints();
        _constraints.fill = GridBagConstraints.HORIZONTAL;

        _constraints.weightx = 1.0;
        _constraints.anchor = GridBagConstraints.NORTHWEST;
        _entryPanel.setLayout(_grid);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        _entryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        messageArea = new JTextArea("");
        messageArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        messageArea.setBackground(null);

        messageArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        _messageScrollPane = new JScrollPane(messageArea);
        _messageScrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        _messageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        _messageScrollPane.getViewport().setBackground(null);

        _entryScrollPane = new JScrollPane(_entryPanel);

        _entryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        _entryScrollPane.getViewport().setBackground(null);
        _entryScrollPane.setBackground(null);
        add(_entryScrollPane);

        _entryPanel.setBackground(null);
    }

    public void addCheckBox(String name, String label, boolean defaultValue) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        JCheckBox checkbox = new JCheckBox();
        checkbox.setBackground(_background);
        checkbox.setOpaque(false);
        checkbox.setSelected(defaultValue);
        _addPair(name, lbl, checkbox, checkbox);

        checkbox.addItemListener(new QueryItemListener(this, name));

    }

    private void addChoice(String name, String label, Object[] values,
            Object defaultChoice, boolean editable, final Color background,
            final Color foreground) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        JComboBox combobox = new JComboBox(values);
        combobox.setEditable(editable);

        combobox.setEditor(new BasicComboBoxEditor() {
            public Component getEditorComponent() {
                Component result = super.getEditorComponent();
                result.setBackground(background);
                result.setForeground(foreground);
                return result;
            }
        });
        combobox.setSelectedItem(defaultChoice);
        _addPair(name, lbl, combobox, combobox);

        combobox.addItemListener(new QueryItemListener(this, name));

    }

    public void addLine(String name, String label, String defaultValue) {
        addLine(name, label, defaultValue, Color.white, Color.black);
    }

    private void addLine(String name, String label, String defaultValue,
            Color background, Color foreground) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        JTextField entryBox = new JTextField(defaultValue, _width);
        entryBox.setBackground(background);
        entryBox.setForeground(foreground);
        _addPair(name, lbl, entryBox, entryBox);

        entryBox.addActionListener(new QueryActionListener(this, name));

        entryBox.addFocusListener(new QueryFocusListener(this, name));
    }

    public void addQueryListener(QueryListener listener) {
        if (_listeners == null) {
            _listeners = new Vector();
        }

        if (_listeners.contains(listener)) {
            return;
        }

        _listeners.add(listener);
    }

    public void addRadioButtons(String name, String label, String[] values,
            String defaultValue) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.LEFT);

        JPanel buttonPanel = new JPanel(flow);

        ButtonGroup group = new ButtonGroup();
        QueryActionListener listener = new QueryActionListener(this, name);

        JRadioButton[] buttons = new JRadioButton[values.length];

        for (int i = 0; i < values.length; i++) {
            JRadioButton checkbox = new JRadioButton(values[i]);
            buttons[i] = checkbox;
            checkbox.setBackground(_background);

            checkbox.setOpaque(false);

            if (values[i].equals(defaultValue)) {
                checkbox.setSelected(true);
            }

            group.add(checkbox);
            buttonPanel.add(checkbox);

            checkbox.addActionListener(listener);
        }

        _addPair(name, lbl, buttonPanel, buttons);
    }

    public void addTextArea(String name, String label, String theValue) {
        addTextArea(name, label, theValue, Color.white, Color.black, _height,
                _width);
    }

    private void addTextArea(String name, String label, String theValue,
            Color background, Color foreground, int height, int width) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        JTextArea textArea = new JTextArea(theValue, height, width);
        textArea.setEditable(true);
        textArea.setBackground(background);
        textArea.setForeground(foreground);

        QueryScrollPane textPane = new QueryScrollPane(textArea);
        _addPair(name, lbl, textPane, textPane);
        textArea.addFocusListener(new QueryFocusListener(this, name));
    }

    public boolean getBooleanValue(String name) throws NoSuchElementException,
            IllegalArgumentException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + "\" in the query box.");
        }

        if (result instanceof JToggleButton) {
            return ((JToggleButton) result).isSelected();
        } else {
            throw new IllegalArgumentException("Item named \"" + name
                    + "\" is not a radio button, and hence does not have "
                    + "a boolean value.");
        }
    }

    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        preferred.width = Short.MAX_VALUE;
        return preferred;
    }

    private Object getObjectValue(String name) throws NoSuchElementException,
            IllegalArgumentException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JTextField) {
            return ((JTextField) result).getText();
        } else if (result instanceof QueryColorChooser) {
            return ((QueryColorChooser) result).getSelectedColor();
        } else if (result instanceof QueryFileChooser) {
            return ((QueryFileChooser) result).getSelectedFileName();
        } else if (result instanceof JTextArea) {
            return ((JTextArea) result).getText();
        } else if (result instanceof JToggleButton) {
            JToggleButton toggleButton = (JToggleButton) result;

            if (toggleButton.isSelected()) {
                return "true";
            } else {
                return "false";
            }
        } else if (result instanceof JSlider) {
            return "" + ((JSlider) result).getValue();
        } else if (result instanceof JComboBox) {
            return (((JComboBox) result).getSelectedItem());
        } else if (result instanceof JToggleButton[]) {
            JToggleButton[] buttons = (JToggleButton[]) result;
            StringBuffer toReturn = null;

            for (JToggleButton button : buttons) {
                if (button.isSelected()) {
                    if (toReturn == null) {
                        toReturn = new StringBuffer(button.getText());
                    } else {
                        toReturn.append(", ").append(button.getText());
                    }
                }
            }

            if (toReturn == null) {
                toReturn = new StringBuffer();
            }

            return toReturn.toString();
        } else if (result instanceof QueryScrollPane) {
            return ((QueryScrollPane) result).getText();
        } else if (result instanceof SettableQueryChooser) {
            return ((SettableQueryChooser) result).getQueryValue();
        } else {
            throw new IllegalArgumentException("Query class cannot generate"
                    + " a string representation for entries of type "
                    + result.getClass());
        }
    }

    public String getStringValue(String name) throws NoSuchElementException,
            IllegalArgumentException {
        return getObjectValue(name).toString();
    }

    private int getTextWidth() {
        return _width;
    }

    private void set(String name, String value) throws NoSuchElementException,
            IllegalArgumentException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JTextField) {
            ((JTextField) result).setText(value);
        } else if (result instanceof JTextArea) {
            ((JTextArea) result).setText(value);
        } else if (result instanceof QueryScrollPane) {
            ((QueryScrollPane) result).setText(value);
        } else if (result instanceof JToggleButton) {
            Boolean flag = Boolean.valueOf(value);
            setBoolean(name, flag);
        } else if (result instanceof JSlider) {
            Integer parsed = Integer.valueOf(value);
            ((JSlider) result).setValue(parsed);
        } else if (result instanceof JComboBox) {
            ((JComboBox) result).setSelectedItem(value);
        } else if (result instanceof JToggleButton[]) {
            Set selectedValues = new HashSet();
            StringTokenizer tokenizer = new StringTokenizer(value, ",");

            while (tokenizer.hasMoreTokens()) {
                selectedValues.add(tokenizer.nextToken().trim());
            }

            JToggleButton[] buttons = (JToggleButton[]) result;

            for (JToggleButton button : buttons) {
                if (selectedValues.contains(button.getText())) {
                    button.setSelected(true);
                } else {
                    button.setSelected(false);
                }
            }
        } else if (result instanceof QueryColorChooser) {
            ((QueryColorChooser) result).setColor(value);
        } else if (result instanceof SettableQueryChooser) {
            ((SettableQueryChooser) result).setQueryValue(value);
        } else if (result instanceof QueryFileChooser) {
            ((QueryFileChooser) result).setFileName(value);
        } else {
            throw new IllegalArgumentException("Query class cannot set"
                    + " a string representation for entries of type "
                    + result.getClass());
        }

        previous.put(name, value);
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        _background = color;

        Component[] components = getComponents();

        for (Component component : components) {
            if (!(component instanceof JTextField)) {
                component.setBackground(_background);
            }
        }
    }

    private void setBoolean(String name, boolean value)
            throws NoSuchElementException, IllegalArgumentException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + "\" in the query box.");
        }

        if (result instanceof JToggleButton) {
            ((JToggleButton) result).setSelected(value);
        } else {
            throw new IllegalArgumentException("Item named \"" + name
                    + "\" is not a radio button, and hence does not have "
                    + "a boolean value.");
        }

        _notifyListeners(name);
    }

    public void setColumns(int columns) {
        if (columns <= 0) {
            throw new IllegalArgumentException(
                    "Query.setColumns() requires a strictly positive "
                            + "argument.");
        }

        this.columns = columns;
    }

    public void setDisplay(String name, String value)
            throws NoSuchElementException, IllegalArgumentException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JTextArea) {
            JTextArea label = (JTextArea) result;
            label.setText(value);
        } else {
            throw new IllegalArgumentException("Item named \"" + name
                    + "\" is not a display, and hence cannot be set using "
                    + "setDisplay().");
        }

        _notifyListeners(name);
    }

    public void setEnabled(String name, boolean value) {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JComponent) {
            ((JComponent) result).setEnabled(value);
        } else if (result instanceof JToggleButton[]) {
            JToggleButton[] buttons = (JToggleButton[]) result;

            for (JToggleButton button : buttons) {
                button.setEnabled(value);
            }
        }
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    public void setLine(String name, String value) {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JTextField) {
            JTextField line = (JTextField) result;
            line.setText(value);
        } else {
            throw new IllegalArgumentException("Item named \"" + name
                    + "\" is not a line, and hence cannot be set using "
                    + "setLine().");
        }

        _notifyListeners(name);
    }

    public void setMessage(String message) {
        if (!messageScrollPaneAdded) {
            messageScrollPaneAdded = true;
            add(_messageScrollPane, 1);

            add(Box.createRigidArea(new Dimension(0, 10)), 2);
        }

        messageArea.setText(message);

        int lineCount = messageArea.getLineCount() + 1;

        if (lineCount > 30) {
            lineCount = 30;
        }

        messageArea.setRows(lineCount);
        messageArea.setColumns(_width);

        validate();
    }

    public void setSlider(String name, int value) {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JSlider) {
            JSlider theSlider = (JSlider) result;

            theSlider.setValue(value);
        } else {
            throw new IllegalArgumentException("Item named \"" + name
                    + "\" is not a slider, and hence cannot be set using "
                    + "setSlider().");
        }

        _notifyListeners(name);
    }

    public void setTextHeight(int characters) {
        _height = characters;
    }

    public void setTextWidth(int characters) {
        _width = characters;
    }

    public void setToolTip(String name, String tip) {
        JLabel label = (JLabel) _labels.get(name);

        if (label != null) {
            label.setToolTipText(tip);
        }
    }

    private static Color stringToColor(String description) {
        String[] specArray = description.split("[{},]");
        float red = 0f;
        float green = 0f;
        float blue = 0f;
        float alpha = 1.0f;

        try {
            int i = 0;

            while (specArray[i].trim().equals("")) {
                i++;
            }

            if (specArray.length > i) {
                red = Float.parseFloat(specArray[i]);
            }

            i++;

            while (specArray[i].trim().equals("")) {
                i++;
            }

            if (specArray.length > i) {
                green = Float.parseFloat(specArray[i]);
            }

            i++;

            while (specArray[i].trim().equals("")) {
                i++;
            }

            if (specArray.length > i) {
                blue = Float.parseFloat(specArray[i]);
            }

            i++;

            while (specArray[i].trim().equals("")) {
                i++;
            }

            if (specArray.length > i) {
                alpha = Float.parseFloat(specArray[i]);
            }
        } catch (Exception ignored) {
        }
        return new Color(red, green, blue, alpha);
    }

    public String stringValue(String name) throws NoSuchElementException,
            IllegalArgumentException {
        return getStringValue(name);
    }

    private static final int DEFAULT_ENTRY_HEIGHT = 10;

    private static final int DEFAULT_ENTRY_WIDTH = 30;

    private void _addPair(String name, JLabel label, Component widget,
            Object entry) {

        _constraints.gridwidth = 1;
        _constraints.insets = leftPadding;
        _grid.setConstraints(label, _constraints);
        _entryPanel.add(label);

        _constraints.insets = insets;

        if ((columns > 1) && (((_entries.size() + 1) % columns) != 0)) {
            _constraints.gridwidth = 1;
        } else {
            _constraints.gridwidth = GridBagConstraints.REMAINDER;
        }

        _grid.setConstraints(widget, _constraints);
        _entryPanel.add(widget);

        _entries.put(name, entry);
        _labels.put(name, label);
        previous.put(name, getObjectValue(name));

        _recalculatePreferredSize(widget);
    }

    private void _recalculatePreferredSize(Component widget) {
        Dimension preferredSize = _entryPanel.getPreferredSize();

        preferredSize.width += 25;

        widgetsHeight += widget.getPreferredSize().height + insets.top
                + insets.bottom;
        preferredSize.height = widgetsHeight;

        Toolkit tk = Toolkit.getDefaultToolkit();

        if (preferredSize.height > tk.getScreenSize().height) {
            preferredSize.height = (int) (tk.getScreenSize().height * 0.75);
            _entryScrollPane.setPreferredSize(preferredSize);
        }

        _entryScrollPane.setPreferredSize(preferredSize);

        _entryPanel.revalidate();
    }

    private Color _background = null;

    private final GridBagConstraints _constraints;

    private final Map _entries = new HashMap();

    private final GridBagLayout _grid;

    private Vector _listeners;

    private void _notifyListeners(String name) {
        if (_listeners != null) {
            String previous = (String) this.previous.get(name);
            String newValue = getStringValue(name);

            if (newValue.equals(previous)) {
                return;
            }

            this.previous.put(name, newValue);

            Enumeration listeners = _listeners.elements();

            while (listeners.hasMoreElements()) {
                QueryListener queryListener = (QueryListener) (listeners
                        .nextElement());
                queryListener.changed(name);
            }
        }
    }

    private int columns = 1;

    private final JPanel _entryPanel = new JPanel();

    private final JScrollPane _entryScrollPane;

    private int _height = DEFAULT_ENTRY_HEIGHT;

    private final Map _labels = new HashMap();

    private final Insets leftPadding = new Insets(0, 10, 0, 0);

    private JTextArea messageArea = null;

    private final JScrollPane _messageScrollPane;

    private boolean messageScrollPaneAdded = false;

    private Insets insets = new Insets(0, 0, 0, 0);

    private final Map previous = new HashMap();

    private int widgetsHeight = 20;

    private int _width = DEFAULT_ENTRY_WIDTH;

    static class QueryActionListener implements ActionListener {

        QueryActionListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent event) {
            owner._notifyListeners(name);
        }

        private final Query owner;

        private final String name;
    }

    public static class QueryColorChooser extends Box implements ActionListener {

        QueryColorChooser(Query owner, String name, String defaultColor) {
            super(BoxLayout.X_AXIS);
            this.owner = owner;
            entryBox = new JTextField(defaultColor, this.owner.getTextWidth());

            JButton button = new JButton("Choose");
            button.addActionListener(this);
            add(entryBox);
            add(button);

            entryBox.addActionListener(new QueryActionListener(this.owner, name));
            entryBox.addFocusListener(new QueryFocusListener(this.owner, name));

            this.name = name;
        }

        public void actionPerformed(ActionEvent e) {
            String spec = getSelectedColor().trim();
            Color newColor = JColorChooser.showDialog(owner, "Choose Color",
                    stringToColor(spec));

            if (newColor != null) {
                float[] components = newColor.getRGBComponents(null);
                StringBuilder string = new StringBuilder("{");

                for (int j = 0; j < components.length; j++) {
                    string.append(components[j]);

                    if (j < (components.length - 1)) {
                        string.append(",");
                    } else {
                        string.append("}");
                    }
                }

                entryBox.setText(string.toString());
                owner._notifyListeners(name);
            }
        }

        String getSelectedColor() {
            return entryBox.getText();
        }

        void setColor(String name) {
            entryBox.setText(name);
        }

        private final JTextField entryBox;

        private final String name;

        private final Query owner;
    }

    public static class QueryFileChooser extends Box implements ActionListener {

        public QueryFileChooser(Query owner, String name, String defaultName,
                URI base, File startingDirectory, boolean allowFiles,
                boolean allowDirectories) {
            this(owner, name, defaultName, base, startingDirectory, allowFiles,
                    allowDirectories, false, Color.white, Color.black);
        }

        QueryFileChooser(Query owner, String name, String defaultName,
                URI base, File startingDirectory, boolean allowFiles,
                boolean allowDirectories, boolean save, Color background,
                Color foreground) {
            super(BoxLayout.X_AXIS);
            this.owner = owner;
            this.base = base;
            this.save = save;
            this.startingDirectory = startingDirectory;

            if (!allowFiles && !allowDirectories) {
                throw new IllegalArgumentException(
                        "QueryFileChooser: nothing to be chosen.");
            }

            this.allowFiles = allowFiles;
            this.allowDirectories = allowDirectories;
            entryBox = new JTextField(defaultName, this.owner.getTextWidth());
            entryBox.setBackground(background);
            entryBox.setForeground(foreground);

            JButton button = new JButton("Browse ");
            button.addActionListener(this);
            add(entryBox);
            add(button);

            entryBox.addActionListener(new QueryActionListener(this.owner, name));

            entryBox.addFocusListener(new QueryFocusListener(this.owner, name));

            this.name = name;
        }

        public void actionPerformed(ActionEvent e) {

            JFileChooserBugFix jFileChooserBugFix = new JFileChooserBugFix();
            Color background = null;
            try {
                background = jFileChooserBugFix.saveBackground();
                JFileChooser fileChooser = new JFileChooser(startingDirectory) {
                    public void approveSelection() {
                        File file = getSelectedFile();
                        if (file.exists() && getDialogType() == SAVE_DIALOG) {
                            String queryString = file.getName()
                                    + " already exists. Overwrite?";
                            int selected = JOptionPane.showOptionDialog(null,
                                    queryString, "Confirm save",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, null,
                                    null);
                            if (selected == 0) {
                                super.approveSelection();
                            }
                        } else {
                            super.approveSelection();
                        }
                    }
                };
                String fileName = getSelectedFileName().trim();
                if (!fileName.equals("")) {
                    fileChooser.setSelectedFile(new File(fileName));
                }
                fileChooser.setApproveButtonText("Select");

                fileChooser.setApproveButtonMnemonic('S');

                if (allowFiles && allowDirectories) {
                    fileChooser
                            .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                } else if (allowFiles && !allowDirectories) {
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                } else if (!allowFiles && allowDirectories) {
                    fileChooser
                            .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                } else {
                    throw new RuntimeException(
                            "QueryFileChooser: nothing to be chosen.");
                }

                int returnValue = save ? fileChooser.showSaveDialog(owner)
                        : fileChooser.showOpenDialog(owner);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (base == null) {
                        try {
                            entryBox.setText(fileChooser.getSelectedFile()
                                                        .getCanonicalPath());
                        } catch (IOException ex) {
                            entryBox.setText(fileChooser.getSelectedFile()
                                                        .getName());
                        }
                    } else {
                        File selectedFile = fileChooser.getSelectedFile();

                        try {
                            selectedFile = selectedFile.getCanonicalFile();
                        } catch (IOException ignored) {
                        }

                        URI relativeURI = base
                                .relativize(selectedFile.toURI());
                        if (relativeURI != null
                                && relativeURI.getScheme() != null
                                && relativeURI.getScheme().equals("file")) {
                            String pathName = relativeURI.getPath();
                            File file = new File(pathName.replace("%20", " "));
                            try {
                                entryBox.setText(file.getCanonicalPath()
                                                     .replace('\\', '/'));
                            } catch (IOException ex) {
                                entryBox.setText(file.toString());
                            }
                        } else {
                            entryBox.setText(relativeURI.toString());
                        }
                    }

                    owner._notifyListeners(name);
                }
            } finally {
                jFileChooserBugFix.restoreBackground(background);
            }
        }

        String getSelectedFileName() {
            return entryBox.getText();
        }

        void setFileName(String name) {
            entryBox.setText(name);
        }

        private final Query owner;

        private final URI base;

        private JTextField entryBox;

        private String name;

        private final boolean save;

        private final File startingDirectory;

        private boolean allowFiles;

        private boolean allowDirectories;
    }

    static class QueryFocusListener implements FocusListener {

        QueryFocusListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            owner._notifyListeners(name);
        }

        private final Query owner;

        private final String name;
    }

    static class QueryItemListener implements ItemListener {
        QueryItemListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void itemStateChanged(ItemEvent e) {
            owner._notifyListeners(name);
        }

        private final Query owner;

        private final String name;
    }

    static class QueryScrollPane extends JScrollPane {

        final JTextArea textArea;

        QueryScrollPane(JTextArea c) {
            super(c);
            textArea = c;
            textArea.getDocument().addUndoableEditListener(
                    new UndoListener(textArea));

        }

        String getText() {
            return textArea.getText();
        }

        void setText(String s) {
            textArea.setText(s);
        }
    }

    static class SliderListener implements ChangeListener {
        SliderListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void stateChanged(ChangeEvent event) {
            owner._notifyListeners(name);
        }

        private final Query owner;

        private final String name;
    }
}
