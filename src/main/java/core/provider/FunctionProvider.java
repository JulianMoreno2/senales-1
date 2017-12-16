package core.provider;

import core.function.FFT;
import core.function.LinearConvolve;

public class FunctionProvider {

	public static LinearConvolve provideLinearConvolve() {
		return new LinearConvolve();
	}

	public static FFT provideFFT() {
		return new FFT();
	}
}
