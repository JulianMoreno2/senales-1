package presentation.view;

import core.provider.PresenterProvider;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
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

        HBox hBox = createHBox(mainPresenter);

        borderPane.setCenter(hBox);
        primaryStage.setTitle("Procesamiento de Senales II");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHBox(MainPresenter mainPresenter) {

        TilePane pulsationTilePane = createPulsationPlotTilePane(mainPresenter);
        TilePane lowPassTilePane = createLowPassFilterTilePane(mainPresenter);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(pulsationTilePane, lowPassTilePane);

        return hBox;
    }

    private TilePane createPulsationPlotTilePane(MainPresenter mainPresenter) {

        ToggleButton pulsationPlotToggleButton = new ToggleButton("Pulsation Plot");
        pulsationPlotToggleButton.setOnAction(event -> mainPresenter.onClickOpenPulsationPlot());
        TilePane pulsationTilePane = new TilePane();
        pulsationTilePane.getChildren().add(pulsationPlotToggleButton);

        return pulsationTilePane;
    }

    private TilePane createLowPassFilterTilePane(MainPresenter mainPresenter) {

        Label frecuencyLabel = new Label("Frecuency");
        TextField frecuencyTextField = new TextField();
        frecuencyTextField.setDisable(lowPassFilterIsActive);
        frecuencyTextField.appendText("0");

        Label orderLabel = new Label("Order");
        TextField orderTextField = new TextField();
        orderTextField.setDisable(lowPassFilterIsActive);
        orderTextField.appendText("0");

        ToggleButton lowPassFilterToggleButton = new ToggleButton("Low Pass Filter Plot");
        Button lowPassFilterApplyButton = new Button("Apply");
        lowPassFilterApplyButton.setDisable(lowPassFilterIsActive);
        
        Button plotSignalButton = new Button("Plot Signal");
        plotSignalButton.setOnAction(event -> {
        	mainPresenter.onClickPlotSignal();
        });

        lowPassFilterToggleButton.setOnAction(event -> {
        	mainPresenter.onClickLowPassFilterPlot();
            lowPassFilterApplyButton.setDisable(!lowPassFilterIsActive);
            orderTextField.setDisable(!lowPassFilterIsActive);
            lowPassFilterIsActive = !lowPassFilterIsActive;
        });

        lowPassFilterApplyButton.setOnAction(event -> mainPresenter.onClickLowPassFilterApply(
                Integer.getInteger(frecuencyTextField.getText()),
                Integer.getInteger(orderTextField.getText())));

        TilePane lowPassFilterTilePane = new TilePane();
        
		lowPassFilterTilePane.getChildren().addAll(lowPassFilterToggleButton, lowPassFilterApplyButton, plotSignalButton);

        TilePane frecuencyTilePane = new TilePane();
        frecuencyTilePane.getChildren().addAll(frecuencyLabel, frecuencyTextField);

        TilePane orderTilePane = new TilePane();
        orderTilePane.getChildren().addAll(orderLabel, orderTextField);

        TilePane lowPassTilePane = new TilePane();
        lowPassTilePane.getChildren().addAll(lowPassFilterTilePane, frecuencyTilePane, orderTilePane);

        return lowPassTilePane;
    }
}
