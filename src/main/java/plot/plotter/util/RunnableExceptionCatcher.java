package plot.plotter.util;

public class RunnableExceptionCatcher implements Runnable {

    public RunnableExceptionCatcher(Runnable runnable) {
        _runnable = runnable;
    }

    public void run() {
        try {
            _runnable.run();
        } catch (Throwable e) {
            MessageHandler.error(MessageHandler
                    .shortDescription(e), e);
        }
    }

    private final Runnable _runnable;
}
