package presentation.view;

import core.provider.PresenterProvider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import presentation.presenter.MainPresenter;

public class MainView extends Application implements MainPresenter.View {

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 400, 100, Color.WHITE);

        MainPresenter mainPresenter = PresenterProvider.provideMainPresenter();
        MenuBar menuBar = new MenuBarBuilder(mainPresenter.onClickOpenFile()).build();

        ToggleButton toggleButton = new ToggleButton("Plot");
        toggleButton.setOnAction(mainPresenter.onClickOpenPlot());

        borderPane.setTop(menuBar);
        borderPane.setCenter(toggleButton);
        primaryStage.setTitle("Procesamiento de Senales II");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
