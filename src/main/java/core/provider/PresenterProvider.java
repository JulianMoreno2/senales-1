package core.provider;

import presentation.presenter.MainPresenter;

public class PresenterProvider {

    public static MainPresenter provideMainPresenter() {
        return new MainPresenter(UtilProvider.provideOpenFilePublishSubject());
    }

}
