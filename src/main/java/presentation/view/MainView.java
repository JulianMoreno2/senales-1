package presentation.view;

import core.provider.PresenterProvider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import presentation.presenter.MainPresenter;

public class MainView extends Application implements MainPresenter.View {

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 400, 200, Color.WHITE);

        MainPresenter mainPresenter = PresenterProvider.provideMainPresenter();
        MenuBar menuBar = new MenuBarBuilder(mainPresenter.onClickOpenFile()).build();

        TilePane tilePane = createButtonTile(mainPresenter);

        borderPane.setTop(menuBar);
        borderPane.setCenter(tilePane);

        primaryStage.setTitle("Procesamiento de Senales II");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TilePane createButtonTile(MainPresenter mainPresenter) {
        ToggleButton togglePulsationPlotButton = new ToggleButton("Pulsation Plot");
        togglePulsationPlotButton.setOnAction(mainPresenter.onClickOpenPulsationPlot());

        ToggleButton toggleLowPassFilterButton = new ToggleButton("Low Pass Filter Plot");
        toggleLowPassFilterButton.setOnAction(mainPresenter.onClickOpenLowPassFilterPlot());

        TilePane tilePane = new TilePane();
        tilePane.getChildren().addAll(togglePulsationPlotButton, toggleLowPassFilterButton);

        return tilePane;
    }
}
