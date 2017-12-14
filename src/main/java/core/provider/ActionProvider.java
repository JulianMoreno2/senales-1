package core.provider;

import core.action.GetFileCsvPointsAction;
import core.action.GetSignalFrecuencyAction;
import core.action.LowPassFilterPlotAction;
import core.action.PulsationPlotAction;
import core.action.SaveFilterAction;

public class ActionProvider {

    public static PulsationPlotAction providePulsationPlotAction() {
        return new PulsationPlotAction(ServiceProvider.providePulsationService());
    }

    public static LowPassFilterPlotAction provideLowPassFilterPlotAction() {
        return new LowPassFilterPlotAction(ServiceProvider.provideLowPassFilterService(),
        		RepositoryProvider.provideFilterRepository());
    }

    public static GetSignalFrecuencyAction provideGetSignalFrecuencyAction() {
        return new GetSignalFrecuencyAction();
    }

    public static GetFileCsvPointsAction provideGetFileCsvPointsAction() {
        return new GetFileCsvPointsAction(ServiceProvider.provideOpenFileService());
    }

	public static SaveFilterAction provideSaveFilterAction() {
		return new SaveFilterAction(RepositoryProvider.provideFilterRepository());
	}
}
