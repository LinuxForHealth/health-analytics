package com.ibm.healthpatterns.ascvd.interop;

import java.util.EnumSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AscvdOutput {

	AscvdInput input;
	Double individualSum;
	Double tenYearRisk;
	
	public boolean isOutputValid() {
		return input.isInputValid();
	}
	
	public AscvdInput getInput() {
		return input;
	}

	public void setInput(AscvdInput input) {
		this.input = input;
	}

	public EnumSet<AscvdInputValidationFailureReason> getValidationResults() {
		return input.getValidationResults();
	}

	public Double getIndividualSum() {
		return individualSum;
	}
	
	public void setIndividualSum(Double individualSum) {
		this.individualSum = individualSum;
	}
	
	public Double getTenYearRisk() {
		return tenYearRisk;
	}
	
	public void setTenYearRisk(Double tenYearRisk) {
		this.tenYearRisk = tenYearRisk;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(input)
			.append(individualSum)
			.append(tenYearRisk)
		.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AscvdOutput == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		AscvdOutput other = (AscvdOutput) obj;
		return new EqualsBuilder()
			.append(input, other.input)
			.append(individualSum, other.individualSum)
			.append(tenYearRisk, other.tenYearRisk)
		.build();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append(input)
			.append(individualSum)
			.append(tenYearRisk)
		.build();
	}
}