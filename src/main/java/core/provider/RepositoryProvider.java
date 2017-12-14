package core.provider;

import core.repository.FilterRepository;

public class RepositoryProvider {
	
	private static FilterRepository instance;

	public static FilterRepository provideFilterRepository() {
		if(instance == null) {
			instance = new FilterRepository();
		}
		return instance;
	}
}
