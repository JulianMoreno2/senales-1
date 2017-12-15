package core.windows;

import java.util.ArrayList;
import java.util.List;

public class OperationWindowsSignal {
	
	public List<Double> windowSignalMultiplication(List<Double> dataSignal,List<Double> dataWindow) {
		int indexSignal = 0;
		int indexWindows = 0;
		List<Double> data = new ArrayList<>();
		double pointMultiplication;
		while (indexSignal < dataSignal.size()) {
			pointMultiplication =  dataSignal.get(indexSignal) *  dataWindow.get(indexWindows);
			data.add(pointMultiplication);
			indexSignal++;
			indexWindows++;
			if (indexWindows > (dataWindow.size()-1))
				indexWindows = 0;

		}
		return data;

	}

}
