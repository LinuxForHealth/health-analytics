package com.ibm.healthpatterns.ascvd.interop;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.healthpatterns.ascvd.AscvdModel;

@Path("/ascvd")
public class AscvdDriverCli {

  public static final String AGE = "age";
  public static final String MALE = "male";
  public static final String AFRICAN_AMERICAN = "africanAmerican";
  public static final String BP_TREATED = "bpTreated";
  public static final String CURRENT_SMOKER = "currentSmoker";
  public static final String DIABETIC = "diabetic";
  public static final String TOTAL_CHOLESTEROL = "totalCholesterol";
  public static final String HDL_CHOLESTEROL = "hdlCholesterol";
  public static final String SYSTOLIC_BP = "systolicBp";
  public static final String OUTPUT_PATH = "outputPath";

  @SuppressWarnings("static-access")
  private static Options getOptions() {
    Options options = new Options();
    options.addOption(
        Option.builder("a")
        .desc("The age of the patient")
        .hasArg()
        .argName(AGE)
        .longOpt(AGE)
        .required()
        .type(Integer.class)
        .build()
        );
    options.addOption(
        Option.builder("m")
        .desc("Is the patient male?")
        .hasArg()
        .argName(MALE)
        .longOpt(MALE)
        .required()
        .type(Boolean.class)
        .build()
        );
    options.addOption(
        Option.builder("aa")
        .desc("Is the patient african american?")
        .hasArg()
        .argName(AFRICAN_AMERICAN)
        .longOpt(AFRICAN_AMERICAN)
        .required()
        .type(Boolean.class)
        .build()
        );
    options.addOption(
        Option.builder("bpt")
        .desc("Is the patient being treated for high blood pressure?")
        .hasArg()
        .argName(BP_TREATED)
        .longOpt(BP_TREATED)
        .required()
        .type(Boolean.class)
        .build()
        );
    options.addOption(
        Option.builder("cs")
        .desc("Is the patient a current smoker?")
        .hasArg()
        .argName(CURRENT_SMOKER)
        .longOpt(CURRENT_SMOKER)
        .required()
        .type(Boolean.class)
        .build()
        );
    options.addOption(
        Option.builder("d")
        .desc("Is the patient a diabetic?")
        .hasArg()
        .argName(DIABETIC)
        .longOpt(DIABETIC)
        .required()
        .type(Boolean.class)
        .build()
        );		
    options.addOption(
        Option.builder("tc")
        .desc("What is the patient's total cholesterol?")
        .hasArg()
        .argName(TOTAL_CHOLESTEROL)
        .longOpt(TOTAL_CHOLESTEROL)
        .required()
        .type(Double.class)
        .build()
        );
    options.addOption(
        Option.builder("hdl")
        .desc("What is the patient's HDL cholesterol?")
        .hasArg()
        .argName(HDL_CHOLESTEROL)
        .longOpt(HDL_CHOLESTEROL)
        .required()
        .type(Double.class)
        .build()
        );	
    options.addOption(
        Option.builder("sbp")
        .desc("What is the patient's systolic blood pressure?")
        .hasArg()
        .argName(SYSTOLIC_BP)
        .longOpt(SYSTOLIC_BP)
        .required()
        .type(Double.class)
        .build()
        );
    options.addOption(
        Option.builder("o")
        .desc("The output path to store the results of this analytic")
        .hasArg()
        .argName(OUTPUT_PATH)
        .longOpt(OUTPUT_PATH)
        .type(String.class)
        .build()
        );
    return options;
  }
	
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public static final AscvdOutput calculate(
      @QueryParam(MALE) boolean isMale,
      @QueryParam(AFRICAN_AMERICAN) boolean isAfricanAmerican, 
      @QueryParam(AGE) int age,
      @QueryParam(TOTAL_CHOLESTEROL) double totalCholesterol,
      @QueryParam(HDL_CHOLESTEROL) double hdlCholesterol,
      @QueryParam(SYSTOLIC_BP) double systolicBp,
      @QueryParam(BP_TREATED) boolean isBpTreated,
      @QueryParam(CURRENT_SMOKER) boolean isCurrentSmoker,
      @QueryParam(DIABETIC) boolean isDiabetic) {
    AscvdInput input = AscvdInputBuilder.newInstance()
        .isMale(isMale)
        .isAfricanAmerican(isAfricanAmerican)
        .age(age)
        .totalCholesterol(totalCholesterol)
        .hdlCholesterol(hdlCholesterol)
        .systolicBp(systolicBp)
        .isBpTreated(isBpTreated)
        .isCurrentSmoker(isCurrentSmoker)
        .isDiabetic(isDiabetic)
        .build();

    AscvdOutput output = AscvdModel.INSTANCE.calculate(input);
    return output;
  }

  public static void main(String[] args) throws ParseException, JsonProcessingException, IOException {
    Options options = getOptions();
		
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;

    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("AscvdDriverCli", options);
      throw e;
    }

    AscvdOutput output = calculate(
        Boolean.parseBoolean(cmd.getOptionValue(MALE)),
        Boolean.parseBoolean(cmd.getOptionValue(AFRICAN_AMERICAN)),
        Integer.parseInt(cmd.getOptionValue(AGE)),
        Double.parseDouble(cmd.getOptionValue(TOTAL_CHOLESTEROL)),
        Double.parseDouble(cmd.getOptionValue(HDL_CHOLESTEROL)),
        Double.parseDouble(cmd.getOptionValue(SYSTOLIC_BP)),
        Boolean.parseBoolean(cmd.getOptionValue(BP_TREATED)),
        Boolean.parseBoolean(cmd.getOptionValue(CURRENT_SMOKER)),
        Boolean.parseBoolean(cmd.getOptionValue(DIABETIC))
        );  

    ObjectMapper mapper = new ObjectMapper();
    // Converting the Object to JSONString
    String jsonOutput = mapper.writeValueAsString(output);

    if (cmd.hasOption(OUTPUT_PATH)) {
      String outputPath = cmd.getOptionValue(OUTPUT_PATH);
      if (outputPath != null) {
        File outputDir = new File(outputPath);
        if (outputDir.exists() && outputDir.isDirectory()) {
          try (Writer writer = new BufferedWriter(
              new FileWriter(new File(outputDir, "native.ascvd.json")))) {
            writer.write(jsonOutput);
          }
        }
      }
    } else {
      System.out.println(jsonOutput);
    }
  }
}