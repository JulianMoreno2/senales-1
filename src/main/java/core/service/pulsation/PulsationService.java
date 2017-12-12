package core.service.pulsation;

import java.util.ArrayList;
import java.util.List;

public class PulsationService {

    public int getPulsationsQuantity(List<Double> arrayDatafileCsv) {
        double threshold = 1.2;
        int firstPeak = 0;
        int secondPeak = 0;

        for (int index = 1; index < arrayDatafileCsv.size() - 1; index++) {

            if ((arrayDatafileCsv.get(index - 1) > arrayDatafileCsv.get(index))
                    && (arrayDatafileCsv.get(index) > arrayDatafileCsv.get(index + 1))
                    && (arrayDatafileCsv.get(index) > threshold)) {
                if (firstPeak == 0) {
                    firstPeak = (index);
                } else if (secondPeak == 0) {
                    secondPeak = (index);
                }
            }
        }
        double period = ((secondPeak - firstPeak) * 0.01);
        return (int) (60 / period);
    }

    public int getArrhythmiaQuantityPoint(List<Double> arrayDatafileCsv) {
        int count = 0;
        double threshold = 1.2;
        double thresholdArrhytmia = 0.3;
        int firstPeak = 0;
        int secondPeak = 0;
        ArrayList<Double> arrhythmiaPoint = new ArrayList<>();

        for (int index = 1; index < arrayDatafileCsv.size() - 1; index++) {

            if ((arrayDatafileCsv.get(index - 1) > arrayDatafileCsv.get(index))
                    && (arrayDatafileCsv.get(index) > arrayDatafileCsv.get(index + 1))
                    && (arrayDatafileCsv.get(index) > threshold)) {
                if (firstPeak == 0) {
                    firstPeak = (index);
                } else if (secondPeak == 0) {
                    secondPeak = (index);
                }
            }
            if ((arrayDatafileCsv.get(index) < thresholdArrhytmia)) {
                count++;
                arrhythmiaPoint.add(arrayDatafileCsv.get(index));
            }
        }

        return count;
    }
}
