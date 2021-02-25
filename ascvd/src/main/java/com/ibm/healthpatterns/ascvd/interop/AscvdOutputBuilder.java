package com.ibm.healthpatterns.ascvd.interop;

public class AscvdOutputBuilder {

	AscvdInput input;
	Double individualSum;
	Double tenYearRisk;
	
	public AscvdOutputBuilder() {}
	
	public static AscvdOutputBuilder newInstance() {
		return new AscvdOutputBuilder();
	}
	
	public AscvdOutputBuilder input(AscvdInput input) {
		this.input = input;
		return this;
	}
	
	public AscvdOutputBuilder individualSum(Double individualSum) {
		this.individualSum = individualSum;
		return this;
	}
	
	public AscvdOutputBuilder tenYearRisk(Double tenYearRisk) {
		this.tenYearRisk = tenYearRisk;
		return this;
	}
	
	public AscvdOutput build() {
		AscvdOutput output = new AscvdOutput();
		output.setInput(input);
		output.setIndividualSum(individualSum);
		output.setTenYearRisk(tenYearRisk);
		return output;
	}
}
