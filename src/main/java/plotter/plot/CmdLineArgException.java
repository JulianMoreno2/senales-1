package plotter.plot;

public class CmdLineArgException extends Exception {

    public CmdLineArgException() {
        super();
    }

    CmdLineArgException(String s) {
        super(s);
    }
}