package untref.interfacebuilders;

import java.util.ArrayList;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import untref.eventhandlers.OpenFilesEventHandler;
import untref.eventhandlers.OpenPlotEventHandler;
import untref.factory.FileChooserFactory;
import untref.repository.FileCsv;
import untref.service.FileIOService;

public class MenuBarBuilder {

	private FileIOService fileIOService;
	private ArrayList<FileCsv> arrayfileCsv;
	private FileChooserFactory fileChooserFactory;

	public MenuBarBuilder( ) {
		this.fileIOService = new FileIOService();
		this.fileChooserFactory = new FileChooserFactory();
		
		this.arrayfileCsv = new ArrayList<FileCsv>();

	}

	public MenuBar build() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = createFileMenu();
		Menu plotMenu = this.createPlotMenu();

		menuBar.getMenus().addAll(fileMenu, plotMenu);
		return menuBar;
	}

	private Menu createFileMenu() {
		Menu fileMenu = new Menu("File");
		MenuItem fileMenuItemOpen = createOpenMenuItem();

		fileMenu.getItems().addAll(fileMenuItemOpen);
		return fileMenu;
	}

	private Menu createPlotMenu() {
		Menu plotMenu = new Menu("Plot");
		MenuItem menuItemOpen = createOpenPlotMenuItem();

		plotMenu.getItems().addAll(menuItemOpen);
		return plotMenu;
	}

	private MenuItem createOpenPlotMenuItem() {
		MenuItem plotMenuItem = new MenuItem("open plot");

		setOpenPlotEvent(plotMenuItem,this.arrayfileCsv);

		return plotMenuItem;
	}

	private MenuItem createOpenMenuItem() {
		MenuItem fileMenuItem = new MenuItem("open ..");
		final FileChooser fileChooser = fileChooserFactory.create("open file");
		setOpenEvent(fileMenuItem, fileChooser,this.arrayfileCsv);
		return fileMenuItem;
	}

	private void setOpenEvent(MenuItem fileMenuItem, final FileChooser fileChooser,ArrayList<FileCsv> arrayfileCsv) {
		fileMenuItem.setOnAction(new OpenFilesEventHandler(fileChooser, fileIOService,arrayfileCsv));
	}

	private void setOpenPlotEvent(MenuItem plotMenuItem,ArrayList<FileCsv> arrayfileCsv) {
		plotMenuItem.setOnAction(new OpenPlotEventHandler(arrayfileCsv));
	}

}