package untref.repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileCsv {
	private String path;
	private ArrayList<Double> arrayDatafileCsv;

	public FileCsv() {

		this.arrayDatafileCsv = new ArrayList<>();
	}

	public void setPathArchivo(String path) {
		this.path = path;
	}

	public String getPathArchivo() {
		return this.path;
	}

	private void leerRegistro() throws IOException {
		String registro = null;
		BufferedReader buffer = null;
		try {
			buffer = new BufferedReader(new FileReader(this.path));

			while ((registro = buffer.readLine()) != null) {

				if (registro != null) {
					StringTokenizer dividir = new StringTokenizer(registro, ",");
					this.llenarDatos(dividir);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.close();
	}

	private void llenarDatos(StringTokenizer dividir) {

		while (dividir.hasMoreTokens()) {
			String dato = dividir.nextToken();
			this.arrayDatafileCsv.add(Double.parseDouble(dato));
		}

	}

	public ArrayList<Double> getArrayListDataFileCsv() {
		try {
			this.leerRegistro();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return this.arrayDatafileCsv;
	}

}
