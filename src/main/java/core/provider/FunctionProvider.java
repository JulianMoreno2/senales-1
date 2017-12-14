package core.provider;

import core.function.LinearConvolve;

public class FunctionProvider {

	public static LinearConvolve provideLinearConvolve() {
		return new LinearConvolve();
	}

}
