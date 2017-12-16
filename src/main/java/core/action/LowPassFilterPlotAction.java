package core.action;

import java.io.IOException;
import java.util.List;

import core.provider.PlotterAppProvider;
import core.repository.FilterRepository;
import core.service.filter.LowPassFilterService;
import core.util.FilterKeys;

public class LowPassFilterPlotAction {

    private final LowPassFilterService lowPassFilterService;
    private final FilterRepository filterRepository;

    public LowPassFilterPlotAction(LowPassFilterService lowPassFilterService, FilterRepository filterRepository) {
        this.lowPassFilterService = lowPassFilterService;
        this.filterRepository = filterRepository;
    }

    public void execute(List<Double> signalData) {

        List<Double> filterData = this.filterRepository.get(FilterKeys.FILTER);
		List<Double> points = this.lowPassFilterService.apply(signalData, filterData);

        try {
            PlotterAppProvider.provide().startSimplePlot(points);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
