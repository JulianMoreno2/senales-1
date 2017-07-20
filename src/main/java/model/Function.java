package model;

public class Function {

    private int pointsAmount;
    private double real;
    private double imaginary;

    public Function(int pointsAmount, double real, double imaginary){

        this.pointsAmount = pointsAmount;
        this.real = real;
        this.imaginary = imaginary;
    }


    public Complex[] buildPoints() {
        Complex[] x = new Complex[pointsAmount];

        for (int i = 0; i < pointsAmount; i++) {
            x[i] = new Complex(i, 0);
            x[i] = new Complex(real, imaginary);
        }
        return x;
    }
}
