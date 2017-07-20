package plot.function;

import plot.model.Complex;

public class Sin {

    public Complex[] points(int pointsAmount){

        Complex[] y = new Complex[pointsAmount];

        for (int i = 0; i < pointsAmount; i++) {

            y[i] = new Complex(i, 0);

            double k =  100 - (int) (50 * Math.sin((i / 100.0) * 2
                    * Math.PI));

            y[i] = new Complex(k, 0);
        }
        return y;
    }
}
