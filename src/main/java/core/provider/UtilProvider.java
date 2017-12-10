package core.provider;

import java.util.ArrayList;

import core.service.io.FileCsv;
import io.reactivex.subjects.PublishSubject;

public class UtilProvider {
	
	public static PublishSubject<ArrayList<FileCsv>>openFilePublishSubject;

	public static PublishSubject<ArrayList<FileCsv>> provideOpenFilePublishSubject() {
		if(openFilePublishSubject == null) {
			openFilePublishSubject = PublishSubject.create();
			return openFilePublishSubject;
		}
		return openFilePublishSubject;
	}

}
