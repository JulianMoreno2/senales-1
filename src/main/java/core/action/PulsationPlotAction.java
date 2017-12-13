package core.action;

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
}
