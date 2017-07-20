package plot.plotter.gui;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

public class JFileChooserBugFix {

    public JFileChooserBugFix() {
        HTMLEditorKit = new HTMLEditorKit();
    }

    public void restoreBackground(Color background) {
        try {
            if (background != null) {
                String rgb = Integer.toHexString(background.getRGB());
                String rule = "body {background: #"
                        + rgb.substring(2, rgb.length()) + ";}";
                StyleSheet styleSheet = HTMLEditorKit.getStyleSheet();
                styleSheet.addRule(rule);
                HTMLEditorKit.setStyleSheet(styleSheet);
            }
        } catch (Exception ex) {
            System.out.println("Problem restoring background color.");
            ex.printStackTrace();
        }
    }

    public Color saveBackground() {
        if (HTMLEditorKit == null) {
            HTMLEditorKit = new HTMLEditorKit();
        }
        StyleSheet styleSheet = HTMLEditorKit.getStyleSheet();
        Color background = null;

        try {
            AttributeSet bodyAttribute = (AttributeSet) styleSheet.getStyle(
                    "body").getAttribute(
                    javax.swing.text.StyleConstants.ResolveAttribute);
            background = styleSheet.getBackground(bodyAttribute);
        } catch (Exception ex) {
        }

        try {
            Color shadow = UIManager.getColor("ToolBar.shadow");
            String rgb = Integer.toHexString(shadow.getRGB());
            String rule = "body {background: #"
                    + rgb.substring(2, rgb.length()) + ";}";
            styleSheet.addRule(rule);
            HTMLEditorKit.setStyleSheet(styleSheet);
        } catch (Exception ex) {
            System.err.println("Problem setting background color");
            ex.printStackTrace();
        }
        return background;
    }

    private HTMLEditorKit HTMLEditorKit;
}