package presentation.presenter;

import core.action.GetFileCsvPointsAction;
import core.action.LowPassFilterPlotAction;
import core.action.PulsationPlotAction;
import core.action.GetSignalFrecuencyAction;
import core.service.io.OpenFileService;
import presentation.presenter.Presenter.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPresenter extends Presenter<View> {

    private final PulsationPlotAction pulsationPlotAction;
    private final LowPassFilterPlotAction lowPassFilterPlotAction;
    private final GetSignalFrecuencyAction getSignalFrecuencyAction;
    private GetFileCsvPointsAction getFileCsvPointsAction;

    public MainPresenter(GetFileCsvPointsAction getFileCsvPointsAction,
                         PulsationPlotAction pulsationPlotAction,
                         LowPassFilterPlotAction lowPassFilterPlotAction,
                         GetSignalFrecuencyAction getSignalFrecuencyAction) {

        this.getFileCsvPointsAction = getFileCsvPointsAction;
        this.pulsationPlotAction = pulsationPlotAction;
        this.lowPassFilterPlotAction = lowPassFilterPlotAction;
        this.getSignalFrecuencyAction = getSignalFrecuencyAction;
    }

    public void onClickOpenPulsationPlot() {
        pulsationPlotAction.execute(loadFileAsList());
    }

    public void onClickLowPassFilterApply(Integer frecuency, Integer order) {
        lowPassFilterPlotAction.execute(loadFileAsMap(), frecuency, order);
    }

    public String onClickLowPassFilterPlot() {
        return getSignalFrecuencyAction.execute(loadFileAsMap());
    }

    private Map<String, String> loadFileAsMap() {

        try {
            return getFileCsvPointsAction.getStringArrayListDataFileCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
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
