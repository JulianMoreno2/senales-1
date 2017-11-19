package presentation.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

class MenuBarBuilder {

    private EventHandler<ActionEvent> handler;

    public MenuBarBuilder(EventHandler<ActionEvent> handler) {
        this.handler = handler;
    }

    public MenuBar build() {
        MenuBar menuBar = new MenuBar();
        MenuItem openFileMenuItem = createOpenMenuItem();
        Menu fileMenu = createMenu(openFileMenuItem);

        openFileMenuItem.setOnAction(handler);

        menuBar.getMenus().addAll(fileMenu);
        return menuBar;
    }

    private Menu createMenu(MenuItem... items) {
        Menu menu = new Menu("File");
        menu.getItems().setAll(items);
        return menu;
    }

    private MenuItem createOpenMenuItem() {
        return new MenuItem("Open");
    }
}