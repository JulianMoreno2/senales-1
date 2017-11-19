package plot.plotter.plot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

@SuppressWarnings("serial")
public abstract class PlotLive extends Plot implements Runnable {

    public abstract void addPoints();

    @Deprecated
    public void makeButtons() {
        if (_startButton == null) {
            _startButton = new JButton("start");
            _startButton.addActionListener(new StartButtonListener());
            add(_startButton);
        }

        _startButton.setVisible(true);

        if (_stopButton == null) {
            _stopButton = new JButton("stop");
            _stopButton.addActionListener(new StopButtonListener());
            add(_stopButton);
        }

        _stopButton.setVisible(true);
        _stopButton.setEnabled(false);
        _startButton.setEnabled(true);
    }

    private void pause() {
        _paused = true;
        _plotting = false;
        _stopButton.setEnabled(false);
        _startButton.setEnabled(true);
    }

    @Override
    public void run() {
        while (_plotting || _paused) {
            if (_plotting) {
                addPoints();
                Thread.yield();
            } else if (_paused) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void setButtons(boolean visible) {
        super.setButtons(visible);

        if (_startButton == null) {
            _startButton = new JButton("start");
            _startButton.addActionListener(new StartButtonListener());
            add(_startButton);
        }

        _startButton.setVisible(visible);

        if (_stopButton == null) {
            _stopButton = new JButton("stop");
            _stopButton.addActionListener(new StopButtonListener());
            add(_stopButton);
        }

        _stopButton.setVisible(visible);

        if (visible) {
            _stopButton.setEnabled(false);
            _startButton.setEnabled(true);
        }
    }

    private synchronized void start() {
        _plotting = true;
        _paused = false;
        if (_stopButton != null) {
            _stopButton.setEnabled(true);
        }
        if (_startButton != null) {
            _startButton.setEnabled(false);
        }
        if (_plotLiveThread == null) {
            _plotLiveThread = new Thread(this, "PlotLive Thread");
            _plotLiveThread.start();
        } else {
            notifyAll();
        }
    }

    public void stop() {
        _plotting = false;
        _paused = false;
        _plotLiveThread = null;
    }

    private Thread _plotLiveThread = null;

    private boolean _plotting = false;

    private boolean _paused = false;

    private JButton _startButton;

    private JButton _stopButton;

    class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            start();
        }
    }

    class StopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            pause();
        }
    }
}
