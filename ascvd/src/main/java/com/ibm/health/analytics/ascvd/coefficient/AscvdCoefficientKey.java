package com.ibm.health.analytics.ascvd.coefficient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AscvdCoefficientKey {

	boolean isMale;
	boolean isAfricanAmerican;
	AscvdFeature feature;
	
	public AscvdCoefficientKey(boolean isMale, boolean isAfricanAmerican, AscvdFeature feature) {
		this.isMale = isMale;
		this.isAfricanAmerican = isAfricanAmerican;
		this.feature = feature;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(isMale)
			.append(isAfricanAmerican)
			.append(feature)
		.build();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AscvdCoefficientKey == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		AscvdCoefficientKey other = (AscvdCoefficientKey) obj;
		return new EqualsBuilder()
			.append(isMale, other.isMale)
			.append(isAfricanAmerican, other.isAfricanAmerican)
			.append(feature, other.feature)
		.build();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append(isMale)
			.append(isAfricanAmerican)
			.append(feature)
		.build();
	}
}