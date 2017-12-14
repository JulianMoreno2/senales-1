package core.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterRepository {

	private Map<String, List<Double>> filters;
	
	public FilterRepository() {
		filters = new HashMap<String, List<Double>>();
	}

	public void save(String name, List<Double> data) {
		filters.put(name, data);	
	}
	
	public List<Double> get(String name) {
		return filters.get(name);
	}

}
