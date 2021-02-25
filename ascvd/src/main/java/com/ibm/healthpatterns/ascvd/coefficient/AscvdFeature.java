package com.ibm.healthpatterns.ascvd.coefficient;

public enum AscvdFeature {
	LN_AGE,
	LN_AGE_SQUARED,
	LN_TOTAL_CHOLESTEROL,
	LN_AGE_LN_TOTAL_CHOLESTEROL,
	LN_HDL,
	LN_AGE_LN_HDL,
	LN_TREATED_SYSTOLIC_BP,
	LN_AGE_LN_TREATED_SYSTOLIC_BP,
	LN_UNTREATED_SYSTOLIC_BP,
	LN_AGE_LN_UNTREATED_SYSTOLIC_BP,
	CURRENT_SMOKER,
	LN_AGE_CURRENT_SMOKER,
	DIABETES,
	
	// these aren't actually features but its worth treating them as so
	// because of how we're managing coefficients
	MEAN,
	BASELINE_SURVIVAL;
}