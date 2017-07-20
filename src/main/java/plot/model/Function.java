package plot.model;

import java.util.ArrayList;

public class Function {

    private int pointsAmount;
    private double real;
    private double imaginary;

    public Function(int pointsAmount, double real, double imaginary){
        this.pointsAmount = pointsAmount;
        this.real = real;
        this.imaginary = imaginary;
    }

    public Complex[] buildFromCsvFilePoints(ArrayList csvFilePoints) {

        Complex[] x = new Complex[csvFilePoints.size()];

        for (int i = 0; i < x.length; i++) {
            x[i] = new Complex(i, 0);
            x[i] = new Complex(real, imaginary);
        }
        return x;
    }

    public Complex[] buildSinPoints(int pointsAmount){

        Complex[] x = new Complex[pointsAmount];

        for (int i = 0; i < pointsAmount; i++) {

//            x[i] = new Complex(i, 0);
            x[i] = new Complex(100 - (int) (50 * Math.sin((i / 100.0) * 2
                    * Math.PI)), 0);

        }

        return x;
    }
}
