package plot.plotter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MessageHandler {

    public static void error(String info) {
        _handler._error(info);
    }


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

    public static MessageHandler getMessageHandler() {
        return _handler;
    }

    public static void message(String info) {
        _handler._message(info);
    }

    public static void setMessageHandler(MessageHandler handler) {
        if (handler != null) {
            _handler = handler;
        }
    }

    public static String shortDescription(Throwable throwable) {
        String throwableType = null;

        if (throwable instanceof Exception) {
            throwableType = "Exception";
        } else if (throwable instanceof Error) {
            throwableType = "Error";
        } else {
            throwableType = "Throwable";
        }

        return throwableType;
    }

    public static void warning(String info) throws CancelException {
        _handler._warning(info);
    }

    public static void warning(String info, Throwable throwable)
            throws CancelException {
        _handler._warning(info + ": " + throwable.getMessage(), throwable);
    }

    public static boolean yesNoQuestion(String question) {
        return _handler._yesNoQuestion(question);
    }

    public static boolean yesNoCancelQuestion(String question)
            throws CancelException {
        return _handler._yesNoCancelQuestion(question);
    }

    protected void _error(String info) {
        System.err.println(info);
    }

    protected void _error(String info, Throwable throwable) {
        if (throwable instanceof CancelException) {
            return;
        }

        System.err.println(info);
        throwable.printStackTrace();
    }

    protected void _message(String info) {
        System.err.println(info);
    }

    protected void _warning(String info) throws CancelException {
        _error(info);
    }

    protected void _warning(String info, Throwable throwable)
            throws CancelException {
        _error(info, throwable);
    }

    protected boolean _yesNoQuestion(String question) {
        System.out.print(question);
        System.out.print(" (yes or no) ");

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(
                System.in));

        try {
            String reply = stdIn.readLine();

            if (reply == null) {
                return false;
            } else if (reply.trim().toLowerCase().equals("yes")) {
                return true;
            }
        } catch (IOException ex) {
        }

        return false;
    }

    protected boolean _yesNoCancelQuestion(String question)
            throws CancelException {
        System.out.print(question);
        System.out.print(" (yes or no or cancel) ");

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(
                System.in));

        try {
            String reply = stdIn.readLine();

            if (reply == null) {
                return false;
            } else {
                if (reply.trim().toLowerCase().equals("yes")) {
                    return true;
                } else if (reply.trim().toLowerCase().equals("cancel")) {
                    throw new CancelException("Cancelled: "
                            + question);
                }
            }
        } catch (IOException ex) {
        }

        return false;
    }

    private static MessageHandler _handler = new MessageHandler();
}
