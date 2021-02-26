package com.ibm.health.analytics.ascvd;
import org.junit.Assert;
import org.junit.Test;
import com.ibm.health.analytics.ascvd.interop.AscvdInput;
import com.ibm.health.analytics.ascvd.interop.AscvdInputBuilder;
import com.ibm.health.analytics.ascvd.interop.AscvdOutput;

public class AscvdModelTest {

	@Test
	public void testWhiteFemaleFromPaper() {
		AscvdInput input = AscvdInputBuilder.newInstance()
			.isMale(false)
			.isAfricanAmerican(false)
			.age(55)
			.totalCholesterol(213.0)
			.hdlCholesterol(50.0)
			.systolicBp(120.0)
			.isBpTreated(false)
			.isCurrentSmoker(false)
			.isDiabetic(false)
			.build();

		AscvdOutput output = AscvdModel.INSTANCE.calculate(input);
		Assert.assertEquals(-29.67, output.getIndividualSum(), 0.01);
		Assert.assertEquals(0.021, output.getTenYearRisk(), 0.001);
	}
	
	@Test
	public void testAfricanAmericanFemaleFromPaper() {
		AscvdInput input = AscvdInputBuilder.newInstance()
			.isMale(false)
			.isAfricanAmerican(true)
			.age(55)
			.totalCholesterol(213.0)
			.hdlCholesterol(50.0)
			.systolicBp(120.0)
			.isBpTreated(false)
			.isCurrentSmoker(false)
			.isDiabetic(false)
			.build();
		
		AscvdOutput output = AscvdModel.INSTANCE.calculate(input);
		Assert.assertEquals(86.16, output.getIndividualSum(), 0.01);
		Assert.assertEquals(0.030, output.getTenYearRisk(), 0.001);
	}
	
	@Test
	public void testWhiteMaleFromPaper() {
		AscvdInput input = AscvdInputBuilder.newInstance()
			.isMale(true)
			.isAfricanAmerican(false)
			.age(55)
			.totalCholesterol(213.0)
			.hdlCholesterol(50.0)
			.systolicBp(120.0)
			.isBpTreated(false)
			.isCurrentSmoker(false)
			.isDiabetic(false)
			.build();
		
		AscvdOutput output = AscvdModel.INSTANCE.calculate(input);
		Assert.assertEquals(60.69, output.getIndividualSum(), 0.01);
		Assert.assertEquals(0.053, output.getTenYearRisk(), 0.001);
	}
	
	@Test
	public void testAfricanAmericanMaleFromPaper() {
		AscvdInput input = AscvdInputBuilder.newInstance()
			.isMale(true)
			.isAfricanAmerican(true)
			.age(55)
			.totalCholesterol(213.0)
			.hdlCholesterol(50.0)
			.systolicBp(120.0)
			.isBpTreated(false)
			.isCurrentSmoker(false)
			.isDiabetic(false)
			.build();
		
		AscvdOutput output = AscvdModel.INSTANCE.calculate(input);
		Assert.assertEquals(18.97, output.getIndividualSum(), 0.01);
		Assert.assertEquals(0.061, output.getTenYearRisk(), 0.001);
	}
}
