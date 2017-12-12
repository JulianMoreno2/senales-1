package core.action;

import core.service.io.OpenFileService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GetFileCsvPointsAction {

    private OpenFileService openFileService;

    public GetFileCsvPointsAction(OpenFileService openFileService) {
        this.openFileService = openFileService;
    }

    public ArrayList<Double> getArrayListDataFileCsv() throws IOException {

        String path = getFilePath();

        String registry;
        BufferedReader buffer = null;
        ArrayList<Double> arrayDatafileCsv = new ArrayList<>();

        try {

            buffer = new BufferedReader(new FileReader(path));

            while ((registry = buffer.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(registry, ",");
                while (stringTokenizer.hasMoreTokens()) {
                    String data = stringTokenizer.nextToken();
                    arrayDatafileCsv.add(Double.parseDouble(data));
                }
            }

            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayDatafileCsv;
    }

    public Map<String, String> getStringArrayListDataFileCsv() throws IOException {

        String path = getFilePath();

        String registry;
        BufferedReader buffer = null;
        Map<String, String> dataFile = new LinkedHashMap<>();

        try {

            buffer = new BufferedReader(new FileReader(path));

            while ((registry = buffer.readLine()) != null) {
                String[] data = registry.split(",");
                dataFile.put(data[0], data[1]);
            }

            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataFile;
    }

    private String getFilePath() {
        return openFileService.openFile().getAbsolutePath();
    }

}
