package core.service.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import core.function.FFT;
import core.function.InverseFFT;
import core.function.LinearConvolve;
import core.model.Complex;

public class LowPassFilterService {

    private final LinearConvolve linearConvolve;

    public LowPassFilterService(LinearConvolve linearConvolve) {
        this.linearConvolve = linearConvolve;
    }

    public List<Double> apply(List<Double> data, List<Double> filterData) {

        Complex[] dataAsComplex = toPowerOfTwo(data);

        Complex[] filterDataAsComplex = InverseFFT.build(toPowerOfTwo(filterData));

        Complex[] filteredData = FFT.build(applyLinearConvolve(dataAsComplex, filterDataAsComplex));

        return Arrays.stream(filteredData).map(Complex::re).collect(Collectors.toList());
    }

    private Complex[] applyLinearConvolve(Complex[] dataAsComplex, Complex[] filterDataAsComplex) {

        int dataLength = dataAsComplex.length;
        Complex[] dataResult = new Complex[dataLength];

        int filterLength = filterDataAsComplex.length;
        Complex[] dataAux = new Complex[filterLength];

        int times = dataLength / filterLength;

        for (int i = 0; i < times; i++) {
            for (int j = 0; j < filterLength; j++) {
                dataAux[j] = dataAsComplex[j + (filterLength * i)];
            }

            Complex[] dataFiltered = linearConvolve.build(dataAux, filterDataAsComplex);
            for (int h = 0; h < filterLength; h++) {
                dataResult[h + (filterLength * i)] = dataFiltered[h];
            }
        }

        return dataResult;
    }

    private Complex[] toPowerOfTwo(List<Double> data) {

        Complex[] dataAsComplex = toComplex(data);

        int dataLength = dataAsComplex.length;
        double pow = Math.pow(2, Math.round(Math.log(dataLength) / Math.log(2)));
        double powerOf2 = pow < dataLength ? pow : pow/2;

        if (powerOf2 != dataLength) {
            dataLength = (int) powerOf2;
        }

        Complex[] dataPowerOfTwo = new Complex[dataLength];

        for (int i = 0; i < dataLength; i++) {
            dataPowerOfTwo[i] = dataAsComplex[i];
        }

        return dataPowerOfTwo;
    }

    public Complex[] toComplex(List<Double> points) {

        Complex[] functionPoints = new Complex[points.size()];

        for (int i = 0; i < functionPoints.length; i++) {
            functionPoints[i] = new Complex(points.get(i), 0);
        }

        return functionPoints;
    }
}
