package core.action;

import core.model.Complex;
import core.provider.PlotterAppProvider;
import core.service.pulsation.PulsationService;

import java.io.IOException;
import java.util.List;

public class PulsationPlotAction {

    private final PulsationService pulsationService;

    public PulsationPlotAction(PulsationService pulsationService) {
        this.pulsationService = pulsationService;
    }

    public void execute(List<Double> data) {
        int pulsationsQuantity = pulsationService.getPulsationsQuantity(data);
        int arrhythmiaQuantityPoint = pulsationService.getArrhythmiaQuantityPoint(data);

        try {
            PlotterAppProvider.provide().startPulsationPlot(data, pulsationsQuantity, arrhythmiaQuantityPoint);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
