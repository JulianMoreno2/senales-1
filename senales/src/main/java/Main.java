
public class Main {

    // display an array of Complex numbers to standard output
    private static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }

    /***************************************************************************
     *  Test client and sample execution
     *
     *  % java FFT 4
     *  x
     *  -------------------
     *  -0.03480425839330703
     *  0.07910192950176387
     *  0.7233322451735928
     *  0.1659819820667019
     *
     *  y = fft(x)
     *  -------------------
     *  0.9336118983487516
     *  -0.7581365035668999 + 0.08688005256493803i
     *  0.44344407521182005
     *  -0.7581365035668999 - 0.08688005256493803i
     *
     *  z = ifft(y)
     *  -------------------
     *  -0.03480425839330703
     *  0.07910192950176387 + 2.6599344570851287E-18i
     *  0.7233322451735928
     *  0.1659819820667019 - 2.6599344570851287E-18i
     *
     *  c = cconvolve(x, x)
     *  -------------------
     *  0.5506798633981853
     *  0.23461407150576394 - 4.033186818023279E-18i
     *  -0.016542951108772352
     *  0.10288019294318276 + 4.033186818023279E-18i
     *
     *  d = convolve(x, x)
     *  -------------------
     *  0.001211336402308083 - 3.122502256758253E-17i
     *  -0.005506167987577068 - 5.058885073636224E-17i
     *  -0.044092969479563274 + 2.1934338938072244E-18i
     *  0.10288019294318276 - 3.6147323062478115E-17i
     *  0.5494685269958772 + 3.122502256758253E-17i
     *  0.240120239493341 + 4.655566391833896E-17i
     *  0.02755001837079092 - 2.1934338938072244E-18i
     *  4.01805098805014E-17i
     *
     ***************************************************************************/

    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        Complex[] x = new Complex[n];

        // original data
        for (int i = 0; i < n; i++) {
            x[i] = new Complex(i, 0);
            x[i] = new Complex(-2*Math.random() + 1, 0);
        }
        show(x, "x");

        // FFT of original data
        Complex[] y = FFT.build(x);
        show(y, "y = fft(x)");
//
//        // take inverse FFT
//        Complex[] z = ifft(y);
//        show(z, "z = ifft(y)");
//
//        // circular convolution of x with itself
//        Complex[] c = cconvolve(x, x);
//        show(c, "c = cconvolve(x, x)");
//
//        // linear convolution of x with itself
//        Complex[] d = convolve(x, x);
//        show(d, "d = convolve(x, x)");
    }
}
