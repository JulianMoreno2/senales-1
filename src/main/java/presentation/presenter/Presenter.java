package presentation.presenter;

abstract class Presenter<T extends Presenter.View> {

    private T view;

    public T getView() {
        return view;
    }

    public void setView(T view) {
        this.view = view;
    }

    public interface View {

    }
}
