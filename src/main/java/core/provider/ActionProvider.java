package core.provider;

import core.action.GetFileCsvPointsAction;
import core.action.GetSignalFrecuencyAction;
import core.action.LowPassFilterPlotAction;
import core.action.PulsationPlotAction;

public class ActionProvider {

    public static PulsationPlotAction providePulsationPlotAction() {
        return new PulsationPlotAction(ServiceProvider.providePulsationService());
    }

    public static LowPassFilterPlotAction provideLowPassFilterPlotAction() {
        return new LowPassFilterPlotAction(ServiceProvider.provideLowPassFilterService());
    }

    public static GetSignalFrecuencyAction provideGetSignalFrecuencyAction() {
        return new GetSignalFrecuencyAction();
    }

    public static GetFileCsvPointsAction provideGetFileCsvPointsAction() {
        return new GetFileCsvPointsAction(ServiceProvider.provideOpenFileService());
    }
}
