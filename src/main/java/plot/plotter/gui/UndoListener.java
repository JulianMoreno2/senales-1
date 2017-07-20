package plot.plotter.gui;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class UndoListener implements UndoableEditListener {

    public UndoListener(JTextComponent textArea) {

        // Set the mapping for shortcut keys;
        InputMap inputMap = textArea.getInputMap();
        ActionMap actionMap = textArea.getActionMap();

        // Ctrl-z or equivalent to undo.
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), "undo");
        actionMap.put("undo", _undoAction);
        // Ctrl-y or equivalent to redo
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask()), "redo");
        actionMap.put("redo", _redoAction);
    }

    public void undoableEditHappened(UndoableEditEvent event) {
        _undo.addEdit(event.getEdit());
        _undoAction._updateUndoState();
        _redoAction._updateRedoState();
    }

    protected UndoAction _undoAction = new UndoAction();

    protected RedoAction _redoAction = new RedoAction();

    protected UndoManager _undo = new UndoManager();

    protected class UndoAction extends AbstractAction {

        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                _undo.undo();
            } catch (CannotUndoException ex) {
                throw new RuntimeException("Unable to undo.", ex);
            }
            _updateUndoState();
            _redoAction._updateRedoState();
        }

        protected void _updateUndoState() {
            if (_undo.canUndo()) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        }
    }

    protected class RedoAction extends AbstractAction {
        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                _undo.redo();
            } catch (CannotRedoException ex) {
                throw new RuntimeException("Unable to redo.", ex);
            }
            _updateRedoState();
            _undoAction._updateUndoState();
        }

        protected void _updateRedoState() {
            if (_undo.canRedo()) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        }
    }
}