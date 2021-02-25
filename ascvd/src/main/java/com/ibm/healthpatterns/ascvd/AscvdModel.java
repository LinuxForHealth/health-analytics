package com.ibm.healthpatterns.ascvd;

import com.ibm.healthpatterns.ascvd.coefficient.AscvdCoefficientMgr;
import com.ibm.healthpatterns.ascvd.coefficient.AscvdFeature;
import com.ibm.healthpatterns.ascvd.interop.AscvdInput;
import com.ibm.healthpatterns.ascvd.interop.AscvdOutput;
import com.ibm.healthpatterns.ascvd.interop.AscvdOutputBuilder;

public enum AscvdModel {
	
	INSTANCE;

	AscvdCoefficientMgr coefficientMgr;
	
	private AscvdModel() {
		this.coefficientMgr = AscvdCoefficientMgr.INSTANCE;
	}
	
	public AscvdOutput calculate(AscvdInput input) {
		
		AscvdOutputBuilder builder = AscvdOutputBuilder.newInstance();
		builder.input(input);
		
		if (!input.isInputValid()) {
			return builder.build();
		}
		
		boolean isMale = input.isMale();
		boolean isAfricanAmerican = input.isAfricanAmerican();
		
		// We could store all of these on the output but I'm not sure there's value
		// leaving it out for now
		double lnAge = Math.log(input.getAge());
		double lnTotalCholesterol = Math.log(input.getTotalCholesterol());
		double lnHdlCholesterol = Math.log(input.getHdlCholesterol());
		double lnSystolicBp = Math.log(input.getSystolicBp());
		
		double individualSum = 0;
		individualSum += lnAge * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_AGE);
		individualSum += Math.pow(lnAge, 2) * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_AGE_SQUARED);
		individualSum += lnTotalCholesterol * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_TOTAL_CHOLESTEROL);
		individualSum += lnAge * lnTotalCholesterol * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_AGE_LN_TOTAL_CHOLESTEROL);
		individualSum += lnHdlCholesterol * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_HDL);
		individualSum += lnAge * lnHdlCholesterol * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_AGE_LN_HDL);
		if (input.isBpTreated()) {
			individualSum += lnSystolicBp * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_TREATED_SYSTOLIC_BP); 
			individualSum += lnAge * lnSystolicBp * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_AGE_LN_TREATED_SYSTOLIC_BP);
		} else {
			individualSum += lnSystolicBp * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_UNTREATED_SYSTOLIC_BP); 
			individualSum += lnAge * lnSystolicBp * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_AGE_LN_UNTREATED_SYSTOLIC_BP);
		}
		if (input.isCurrentSmoker()) {
			individualSum += coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.CURRENT_SMOKER);
			individualSum += lnAge * coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.LN_AGE_CURRENT_SMOKER);
		}		
		individualSum += input.isDiabetic() ? coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.DIABETES) : 0;
		
		builder.individualSum(individualSum);
		builder.tenYearRisk(1 - Math.pow(coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.BASELINE_SURVIVAL), Math.exp(individualSum - coefficientMgr.getCoefficient(isMale, isAfricanAmerican, AscvdFeature.MEAN))));
		return builder.build();
	}
}
