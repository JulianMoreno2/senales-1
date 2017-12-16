package presentation.view;

import core.provider.PresenterProvider;
import core.windows.MainWindows;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import presentation.presenter.MainPresenter;

public class MainView extends Application implements MainPresenter.View {

    private boolean lowPassFilterIsActive = true;

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 600, 200, Color.WHITE);

        MainPresenter mainPresenter = PresenterProvider.provideMainPresenter();

        FlowPane flowPane = createHBox(mainPresenter);

        borderPane.setCenter(flowPane);
        primaryStage.setTitle("Procesamiento de Senales II");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private FlowPane createHBox(MainPresenter mainPresenter) {

        TilePane pulsationTilePane = createPulsationPlotTilePane(mainPresenter);
        TilePane lowPassTilePane = createLowPassFilterTilePane(mainPresenter);
        TilePane createPlotSignalTilePane = createPlotSignalTilePane(mainPresenter);
        TilePane createPlotWindowTilePane = createPlotWindowTilePane();

        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(10, 10, 10, 10));
        flowPane.setVgap(10);
        flowPane.setHgap(5);
        flowPane.setPrefWrapLength(200);

        flowPane.getChildren().addAll(pulsationTilePane, lowPassTilePane, createPlotSignalTilePane, createPlotWindowTilePane);

        return flowPane;
    }

    private TilePane createPulsationPlotTilePane(MainPresenter mainPresenter) {

        ToggleButton pulsationPlotToggleButton = new ToggleButton("Pulsation Plot");
        pulsationPlotToggleButton.setOnAction(event -> mainPresenter.onClickOpenPulsationPlot());
        TilePane pulsationTilePane = new TilePane();
        pulsationTilePane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pulsationTilePane.getChildren().add(pulsationPlotToggleButton);

        return pulsationTilePane;
    }

    private TilePane createLowPassFilterTilePane(MainPresenter mainPresenter) {

        ToggleButton lowPassFilterToggleButton = new ToggleButton("Filter");
        Button lowPassFilterApplyButton = new Button("Apply to Signal");
        lowPassFilterApplyButton.setDisable(lowPassFilterIsActive);

        lowPassFilterToggleButton.setOnAction(event -> {
            mainPresenter.onClickFilterPlot();
            lowPassFilterApplyButton.setDisable(!lowPassFilterIsActive);
            lowPassFilterIsActive = !lowPassFilterIsActive;
        });

        lowPassFilterApplyButton.setOnAction(event -> mainPresenter.onClickFilterApply());

        TilePane lowPassFilterTilePane = new TilePane();
        lowPassFilterTilePane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        lowPassFilterTilePane.getChildren().addAll(lowPassFilterToggleButton, lowPassFilterApplyButton);

        return lowPassFilterTilePane;
    }

    private TilePane createPlotSignalTilePane(MainPresenter mainPresenter) {

        Button plotSignalButton = new Button("Plot Signal");
        plotSignalButton.setOnAction(event -> mainPresenter.onClickPlotSignal());

        TilePane plotSignalTilePane = new TilePane();
        plotSignalTilePane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        plotSignalTilePane.getChildren().add(plotSignalButton);

        return plotSignalTilePane;
    }

    private TilePane createPlotWindowTilePane() {
        Button plotWindowslButton = new Button("Plot Windows");
        plotWindowslButton.setOnAction((event -> {
            MainWindows mainWindows = new MainWindows();
            Stage stage = new Stage();
            mainWindows.start(stage);
        }));

        TilePane plotSignalTilePane = new TilePane();
        plotSignalTilePane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        plotSignalTilePane.getChildren().add(plotWindowslButton);

        return plotSignalTilePane;
    }
}
