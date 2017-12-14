package core.service.filter;

import java.util.List;

import core.function.LinearConvolve;
import core.model.Complex;

public class LowPassFilterService {
	
	private final LinearConvolve linearConvolve;
	
	public LowPassFilterService(LinearConvolve linearConvolve) {
		this.linearConvolve = linearConvolve;
	}

	public List<Double> apply(List<Double> data, List<Double> filterData, Integer frecuency, Integer order) {

		Complex[] dataAsComplex = toComplex(data);
		Complex[] filterDataAsComplex = toComplex(filterData);
		
		Complex[] filteredData = linearConvolve.build(dataAsComplex, filterDataAsComplex);
		
		return data;
	}
	
	public Complex[] toComplex(List<Double> points) {
		
		Complex[] functionPoints = new Complex[points.size()];
		
		for (int i = 0; i < functionPoints.length; i++) {
            functionPoints[i] = new Complex(points.get(i), 0);
        }
        
        return functionPoints;
	}
}
