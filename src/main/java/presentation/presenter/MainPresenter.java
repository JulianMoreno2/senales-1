package presentation.presenter;

import core.action.*;
import core.util.FilterKeys;
import presentation.presenter.Presenter.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainPresenter extends Presenter<View> {

	private final PulsationPlotAction pulsationPlotAction;
    private final LowPassFilterPlotAction lowPassFilterPlotAction;
    private final GetSignalFrecuencyAction getSignalFrecuencyAction;
    private final GetFileCsvPointsAction getFileCsvPointsAction;
	private final SaveFilterAction saveFilterAction;

    public MainPresenter(GetFileCsvPointsAction getFileCsvPointsAction,
                         PulsationPlotAction pulsationPlotAction,
                         LowPassFilterPlotAction lowPassFilterPlotAction,
                         GetSignalFrecuencyAction getSignalFrecuencyAction,
                         SaveFilterAction saveFilterAction) {

        this.getFileCsvPointsAction = getFileCsvPointsAction;
        this.pulsationPlotAction = pulsationPlotAction;
        this.lowPassFilterPlotAction = lowPassFilterPlotAction;
        this.getSignalFrecuencyAction = getSignalFrecuencyAction;
        this.saveFilterAction = saveFilterAction;
    }

    public void onClickOpenPulsationPlot() {
        pulsationPlotAction.execute(loadFileAsList());
    }

    public void onClickLowPassFilterApply() {
        lowPassFilterPlotAction.execute(loadNewFile());
    }

    public void onClickLowPassFilterPlot() {
    	saveFilterAction.execute(FilterKeys.LOW_PASS_FILTER, loadFilterFile());
    }

    public void onClickPlotSignal() {
    	pulsationPlotAction.execute(loadNewFile());	
    }

    private List<Double> loadFilterFile() {
    	try {
            return getFileCsvPointsAction.getFilterDataFileCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
	}

	private List<Double> loadNewFile() {

        try {
            Map<String, String> map = getFileCsvPointsAction.getDataFileCsvAsMap();
            
            List<Map.Entry<String, String>> entries = new ArrayList<>(map.entrySet());
            return entries.subList(2, entries.size()).stream()
                    .map(Map.Entry::getValue)
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<Double> loadFileAsList() {

        try {
            return getFileCsvPointsAction.<Double>getArrayListDataFileCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public interface View extends Presenter.View {

    }

}
