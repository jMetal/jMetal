package org.uma.jmetal.auto.parameterv2.param.irace;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.OrdinalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.auto.parameterv2.param.catalogue.AlgorithmResult;
import org.uma.jmetal.auto.parameterv2.param.catalogue.OffspringPopulationSize;
import org.uma.jmetal.auto.parameterv2.param.catalogue.PopulationSize;
import org.uma.jmetal.auto.parameterv2.param.catalogue.PopulationSizeWithArchive;

import java.util.Map;

public class NSGAIIParameterFile {
  public void create(Map<String, Parameter<?>> parameterMap) {
    parameterMap.forEach((key, value) -> System.out.println("Name: " + key + ". Value: " + value));

    AlgorithmResult algorithmResult = (AlgorithmResult) parameterMap.get("algorithmResult");
    PopulationSize populationSize = (PopulationSize) parameterMap.get("populationSize");
    PopulationSizeWithArchive populationSizeWithArchive =
        (PopulationSizeWithArchive) parameterMap.get("populationSizeWithArchive");
    OffspringPopulationSize offspringPopulationSize =
        (OffspringPopulationSize) parameterMap.get("offspringPopulationSize");


    String formatString = "%-40s %-40s %-7s %-30s %-20s\n";
    StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(
          String.format(
              formatString,
              algorithmResult.getName(),
              "--" + algorithmResult.getName(),
              decodeType(algorithmResult),
              algorithmResult.getValidValues(),
              ""));

     stringBuilder.append(
         String.format(
             formatString,
             populationSize.getName(),
             "--" + populationSize.getName(),
             decodeType(populationSize),
             populationSize.getValue(),
             "")) ;

    stringBuilder.append(
        String.format(
            formatString,
            populationSizeWithArchive.getName(),
            "--" + populationSizeWithArchive.getName(),
            decodeType(populationSizeWithArchive),
            populationSizeWithArchive.getValidValues(),
            "")) ;

    System.out.println(stringBuilder.toString()) ;
  }

  private String decodeType(Parameter<?> parameter) {
    String result =" ";
    if (parameter instanceof CategoricalParameter) {
      result = "c";
    } else if (parameter instanceof OrdinalParameter) {
      result = "o";
    } else if (parameter instanceof Parameter) {
      result = "o" ;
    }

    return result ;
  }

}
