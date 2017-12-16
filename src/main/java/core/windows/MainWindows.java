package core.windows;

import java.io.IOException;
import java.util.List;
import core.provider.PlotterAppProvider;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindows extends Application {
	List<Double> dataSignal = null;
	List<Double> dataWindow = null;
	String nameSignal;
	String nameWindows;
	OpenFileWindows openFile;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Windows");
		Scene scene = new Scene(new Group());
		stage.setWidth(250);
		stage.setHeight(200);
		HBox hbox = new HBox();
		VBox vbox = new VBox();

		final ToggleGroup group = new ToggleGroup();

		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {

			}
		});

		getButtonSignal(vbox);
		getButtonWindows(vbox);

		vbox.setSpacing(10);

		hbox.getChildren().add(vbox);
		hbox.setSpacing(50);
		hbox.setPadding(new Insets(20, 10, 10, 20));

		((Group) scene.getRoot()).getChildren().add(hbox);
		stage.setScene(scene);
		stage.show();
	}

	private VBox getButtonSignal(VBox vbox) {

		Button buttonLoadSignal = new Button("Load Signal");
		buttonLoadSignal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				dataSignal = new OpenFileWindows().loadNewFile();
			}
		});
		Button buttonPlotSignal = new Button("Plot Signal");
		buttonPlotSignal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				try {
					PlotterAppProvider.provide().startPulsationPlot(dataSignal, nameSignal);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		vbox.getChildren().add(buttonLoadSignal);
		vbox.getChildren().add(buttonPlotSignal);
		return vbox;
	}

	private VBox getButtonWindows(VBox vbox) {

		Button buttonLoadWindow = new Button("Load Windows");
		buttonLoadWindow.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				try {
					  openFile=new OpenFileWindows();
					dataWindow =   openFile.getWindowDataFile();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		Button buttonPlotWindow = new Button("Plot Windows-Signal");
		buttonPlotWindow.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					PlotterAppProvider.provide().startPulsationPlot(
							new OperationWindowsSignal().windowSignalMultiplication(dataSignal, dataWindow),openFile.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		vbox.getChildren().add(buttonLoadWindow);
		vbox.getChildren().add(buttonPlotWindow);
		return vbox;
	}


}