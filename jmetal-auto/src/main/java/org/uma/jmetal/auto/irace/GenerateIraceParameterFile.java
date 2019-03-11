package org.uma.jmetal.auto.irace;

import org.uma.jmetal.auto.irace.crossover.CrossoverParameter;
import org.uma.jmetal.auto.irace.mutation.MutationParameter;
import org.uma.jmetal.auto.irace.selection.SelectionParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateIraceParameterFile {
  public static void main(String[] args) {
    List<Parameter> parameterList = new ArrayList<>() ;
    parameterList.add(new CrossoverParameter()) ;
    parameterList.add(new MutationParameter()) ;
    parameterList.add(new SelectionParameter()) ;
    parameterList.add(new Parameter(
        "offspringPopulationSize",
        "--offspringPopulationSize",
        ParameterType.i,
        "(1, 400)",
        "",
        Collections.emptyList())) ;

    for (Parameter parameter : parameterList) {
      System.out.println(parameter) ;

      for (Parameter relatedParameter : parameter.getAssociatedParameters()) {
        System.out.println(relatedParameter) ;
      }
    }
  }
}