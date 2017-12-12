package core.provider;

import presentation.presenter.MainPresenter;

public class PresenterProvider {

    public static MainPresenter provideMainPresenter() {
        return new MainPresenter(
                ActionProvider.provideGetFileCsvPointsAction(),
                ActionProvider.providePulsationPlotAction(),
                ActionProvider.provideLowPassFilterPlotAction(),
                ActionProvider.provideGetSignalFrecuencyAction()
        );
    }
}
