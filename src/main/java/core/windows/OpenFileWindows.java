package core.windows;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import core.action.GetFileCsvPointsAction;
import core.service.io.OpenFileService;
import javafx.stage.FileChooser;

public class OpenFileWindows {
	String name;

	public List<Double> getWindowDataFile() throws IOException {
		String registry;
		BufferedReader buffer = null;
		ArrayList<Double> data = new ArrayList<>();

		try {
			GetFileCsvPointsAction file = this.SelectFile();
			buffer = new BufferedReader(new FileReader(file.getFileName()));
			this.name = file.getFileName();
			while ((registry = buffer.readLine()) != null) {

				if (registry != "") {
					data.add(Double.parseDouble(registry));
				}
			}

			buffer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		data.trimToSize();
		return data;
	}

	public GetFileCsvPointsAction SelectFile() {

		FileChooser fileChooser = new FileChooser();

		GetFileCsvPointsAction fileCvs = new GetFileCsvPointsAction(new OpenFileService(fileChooser));

		return fileCvs;

	}

	public List<Double> loadNewFile() {

		try {
			Map<String, String> map = this.SelectFile().getDataFileCsvAsMap();

			List<Map.Entry<String, String>> entries = new ArrayList<>(map.entrySet());
			return entries.subList(2, entries.size()).stream().map(Map.Entry::getValue).map(Double::parseDouble)
					.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	public String getName() {
		return this.name;

	}

}
