package core.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileCsv {
    private String path;
    private ArrayList<Double> arrayDatafileCsv;

    public FileCsv(String path) {
        this.arrayDatafileCsv = new ArrayList<>();
        this.path = path;
    }

    private void read() throws IOException {

        String registry;
        BufferedReader buffer = null;

        try {

            buffer = new BufferedReader(new FileReader(this.path));

            while ((registry = buffer.readLine()) != null) {

                StringTokenizer dividir = new StringTokenizer(registry, ",");
                this.fill(dividir);
            }

            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fill(StringTokenizer stringTokenizer) {
        while (stringTokenizer.hasMoreTokens()) {
            String dato = stringTokenizer.nextToken();
            this.arrayDatafileCsv.add(Double.parseDouble(dato));
        }
    }

    public ArrayList<Double> getArrayListDataFileCsv() throws IOException {
        this.read();
        return this.arrayDatafileCsv;
    }

}
