package com.ibm.health.analytics.ascvd.interop;

import java.util.EnumSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AscvdInput {
	
	int age;
	
	boolean isMale;
	boolean isAfricanAmerican;
	boolean isBpTreated;
	boolean isCurrentSmoker;
	boolean isDiabetic;
	
	double totalCholesterol;
	double hdlCholesterol;
	double systolicBp;
	
	EnumSet<AscvdInputValidationFailureReason> validationResults;
	
	// We only want people to make these via the input builder because
	// it does the validation and populates validationResults
	protected AscvdInput() {}
	
	public boolean isInputValid() {
		return validationResults.isEmpty();
	}
	
	public EnumSet<AscvdInputValidationFailureReason> getValidationResults() {
		return validationResults;
	}
	
	public void setValidationResults(EnumSet<AscvdInputValidationFailureReason> validationResults) {
		this.validationResults = validationResults;
	}
	
	public Integer getAge() {
		return age;
	}
	
	public void setAge(Integer age) {
		this.age = age;
	}
	
	public boolean isMale() {
		return isMale;
	}
	
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}
	
	public boolean isAfricanAmerican() {
		return isAfricanAmerican;
	}
	
	public void setAfricanAmerican(boolean isAfricanAmerican) {
		this.isAfricanAmerican = isAfricanAmerican;
	}
	
	public boolean isBpTreated() {
		return isBpTreated;
	}
	
	public void setBpTreated(boolean isBpTreated) {
		this.isBpTreated = isBpTreated;
	}
	
	public boolean isCurrentSmoker() {
		return isCurrentSmoker;
	}
	
	public void setCurrentSmoker(boolean isCurrentSmoker) {
		this.isCurrentSmoker = isCurrentSmoker;
	}
	
	public boolean isDiabetic() {
		return isDiabetic;
	}
	
	public void setDiabetic(boolean isDiabetic) {
		this.isDiabetic = isDiabetic;
	}
	
	public Double getTotalCholesterol() {
		return totalCholesterol;
	}
	
	public void setTotalCholesterol(Double totalCholesterol) {
		this.totalCholesterol = totalCholesterol;
	}
	
	public Double getHdlCholesterol() {
		return hdlCholesterol;
	}
	
	public void setHdlCholesterol(Double hdlCholesterol) {
		this.hdlCholesterol = hdlCholesterol;
	}
	
	public Double getSystolicBp() {
		return systolicBp;
	}
	
	public void setSystolicBp(Double systolicBp) {
		this.systolicBp = systolicBp;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(age)
			.append(isMale)
			.append(isAfricanAmerican)
			.append(isBpTreated)
			.append(isCurrentSmoker)
			.append(isDiabetic)
			.append(totalCholesterol)
			.append(hdlCholesterol)
			.append(systolicBp)
		.build();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AscvdInput == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		AscvdInput other = (AscvdInput) obj;
		return new EqualsBuilder()
			.append(age, other.age)
			.append(isMale, other.isMale)
			.append(isAfricanAmerican, other.isAfricanAmerican)
			.append(isBpTreated, other.isBpTreated)
			.append(isCurrentSmoker, other.isCurrentSmoker)
			.append(isDiabetic, other.isDiabetic)
			.append(totalCholesterol, other.totalCholesterol)
			.append(hdlCholesterol, other.hdlCholesterol)
			.append(systolicBp, other.systolicBp)
		.build();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append(validationResults)
			.append(age)
			.append(isMale)
			.append(isAfricanAmerican)
			.append(isBpTreated)
			.append(isCurrentSmoker)
			.append(isDiabetic)
			.append(totalCholesterol)
			.append(hdlCholesterol)
			.append(systolicBp)
		.build();
	}
}
