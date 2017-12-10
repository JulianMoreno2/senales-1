package core.provider;

import javafx.stage.FileChooser;
import core.handler.OpenLowPassFilterPlotEventHandler;
import core.service.PulsationService;
import core.service.filter.LowPassFilterService;
import core.service.io.FileIOService;
import core.service.io.OpenFileService;

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
    
    public static LowPassFilterService provideLowPassFilterService() {
    	return new LowPassFilterService();
    }
    
    public static PulsationService providePulsationService() {
    	return new PulsationService();
    }
    
    public static OpenFileService provideOpenFileService() {
    	return new OpenFileService(provideFileChooser("Open File"), provideIOService(), 
    			UtilProvider.provideOpenFilePublishSubject());
    }
}
