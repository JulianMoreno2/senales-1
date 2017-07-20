package plot.plotter.gui;

import java.util.EventListener;

public interface QueryListener extends EventListener {
    void changed(String name);
}