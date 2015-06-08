package com.github.onsdigital.generator.datasets;

import com.github.onsdigital.content.statistic.Dataset;

import java.util.ArrayList;
import java.util.List;

class DatasetList {

	List<Dataset> datasets;

	public void add(Dataset dataset) {
		if (datasets == null) {
			datasets = new ArrayList<>();
		}
		datasets.add(dataset);
	}

}
