package function;

import model.Complex;

public class CircularConvolve {

    public static Complex[] build(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) { throw new RuntimeException("Dimensions don't agree"); }

        int n = x.length;

        // compute FFT of each sequence
        Complex[] a = FFT.build(x);
        Complex[] b = FFT.build(y);

        // point-wise multiply
        Complex[] c = new Complex[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return InverseFFT.build(c);
    }
}
