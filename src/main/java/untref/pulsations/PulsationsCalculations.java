package untref.pulsations;

import java.util.ArrayList;

public class PulsationsCalculations {
	private ArrayList<Double> arrayDatafileCsv;
	private ArrayList<Double> ArrhythmiaPoint;

	public PulsationsCalculations(ArrayList<Double> arrayDatafileCsv) {
		this.arrayDatafileCsv = arrayDatafileCsv;

	}

	public int getPulsationsQuantity() {

		double threshold = 1.2;
		int firstPeak = 0;
		int secondPeak = 0;

		for (int index = 1; index < this.arrayDatafileCsv.size() - 1; index++) {

			if ((this.arrayDatafileCsv.get(index - 1) > this.arrayDatafileCsv.get(index))
					&& (this.arrayDatafileCsv.get(index) > this.arrayDatafileCsv.get(index + 1))
					&& (this.arrayDatafileCsv.get(index) > threshold))
				if (firstPeak == 0)
					firstPeak = (index);
				else if (secondPeak == 0)
					secondPeak = (index);
		}
		double period = ((secondPeak - firstPeak) * 0.01);
		int value = (int) (60 / period);
		return value;
	}

	public int getArrhythmiaQuantityPoint() {
		int count = 0;
		double threshold = 1.2;
		double thresholdArrhytmia = 0.3;
		int firstPeak = 0;
		int secondPeak = 0;
		ArrhythmiaPoint = new ArrayList<Double>();

		for (int index = 1; index < this.arrayDatafileCsv.size() - 1; index++) {

			if ((this.arrayDatafileCsv.get(index - 1) > this.arrayDatafileCsv.get(index))
					&& (this.arrayDatafileCsv.get(index) > this.arrayDatafileCsv.get(index + 1))
					&& (this.arrayDatafileCsv.get(index) > threshold)) {
				if (firstPeak == 0)
					firstPeak = (index);
				else if (secondPeak == 0)
					secondPeak = (index);
			}  if ((this.arrayDatafileCsv.get(index) < thresholdArrhytmia)) {
				count++;
				ArrhythmiaPoint.add(this.arrayDatafileCsv.get(index));
			}
		}

		return count;
	}

	public ArrayList<Double> getArrhythmiaPoint() {
		return ArrhythmiaPoint;
	}

}
