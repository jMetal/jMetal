package org.uma.jmetal.auto.irace.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateIraceParameterFile {
  public static void main(String[] args) {
    /*
    List<Parameter> parameterList = new ArrayList<>();
    parameterList.add(new CrossoverParameter());
    parameterList.add(new MutationParameter());
    parameterList.add(new SelectionParameter());
    parameterList.add(
        new Parameter(
            "offspringPopulationSize",
            "--offspringPopulationSize",
            ParameterTypes.i,
            "(1, 400)",
            "",
            Collections.emptyList()));

    String formatString = "%40s %40s %7s %30s %20s\n" ;
    for (Parameter parameter : parameterList) {
      System.out.format(
          formatString,
          parameter.getName(),
          parameter.getSwitch(),
          parameter.getType(),
          parameter.getValidValues(),
          parameter.getConditionalParameters());

      for (Parameter relatedParameter : parameter.getAssociatedParameters()) {
        System.out.format(
            formatString,
            relatedParameter.getName(),
            relatedParameter.getSwitch(),
            relatedParameter.getType(),
            relatedParameter.getValidValues(),
            relatedParameter.getConditionalParameters());
      }
    }
    */
  }
}
