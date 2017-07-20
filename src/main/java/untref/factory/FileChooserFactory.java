package untref.factory;

import javafx.stage.FileChooser;


public class FileChooserFactory {

	public FileChooser create(String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),new FileChooser.ExtensionFilter("CSV", "*.csv"));
		return fileChooser;
	}
}
