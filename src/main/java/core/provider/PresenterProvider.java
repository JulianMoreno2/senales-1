package core.provider;

import io.reactivex.subjects.PublishSubject;
import presentation.presenter.MainPresenter;

public class PresenterProvider {

    public static MainPresenter provideMainPresenter() {
        return new MainPresenter(ServiceProvider.provideFileChooser("Open File"),
                ServiceProvider.provideIOService(), PublishSubject.create());
    }

}
