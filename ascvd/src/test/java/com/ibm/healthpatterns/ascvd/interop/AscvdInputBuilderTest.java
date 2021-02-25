package com.ibm.healthpatterns.ascvd.interop;

import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Test;

public class AscvdInputBuilderTest {

	private static final int AGE = 50;
	private static final boolean IS_MALE = true;
	private static final boolean IS_AFRICAN_AMERICAN = true;
	private static final boolean IS_BP_TREATED = true;
	private static final boolean IS_CURRENT_SMOKER = true;
	private static final boolean IS_DIABETIC = true;
	private static final double TOTAL_CHOLESTEROL = 170;
	private static final double HDL_CHOLESTEROL = 50;
	private static final double SYSTOLIC_BP = 110;
	
	@Test
	public void testValidInput() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		
		Assert.assertTrue(input.isInputValid());
		Assert.assertTrue(input.getValidationResults().isEmpty());
	}
	
	@Test
	public void testMissingAge() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_AGE));
	}
	
	@Test
	public void testMissingIsMale() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_IS_MALE));
	}
	
	@Test
	public void testMissingIsAfricanAmerican() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_IS_AFRICAN_AMERICAN));
	}
	
	@Test
	public void testMissingIsBpTreated() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_IS_BP_TREATED));
	}
	
	@Test
	public void testMissingCurrentSmoker() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_CURRENT_SMOKER));
	}
	
	@Test
	public void testMissingIsDiabetic() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_IS_DIABETIC));
	}
	
	@Test
	public void testMissingTotalCholesterol() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_TOTAL_CHOLESTEROL));
	}
	
	@Test
	public void testMissingHdlCholesterol() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_HDL_CHOLESTEROL));
	}
	
	@Test
	public void testMissingSystolicBp() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.MISSING_SYSTOLIC_BP));
	}
	
	@Test
	public void testInvalidAgeLow() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(19)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_AGE));
	}
	
	@Test
	public void testInvalidAgeHigh() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(80)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_AGE));
	}
	
	@Test
	public void testInvalidTotalCholesterolLow() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(129.0)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_TOTAL_CHOLESTEROL));
	}
	
	@Test
	public void testInvalidTotalCholesterolHigh() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(321.0)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_TOTAL_CHOLESTEROL));
	}
	
	@Test
	public void testInvalidHdlCholesterolLow() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(19.0)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_HDL_CHOLESTEROL));
	}
	
	@Test
	public void testInvalidHdlCholesterolHigh() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(101.0)
			.systolicBp(SYSTOLIC_BP)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_HDL_CHOLESTEROL));
	}
	
	@Test
	public void testInvalidSystolicBpLow() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(89.0)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_SYSTOLIC_BP));
	}
	
	@Test
	public void testInvalidSystolicBpHigh() {
		
		AscvdInput input = AscvdInputBuilder.newInstance()
			.age(AGE)
			.isMale(IS_MALE)
			.isAfricanAmerican(IS_AFRICAN_AMERICAN)
			.isBpTreated(IS_BP_TREATED)
			.isCurrentSmoker(IS_CURRENT_SMOKER)
			.isDiabetic(IS_DIABETIC)
			.totalCholesterol(TOTAL_CHOLESTEROL)
			.hdlCholesterol(HDL_CHOLESTEROL)
			.systolicBp(201.0)
			.build();
		EnumSet<AscvdInputValidationFailureReason> validationResults = input.getValidationResults();
		
		Assert.assertFalse(input.isInputValid());
		Assert.assertEquals(1, validationResults.size());
		Assert.assertTrue(validationResults.contains(AscvdInputValidationFailureReason.INVALID_SYSTOLIC_BP));
	}
}
