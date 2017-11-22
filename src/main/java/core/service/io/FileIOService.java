package core.service.io;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class FileIOService {

	public List<File> openFiles(FileChooser fileChooser) {
		return fileChooser.showOpenMultipleDialog(null);
	}
}