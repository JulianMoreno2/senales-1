
public class HarryPlotter {
    /**
     * Return a string describing this applet.
     */
    public String getAppletInfo() {
        return "PlotFourierSeries 1.1: Demo of PlotApplet.\n"
                + "By: Edward A. Lee\n "
                + "($Id: PlotFourierSeries.java 57040 2010-01-27 20:52:32Z cxh $)";
    }

//    /**
//     * Initialize the applet.
//     */
//    public void init() {
//        super.init();
//
//        Plot plot = (Plot) plot();
//
//        plot.setTitle("Fourier Series Approximation to a Square Wave");
//        plot.setXRange(0, 400);
//        plot.setMarksStyle("none");
//        plot.addLegend(0, "ideal");
//        plot.addLegend(1, "1 sinusoid");
//
//        for (int j = 2; j <= 10; j++) {
//            plot.addLegend(j, j + " sinusoids");
//        }
//
//        boolean first = true;
//        plot.addPoint(0, 0.0, 0.0, false);
//
//        for (int i = 0; i <= 400; i++) {
//            double approximation = 0.0;
//
//            for (int j = 1; j <= 10; j++) {
//                double sig = (4.0 * Math
//                        .sin((i * 2.0 * Math.PI * ((2 * j) - 1)) / 400.0))
//                        / (Math.PI * ((2 * j) - 1));
//                approximation += sig;
//                plot.addPoint(j, i, approximation, !first);
//            }
//
//            first = false;
//
//            if (i <= 200) {
//                plot.addPoint(0, i, 1.0, true);
//            }
//
//            if (i >= 200) {
//                plot.addPoint(0, i, -1.0, true);
//            }
//        }
//
//        plot.addPoint(0, 400.0, 0.0, true);
//    }
}
