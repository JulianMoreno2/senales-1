package core.action;

import java.util.List;

import core.repository.FilterRepository;

public class SaveFilterAction {
	
	private final FilterRepository filterRepository;
	
	public SaveFilterAction(FilterRepository filterRepository) {
		this.filterRepository = filterRepository;
	}

	public void execute(String name, List<Double> data) {
		filterRepository.save(name, data);
	}

}
