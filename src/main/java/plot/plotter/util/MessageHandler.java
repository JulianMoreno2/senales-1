package plot.plotter.util;

import com.sun.applet2.preloader.CancelException;

class MessageHandler {

    public static void error(String info, Throwable throwable) {
        try {
            _handler._error(info, throwable);
        } catch (Throwable throwable2) {
            System.err.println("Internal Error, exception thrown while "
                    + "handling error: \"" + info + "\"\n");
            throwable.printStackTrace();
            System.err.println("Internal Error:\n");
            throwable2.printStackTrace();
        }
    }

    static String shortDescription(Throwable throwable) {
        String throwableType;

        if (throwable instanceof Exception) {
            throwableType = "Exception";
        } else if (throwable instanceof Error) {
            throwableType = "Error";
        } else {
            throwableType = "Throwable";
        }

        return throwableType;
    }

    private void _error(String info, Throwable throwable) {
        if (throwable instanceof CancelException) {
            return;
        }

        System.err.println(info);
        throwable.printStackTrace();
    }

    private static final MessageHandler _handler = new MessageHandler();
}
