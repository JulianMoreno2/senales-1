package core.provider;

import java.io.File;

import javafx.stage.FileChooser;
import core.service.pulsation.PulsationService;
import core.service.filter.LowPassFilterService;
import core.service.io.OpenFileService;

public class ServiceProvider {

    public static FileChooser provideFileChooser(String title) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File("./src/main/resources"));
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
                           new FileChooser.ExtensionFilter("CSV", "*.csv"));
        return fileChooser;
    }

    public static LowPassFilterService provideLowPassFilterService() {
    	return new LowPassFilterService(FunctionProvider.provideLinearConvolve());
    }
    
    public static PulsationService providePulsationService() {
    	return new PulsationService();
    }
    
    public static OpenFileService provideOpenFileService() {
    	return new OpenFileService(provideFileChooser("Open File"));
    }
}
