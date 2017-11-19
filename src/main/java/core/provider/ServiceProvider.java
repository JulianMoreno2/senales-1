package core.provider;

import javafx.stage.FileChooser;
import core.service.FileIOService;

public class ServiceProvider {

    public static FileChooser provideFileChooser(String title) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter("All Files", "*.*"), new FileChooser.ExtensionFilter("CSV", "*.csv"));
        return fileChooser;
    }

    public static FileIOService provideIOService() {
        return new FileIOService();
    }
}
