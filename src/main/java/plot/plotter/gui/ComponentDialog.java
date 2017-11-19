package plot.plotter.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ComponentDialog extends JDialog {

    public ComponentDialog(Frame owner, String title, Component component,
                           String[] buttons) {
        this(owner, title, component, buttons, null);
    }

    private ComponentDialog(Frame owner, String title, Component component,
            String[] buttons, String message) {
        this(owner, title, component, buttons, message, false);
    }

    private ComponentDialog(Frame owner, String title, Component component,
            String[] buttons, String message, boolean resizable) {
        super(owner, title, true);

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (message != null) {
            JTextArea messageArea = new JTextArea(message);
            messageArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
            messageArea.setEditable(false);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setBackground(getContentPane().getBackground());

            messageArea.setAlignmentX(0.0f);
            panel.add(messageArea);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panel.add(component);
        contents = component;

        if (buttons != null) {
            ComponentDialog.buttons = buttons;
        } else {
            ComponentDialog.buttons = defaultButtons;
        }

        optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, ComponentDialog.buttons, ComponentDialog.buttons[0]);

        optionPane.addPropertyChangeListener(event -> {
            String prop = event.getPropertyName();

            if (isVisible()
                    && (event.getSource() == optionPane)
                    && (prop.equals(JOptionPane.VALUE_PROPERTY) || prop
                    .equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                Object value = optionPane.getValue();

                if (value == JOptionPane.UNINITIALIZED_VALUE) {
                    return;
                }

                optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

                if (value instanceof String) {
                    buttonPressed = (String) value;
                }

                setVisible(false);

                handleClosing();
            }
        });

        getContentPane().add(optionPane);
        pack();
        setResizable(resizable);

        if (owner != null) {
            setLocationRelativeTo(owner);
        } else {
            Toolkit tk = Toolkit.getDefaultToolkit();
            setLocation((tk.getScreenSize().width - getSize().width) / 2, (tk
                    .getScreenSize().height - getSize().height) / 2);
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleClosing();
            }
        });

        setVisible(true);
    }

    public String buttonPressed() {
        return buttonPressed;
    }

    private void handleClosing() {
        if ((contents instanceof CloseListener) && !doneHandleClosing) {
            doneHandleClosing = true;
            ((CloseListener) contents).windowClosed(this, buttonPressed);
        }
    }

    private final Component contents;

    private String buttonPressed = "";

    private static String[] buttons;

    private static final String[] defaultButtons = { "OK", "Cancel" };

    private boolean doneHandleClosing = false;

    private final JOptionPane optionPane;

}
