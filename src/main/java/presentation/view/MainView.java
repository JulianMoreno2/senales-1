package presentation.view;

import core.provider.PresenterProvider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import presentation.presenter.MainPresenter;

public class MainView extends Application implements MainPresenter.View {
	
	boolean lowPassFilterIsActive = true;

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 400, 200, Color.WHITE);

        MainPresenter mainPresenter = PresenterProvider.provideMainPresenter();

        TilePane tilePane = createButtonTile(mainPresenter);

        borderPane.setCenter(tilePane);
        primaryStage.setTitle("Procesamiento de Senales II");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TilePane createButtonTile(MainPresenter mainPresenter) {
        ToggleButton pulsationPlotToggleButton = new ToggleButton("Pulsation Plot");
        pulsationPlotToggleButton.setOnAction(mainPresenter.onClickOpenPulsationPlot());
        TilePane pulsationTilePane = new TilePane();
		pulsationTilePane.getChildren().add(pulsationPlotToggleButton);

        ToggleButton lowPassFilterToggleButton = new ToggleButton("Low Pass Filter Plot");
       
        Button lowPassFilterButton = new Button("Apply");
        lowPassFilterButton.setDisable(lowPassFilterIsActive);
        
        Label frecuencyLabel = new Label("Frecuency");
        TextField frecuencyTextField = new TextField ();
        frecuencyLabel.setDisable(lowPassFilterIsActive);
        frecuencyTextField.setDisable(lowPassFilterIsActive);
        frecuencyTextField.appendText(mainPresenter.getSignalFrecuency());
        
        Label orderLabel = new Label("Order");
        TextField orderTextField = new TextField ();
        orderLabel.setDisable(lowPassFilterIsActive);
        orderTextField.setDisable(lowPassFilterIsActive);
        orderTextField.setPromptText("0");
        
		lowPassFilterToggleButton.setOnAction(event -> {
			lowPassFilterButton.setDisable(!lowPassFilterIsActive);
	        orderTextField.setDisable(!lowPassFilterIsActive);
	        lowPassFilterIsActive = !lowPassFilterIsActive;
		});
				
		lowPassFilterButton.setOnAction(mainPresenter.onClickOpenLowPassFilterPlot(
				Integer.getInteger(frecuencyTextField.getText()), 
				Integer.getInteger(orderTextField.getText())));
		
		TilePane lowPassFilterTilePane = new TilePane();
		lowPassFilterTilePane.getChildren().addAll(lowPassFilterToggleButton, lowPassFilterButton);
		TilePane frecuencyTilePane = new TilePane();
		frecuencyTilePane.getChildren().addAll(frecuencyLabel, frecuencyTextField);
		TilePane orderTilePane = new TilePane();
		orderTilePane.getChildren().addAll(orderLabel, orderTextField);
				
        TilePane tilePane = new TilePane();
        tilePane.getChildren().addAll(pulsationTilePane, lowPassFilterTilePane, frecuencyTilePane, orderTilePane);

        return tilePane;
    }
}
