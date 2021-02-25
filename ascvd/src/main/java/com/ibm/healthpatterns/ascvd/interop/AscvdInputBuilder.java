package com.ibm.healthpatterns.ascvd.interop;

import java.util.EnumSet;

import com.google.common.collect.Range;

public class AscvdInputBuilder {
 
	private Integer age;
	
	private Boolean isMale;
	private Boolean isAfricanAmerican;
	private Boolean isBpTreated;
	private Boolean isCurrentSmoker;
	private Boolean isDiabetic;
	private Boolean hadPriorAscvdEvent;
	
	private Double totalCholesterol;
	private Double hdlCholesterol;
	private Double systolicBp;
	
	private static final Range<Integer> ALLOWABLE_AGE_RANGE = Range.closedOpen(40, 80);
	private static final Range<Double> ALLOWABLE_TOTAL_CHOLESTEROL_RANGE = Range.closed(130.0, 320.0);
	private static final Range<Double> ALLOWABLE_HDL_CHOLESTEROL_RANGE = Range.closed(20.0, 100.0);
	private static final Range<Double> ALLOWABLE_SYSTOLIC_BP_RANGE = Range.closed(90.0, 200.0);
	
	public AscvdInputBuilder() {}
	
	public static AscvdInputBuilder newInstance() {
		return new AscvdInputBuilder();
	}
	
	public AscvdInputBuilder age(Integer age) {
		this.age = age;
		return this;
	}
	
	public AscvdInputBuilder isMale(Boolean isMale) {
		this.isMale = isMale;
		return this;
	}
	
	public AscvdInputBuilder isAfricanAmerican(Boolean isAfricanAmerican) {
		this.isAfricanAmerican = isAfricanAmerican;
		return this;
	}
	
	public AscvdInputBuilder isBpTreated(boolean isBpTreated) {
		this.isBpTreated = isBpTreated;
		return this;
	}
	
	public AscvdInputBuilder isCurrentSmoker(boolean isCurrentSmoker) {
		this.isCurrentSmoker = isCurrentSmoker;
		return this;
	}
	
	public AscvdInputBuilder isDiabetic(boolean isDiabetic) {
		this.isDiabetic = isDiabetic;
		return this;
	}
	
	public AscvdInputBuilder hadPriorAscvdEvent(boolean hadPriorAscvdEvent) {
		this.hadPriorAscvdEvent = hadPriorAscvdEvent;
		return this;
	}
	
	public AscvdInputBuilder totalCholesterol(Double totalCholesterol) {
		this.totalCholesterol = totalCholesterol;
		return this;
	}
	
	public AscvdInputBuilder hdlCholesterol(Double hdlCholesterol) {
		this.hdlCholesterol = hdlCholesterol;
		return this;
	}
	
	public AscvdInputBuilder systolicBp(Double systolicBp) {
		this.systolicBp = systolicBp;
		return this;
	}

	public AscvdInput build() {
		
		EnumSet<AscvdInputValidationFailureReason> validationResults = EnumSet.noneOf(AscvdInputValidationFailureReason.class);
		
		AscvdInput input = new AscvdInput();
		if (age == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_AGE);
		} else if (!ALLOWABLE_AGE_RANGE.contains(age)) {
			validationResults.add(AscvdInputValidationFailureReason.INVALID_AGE);
		} else {
			input.setAge(age);
		}

		if (isMale == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_IS_MALE);
		} else {
			input.setMale(isMale);
		}
		
		if (isAfricanAmerican == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_IS_AFRICAN_AMERICAN);
		} else {
			input.setAfricanAmerican(isAfricanAmerican);
		}
		
		if (isBpTreated == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_IS_BP_TREATED);
		} else {
			input.setBpTreated(isBpTreated);
		}
		
		if (isCurrentSmoker == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_CURRENT_SMOKER);
		} else {
			input.setCurrentSmoker(isCurrentSmoker);
		}
		
		if (isDiabetic == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_IS_DIABETIC);
		} else {
			input.setDiabetic(isDiabetic);
		}
		
		if (hadPriorAscvdEvent != null && hadPriorAscvdEvent) {
			validationResults.add(AscvdInputValidationFailureReason.HAD_PRIOR_ASCVD_EVENT);
		}
		
		if (totalCholesterol == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_TOTAL_CHOLESTEROL);
		} else if (!ALLOWABLE_TOTAL_CHOLESTEROL_RANGE.contains(totalCholesterol)){
			validationResults.add(AscvdInputValidationFailureReason.INVALID_TOTAL_CHOLESTEROL);
		} else {
			input.setTotalCholesterol(totalCholesterol);
		}
		
		if (hdlCholesterol == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_HDL_CHOLESTEROL);
		} else if (!ALLOWABLE_HDL_CHOLESTEROL_RANGE.contains(hdlCholesterol)) {
			validationResults.add(AscvdInputValidationFailureReason.INVALID_HDL_CHOLESTEROL);
		} else {
			input.setHdlCholesterol(hdlCholesterol);
		}
		
		if (systolicBp == null) {
			validationResults.add(AscvdInputValidationFailureReason.MISSING_SYSTOLIC_BP);
		} else if (!ALLOWABLE_SYSTOLIC_BP_RANGE.contains(systolicBp)) {
			validationResults.add(AscvdInputValidationFailureReason.INVALID_SYSTOLIC_BP);
		} else {
			input.setSystolicBp(systolicBp);
		}
		
		input.setValidationResults(validationResults);
		return input;
	}
}
