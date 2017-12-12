package core.action;

import core.provider.PlotterAppProvider;
import core.service.filter.LowPassFilterService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LowPassFilterPlotAction {

    private final LowPassFilterService lowPassFilterService;

    public LowPassFilterPlotAction(LowPassFilterService lowPassFilterService) {
        this.lowPassFilterService = lowPassFilterService;
    }

    public void execute(Map<String, String> data, Integer frecuency, Integer order) {

        List<Map.Entry<String, String>> entries = new ArrayList<>(data.entrySet());
        List<Double> dataAsDouble = entries.subList(2, entries.size()).stream()
                .map(Map.Entry::getValue)
                .map(Double::parseDouble)
                .collect(Collectors.toList());

        ArrayList<Double> points = this.lowPassFilterService.apply(dataAsDouble, frecuency, order);

        try {
            PlotterAppProvider.provide().startLowPassFilterPlot(points);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
