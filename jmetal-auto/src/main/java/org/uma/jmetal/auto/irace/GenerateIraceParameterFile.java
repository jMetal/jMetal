package org.uma.jmetal.auto.irace;

import org.uma.jmetal.auto.irace.crossover.CrossoverParameter;
import org.uma.jmetal.auto.irace.mutation.MutationParameter;
import org.uma.jmetal.auto.irace.selection.SelectionParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateIraceParameterFile {
  public static void main(String[] args) {
    List<Parameter> parameterList = new ArrayList<>();
    parameterList.add(new CrossoverParameter());
    parameterList.add(new MutationParameter());
    parameterList.add(new SelectionParameter());
    parameterList.add(
        new Parameter(
            "offspringPopulationSize",
            "--offspringPopulationSize",
            ParameterType.i,
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
  }
}
/*
System.out.printf("%10s %30s %20s %5s %5s", "STUDENT ID", "EMAIL ID", "NAME", "AGE", "GRADE");
    System.out.println();
    System.out.println("-----------------------------------------------------------------------------");
    for(Student student: students){
        System.out.format("%10s %30s %20s %5d %5c",
                student.getId(), student.getEmailId(), student.getName(), student.getAge(), student.getGrade());
        System.out.println();
    }
 */
