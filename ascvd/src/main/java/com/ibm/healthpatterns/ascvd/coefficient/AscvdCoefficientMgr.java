package com.ibm.healthpatterns.ascvd.coefficient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public enum AscvdCoefficientMgr {

	INSTANCE;
	
	Map<AscvdCoefficientKey, Double> coefficients;
	
	private AscvdCoefficientMgr() {
		loadMap();
	}
	
	public Double getCoefficient(boolean isMale, boolean isAfricanAmerican, AscvdFeature feature) {
		return coefficients.get(new AscvdCoefficientKey(isMale, isAfricanAmerican, feature));
	}
	
	private void loadMap() {
		try {
			CSVParser parser = CSVParser.parse(
				this.getClass().getClassLoader().getResource("coefficients.csv"), 
				Charset.defaultCharset(),
				CSVFormat.DEFAULT
					.withHeader("is_male", "is_african_american", "feature", "coefficient")
					.withSkipHeaderRecord(true)
			);
			
			final Map<AscvdCoefficientKey, Double> coefficients = new HashMap<AscvdCoefficientKey, Double>();
			parser.getRecords().stream().forEach(record -> {
				AscvdCoefficientKey key = new AscvdCoefficientKey(
					Boolean.parseBoolean(record.get("is_male")), 
					Boolean.parseBoolean(record.get("is_african_american")),
					AscvdFeature.valueOf(record.get("feature"))
				);
				double value = Double.parseDouble(record.get("coefficient"));
				coefficients.put(key, value);
			});			
			parser.close();
			this.coefficients = Collections.unmodifiableMap(coefficients);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
