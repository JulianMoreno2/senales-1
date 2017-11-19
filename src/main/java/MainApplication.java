import javafx.application.Application;
import presentation.presenter.MainPresenter;
import presentation.view.MainView;

public class MainApplication {

    public static void main(String[] args) {
        Application.launch(((Application) new MainView()).getClass());
    }

}
