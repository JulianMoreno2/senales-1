package core.service.io;

import java.io.File;

import javafx.stage.FileChooser;

public class OpenFileService {

	private final FileChooser fileChooser;

	public OpenFileService(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public File openFile() {
		return fileChooser.showOpenDialog(null);
	}

}
