package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.auto.parameter.Parameter;

public interface AutoConfigurableAlgorithm {
  void parse(String[] args) ;
  void configure() ;
  List<Parameter<?>> configurableParameterList() ;
  List<Parameter<?>> fixedParameterList() ;

  public static List<String> parameterNames(List<Parameter<?>> parameters) {
    List<String> parameterList = new ArrayList<>();
    for (Parameter<?> parameter : parameters) {
      parameterList.add(parameter.name());
      parameterList.addAll(parameterNames(parameter.getGlobalParameters()));
      List<Parameter<?>> specificParameters = parameter.getSpecificParameters().stream().map(
          Pair::getRight).collect(
          Collectors.toList());

      parameterList.addAll(parameterNames(specificParameters));
    }

    return parameterList;
  }
}
