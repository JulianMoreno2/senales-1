package plot.plotter.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

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

    public JCheckBox addCheckBox(String name, String label, boolean defaultValue) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        JCheckBox checkbox = new JCheckBox();
        checkbox.setBackground(_background);
        checkbox.setOpaque(false);
        checkbox.setSelected(defaultValue);
        _addPair(name, lbl, checkbox, checkbox);

        checkbox.addItemListener(new QueryItemListener(this, name));

        return checkbox;
    }

    public void addChoice(String name, String label, Object[] values,
                          Object defaultChoice) {
        addChoice(name, label, values, defaultChoice, false);
    }

    public void addChoice(String name, String label, Object[] values,
                          Object defaultChoice, boolean editable) {
        addChoice(name, label, values, defaultChoice, editable, Color.white,
                Color.black);
    }

    public JComboBox addChoice(String name, String label, Object[] values,
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

        return combobox;
    }

    public QueryColorChooser addColorChooser(String name, String label,
                                             String defaultColor) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        QueryColorChooser colorChooser = new QueryColorChooser(this, name,
                defaultColor);
        _addPair(name, lbl, colorChooser, colorChooser);
        return colorChooser;
    }

    public JTextArea addDisplay(String name, String label, String theValue) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        JTextArea displayField = new JTextArea(theValue, 1, 10);
        displayField.setEditable(false);
        displayField.setBackground(_background);
        _addPair(name, lbl, displayField, displayField);
        return displayField;
    }

    public void addFileChooser(String name, String label, String defaultName,
                               URI base, File startingDirectory) {
        addFileChooser(name, label, defaultName, base, startingDirectory, true,
                false, false, Color.white, Color.black);
    }

    public void addFileChooser(String name, String label, String defaultName,
                               URI base, File startingDirectory, boolean save) {
        addFileChooser(name, label, defaultName, base, startingDirectory, true,
                false, save, Color.white, Color.black);
    }

    public void addFileChooser(String name, String label, String defaultName,
                               URI base, File startingDirectory, boolean allowFiles,
                               boolean allowDirectories) {
        addFileChooser(name, label, defaultName, base, startingDirectory,
                allowFiles, allowDirectories, false, Color.white, Color.black);
    }

    public void addFileChooser(String name, String label, String defaultName,
                               URI base, File startingDirectory, Color background, Color foreground) {
        addFileChooser(name, label, defaultName, base, startingDirectory, true,
                false, false, background, foreground);
    }

    public QueryFileChooser addFileChooser(String name, String label,
                                           String defaultName, URI base, File startingDirectory,
                                           boolean allowFiles, boolean allowDirectories, Color background,
                                           Color foreground) {
        return addFileChooser(name, label, defaultName, base,
                startingDirectory, allowFiles, allowDirectories, false,
                background, foreground);
    }

    public QueryFileChooser addFileChooser(String name, String label,
                                           String defaultName, URI base, File startingDirectory,
                                           boolean allowFiles, boolean allowDirectories, boolean save,
                                           Color background, Color foreground) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        QueryFileChooser fileChooser = new QueryFileChooser(this, name,
                defaultName, base, startingDirectory, allowFiles,
                allowDirectories, save, background, foreground);
        _addPair(name, lbl, fileChooser, fileChooser);
        return fileChooser;
    }

    public void addLine(String name, String label, String defaultValue) {
        addLine(name, label, defaultValue, Color.white, Color.black);
    }

    public void addLine(String name, String label, String defaultValue,
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

    public JPasswordField addPassword(String name, String label,
                                      String defaultValue) {
        return addPassword(name, label, defaultValue, Color.white, Color.black);
    }

    public JPasswordField addPassword(String name, String label,
                                      String defaultValue, Color background, Color foreground) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        JPasswordField entryBox = new JPasswordField(defaultValue, _width);
        entryBox.setBackground(background);
        entryBox.setForeground(foreground);
        _addPair(name, lbl, entryBox, entryBox);

        entryBox.addActionListener(new QueryActionListener(this, name));

        entryBox.addFocusListener(new QueryFocusListener(this, name));

        return entryBox;
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

    public void addSelectButtons(String name, String label, String[] values,
                                 Set initiallySelected) {
        JLabel lbl = new JLabel(label + ": ");
        lbl.setBackground(_background);

        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.LEFT);

        JPanel buttonPanel = new JPanel(flow);

        QueryActionListener listener = new QueryActionListener(this, name);

        if (initiallySelected == null) {
            initiallySelected = new HashSet();
        }

        JRadioButton[] buttons = new JRadioButton[values.length];

        for (int i = 0; i < values.length; i++) {
            JRadioButton checkbox = new JRadioButton(values[i]);
            buttons[i] = checkbox;
            checkbox.setBackground(_background);

            checkbox.setOpaque(false);

            if (initiallySelected.contains(values[i])) {
                checkbox.setSelected(true);
            }

            buttonPanel.add(checkbox);
            checkbox.addActionListener(listener);
        }

        _addPair(name, lbl, buttonPanel, buttons);
    }

    public void addSeparator() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setPreferredSize(new Dimension(-1, 5));
        panel.add(top, BorderLayout.NORTH);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        panel.add(separator, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(-1, 5));
        panel.add(bottom, BorderLayout.SOUTH);

        _constraints.gridwidth = GridBagConstraints.REMAINDER;
        _constraints.insets = insets;
        _grid.setConstraints(panel, _constraints);
        _entryPanel.add(panel);

        _recalculatePreferredSize(panel);
    }

    public JSlider addSlider(String name, String label, int defaultValue,
                             int minimum, int maximum) throws IllegalArgumentException {
        JLabel lbl = new JLabel(label + ": ");

        if (minimum > maximum) {
            int temp = minimum;
            minimum = maximum;
            maximum = temp;
        }

        if ((defaultValue > maximum) || (defaultValue < minimum)) {
            throw new IllegalArgumentException("Desired default " + "value \""
                    + defaultValue + "\" does not fall "
                    + "between the minimum and maximum.");
        }

        JSlider slider = new JSlider(minimum, maximum, defaultValue);
        _addPair(name, lbl, slider, slider);
        slider.addChangeListener(new SliderListener(this, name));
        return slider;
    }

    public void addText(String text, Color color, int alignment) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setPreferredSize(new Dimension(-1, 5));
        panel.add(top, BorderLayout.NORTH);

        JLabel label = new JLabel(text, alignment);
        label.setForeground(color);
        panel.add(label, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(-1, 5));
        panel.add(bottom, BorderLayout.SOUTH);

        _constraints.gridwidth = GridBagConstraints.REMAINDER;
        _constraints.insets = insets;
        _grid.setConstraints(panel, _constraints);
        _entryPanel.add(panel);

        _recalculatePreferredSize(panel);
    }

    public void addTextArea(String name, String label, String theValue) {
        addTextArea(name, label, theValue, Color.white, Color.black, _height,
                _width);
    }

    public void addTextArea(String name, String label, String theValue,
                            Color background, Color foreground) {
        addTextArea(name, label, theValue, background, foreground, _height,
                _width);
    }

    public JTextArea addTextArea(String name, String label, String theValue,
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
        return textArea;
    }

    public boolean booleanValue(String name) throws NoSuchElementException,
            IllegalArgumentException {
        return getBooleanValue(name);
    }

    public double doubleValue(String name) throws IllegalArgumentException,
            NoSuchElementException, NumberFormatException {
        return getDoubleValue(name);
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

    public char[] getCharArrayValue(String name) throws NoSuchElementException,
            IllegalArgumentException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + "\" in the query box.");
        }

        if (result instanceof JPasswordField) {
            return ((JPasswordField) result).getPassword();
        } else {
            return getStringValue(name).toCharArray();
        }
    }

    public double getDoubleValue(String name) throws IllegalArgumentException,
            NoSuchElementException, NumberFormatException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JPasswordField) {
            throw new IllegalArgumentException("For security reasons, "
                    + "calling getDoubleValue() on a password field is "
                    + "not permitted.  Instead, call getCharArrayValue()");
        } else if (result instanceof JTextField) {
            return (Double.valueOf(((JTextField) result).getText()))
                    .doubleValue();
        } else {
            throw new IllegalArgumentException("Item named \"" + name
                    + "\" is not a text line, and hence cannot be converted "
                    + "to a double value.");
        }
    }

    public int getIntValue(String name) throws IllegalArgumentException,
            NoSuchElementException, NumberFormatException {
        Object result = _entries.get(name);

        if (result == null) {
            throw new NoSuchElementException("No item named \"" + name
                    + " \" in the query box.");
        }

        if (result instanceof JPasswordField) {
            throw new IllegalArgumentException("For security reasons, "
                    + "calling getIntValue() on a password field is "
                    + "not permitted.  Instead, call getCharArrayValue()");
        } else if (result instanceof JTextField) {
            return (Integer.valueOf(((JTextField) result).getText()))
                    .intValue();
        } else if (result instanceof JSlider) {
            return ((JSlider) result).getValue();
        } else if (result instanceof JComboBox) {
            return ((JComboBox) result).getSelectedIndex();
        } else if (result instanceof JToggleButton[]) {
            JToggleButton[] buttons = (JToggleButton[]) result;

            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].isSelected()) {
                    return i;
                }
            }

            return -1;
        } else {
            throw new IllegalArgumentException("Item named \"" + name
                    + "\" is not a text line or slider, and hence "
                    + "cannot be converted to " + "an integer value.");
        }
    }

    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        preferred.width = Short.MAX_VALUE;
        return preferred;
    }

    public Object getObjectValue(String name) throws NoSuchElementException,
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

            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].isSelected()) {
                    if (toReturn == null) {
                        toReturn = new StringBuffer(buttons[i].getText());
                    } else {
                        toReturn.append(", " + buttons[i].getText());
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

    public int getTextHeight() {
        return _height;
    }

    public int getTextWidth() {
        return _width;
    }

    public boolean hasEntry(String name) {
        return _entries.containsKey(name);
    }

    public int intValue(String name) throws IllegalArgumentException,
            NoSuchElementException, NumberFormatException {
        return getIntValue(name);
    }

    public void notifyListeners() {
        Iterator names = _entries.keySet().iterator();

        while (names.hasNext()) {
            String name = (String) names.next();
            _notifyListeners(name);
        }
    }

    public void removeQueryListener(QueryListener listener) {
        if (_listeners == null) {
            return;
        }

        _listeners.remove(listener);
    }

    public void set(String name, String value) throws NoSuchElementException,
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
            setBoolean(name, flag.booleanValue());
        } else if (result instanceof JSlider) {
            Integer parsed = Integer.valueOf(value);
            ((JSlider) result).setValue(parsed.intValue());
        } else if (result instanceof JComboBox) {
            ((JComboBox) result).setSelectedItem(value);
        } else if (result instanceof JToggleButton[]) {
            Set selectedValues = new HashSet();
            StringTokenizer tokenizer = new StringTokenizer(value, ",");

            while (tokenizer.hasMoreTokens()) {
                selectedValues.add(tokenizer.nextToken().trim());
            }

            JToggleButton[] buttons = (JToggleButton[]) result;

            for (int i = 0; i < buttons.length; i++) {
                if (selectedValues.contains(buttons[i].getText())) {
                    buttons[i].setSelected(true);
                } else {
                    buttons[i].setSelected(false);
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

    public void setAndNotify(String name, String value)
            throws NoSuchElementException, IllegalArgumentException {
        set(name, value);
        _notifyListeners(name);
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        _background = color;

        Component[] components = getComponents();

        for (int i = 0; i < components.length; i++) {
            if (!(components[i] instanceof JTextField)) {
                components[i].setBackground(_background);
            }
        }
    }

    public void setBoolean(String name, boolean value)
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

            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setEnabled(value);
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

    public static Color stringToColor(String description) {
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
        } catch (Exception ex) {
        }
        return new Color(red, green, blue, alpha);
    }

    public String stringValue(String name) throws NoSuchElementException,
            IllegalArgumentException {
        return getStringValue(name);
    }

    public static final int DEFAULT_ENTRY_HEIGHT = 10;

    public static final int DEFAULT_ENTRY_WIDTH = 30;

    protected void _addPair(String name, JLabel label, Component widget,
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

    protected void _recalculatePreferredSize(Component widget) {
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

    protected Color _background = null;

    protected GridBagConstraints _constraints;

    protected Map _entries = new HashMap();

    protected GridBagLayout _grid;

    protected Vector _listeners;

    void _notifyListeners(String name) {
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

    private JPanel _entryPanel = new JPanel();

    private JScrollPane _entryScrollPane;

    private int _height = DEFAULT_ENTRY_HEIGHT;

    private Map _labels = new HashMap();

    private Insets leftPadding = new Insets(0, 10, 0, 0);

    private JTextArea messageArea = null;

    private JScrollPane _messageScrollPane;

    private boolean messageScrollPaneAdded = false;

    private Insets insets = new Insets(0, 0, 0, 0);

    private Map previous = new HashMap();

    private int widgetsHeight = 20;

    private int _width = DEFAULT_ENTRY_WIDTH;

    public static class QueryActionListener implements ActionListener {

        public QueryActionListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void actionPerformed(ActionEvent event) {
            owner._notifyListeners(name);
        }

        private Query owner;

        private String name;
    }

    public static class QueryColorChooser extends Box implements ActionListener {

        public QueryColorChooser(Query owner, String name, String defaultColor) {
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
                StringBuffer string = new StringBuffer("{");

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

        public String getSelectedColor() {
            return entryBox.getText();
        }

        public void setColor(String name) {
            entryBox.setText(name);
        }

        private JTextField entryBox;

        private String name;

        private Query owner;
    }

    public static class QueryFileChooser extends Box implements ActionListener {

        public QueryFileChooser(Query owner, String name, String defaultName,
                                URI base, File startingDirectory, boolean allowFiles,
                                boolean allowDirectories) {
            this(owner, name, defaultName, base, startingDirectory, allowFiles,
                    allowDirectories, false, Color.white, Color.black);
        }

        public QueryFileChooser(Query owner, String name, String defaultName,
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
                        } catch (IOException ex) {
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

        public String getSelectedFileName() {
            return entryBox.getText();
        }

        public void setFileName(String name) {
            entryBox.setText(name);
        }

        private Query owner;

        private URI base;

        private JTextField entryBox;

        private String name;

        private boolean save;

        private File startingDirectory;

        private boolean allowFiles;

        private boolean allowDirectories;
    }

    public static class QueryFocusListener implements FocusListener {

        public QueryFocusListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            owner._notifyListeners(name);
        }

        private Query owner;

        private String name;
    }

    public static class QueryItemListener implements ItemListener {
        public QueryItemListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void itemStateChanged(ItemEvent e) {
            owner._notifyListeners(name);
        }

        private Query owner;

        private String name;
    }

    static class QueryScrollPane extends JScrollPane {

        public JTextArea textArea;

        QueryScrollPane(JTextArea c) {
            super(c);
            textArea = c;
            textArea.getDocument().addUndoableEditListener(
                    new UndoListener(textArea));

        }

        public String getText() {
            String retval = textArea.getText();
            return retval;
        }

        public void setText(String s) {
            textArea.setText(s);
        }
    }

    public static class SliderListener implements ChangeListener {
        public SliderListener(Query owner, String name) {
            this.name = name;
            this.owner = owner;
        }

        public void stateChanged(ChangeEvent event) {
            owner._notifyListeners(name);
        }

        private Query owner;

        private String name;
    }
}
