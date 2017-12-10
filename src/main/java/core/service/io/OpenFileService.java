package core.service.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OpenFileService {

	private FileChooser fileChooser;
	private FileIOService fileIOService;
	private PublishSubject<ArrayList<FileCsv>> publishSubject;

	public OpenFileService(FileChooser fileChooser, FileIOService fileIOService,
			PublishSubject<ArrayList<FileCsv>> publishSubject) {
		this.fileChooser = fileChooser;
		this.fileIOService = fileIOService;
		this.publishSubject = publishSubject;
	}

	public void openFile() {

		ArrayList<FileCsv> fileCsvs = new ArrayList<>();
		List<File> file = fileIOService.openFiles(fileChooser);

		for (File filess : file) {
			FileCsv files = new FileCsv(filess.getPath());
			fileCsvs.add(files);
		}

		publishSubject.onNext(fileCsvs);
	}

}
