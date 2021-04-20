package com.ibm.health.analytics.ascvd;

import static io.restassured.RestAssured.given;
import org.hamcrest.MatcherAssert;
import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.Test;
import com.ibm.health.analytics.ascvd.interop.AscvdDriverCli;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AscvdQuarkusTest {
  @Test
  public void testHelloEndpoint() {
    MatcherAssert.assertThat(
        Double.parseDouble(
            given().when().get(
            		"?" + AscvdDriverCli.MALE + "=false"
            		+ "&" + AscvdDriverCli.AFRICAN_AMERICAN + "=false"
            		+ "&" + AscvdDriverCli.AGE + "=55"
            		+ "&" + AscvdDriverCli.TOTAL_CHOLESTEROL + "=213.0"
            		+ "&" + AscvdDriverCli.HDL_CHOLESTEROL + "=50.0"
            		+ "&" + AscvdDriverCli.SYSTOLIC_BP + "=120.0"
            		+ "&" + AscvdDriverCli.BP_TREATED + "=false"
            		+ "&" + AscvdDriverCli.CURRENT_SMOKER + "=false"
            		+ "&" + AscvdDriverCli.DIABETIC + "=false")
            .then().extract().jsonPath().getString("tenYearRisk")),
        IsCloseTo.closeTo(0.02, 0.021) // Expected: 0.02052229820249496
    );
  }
}