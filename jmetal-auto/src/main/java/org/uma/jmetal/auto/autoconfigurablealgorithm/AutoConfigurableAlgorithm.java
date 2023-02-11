package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.auto.parameter.Parameter;

public interface AutoConfigurableAlgorithm {
  void parse(String[] args) ;
  List<Parameter<?>> configurableParameterList() ;
  List<Parameter<?>> fixedParameterList() ;

  static List<String> parameterNames(List<Parameter<?>> parameters) {
    List<String> parameterList = new ArrayList<>();
    for (Parameter<?> parameter : parameters) {
      parameterList.add(parameter.name());
      parameterList.addAll(parameterNames(parameter.globalParameters()));
      List<Parameter<?>> specificParameters = parameter.specificParameters().stream().map(
          Pair::getRight).collect(
          Collectors.toList());

      parameterList.addAll(parameterNames(specificParameters));
    }

    return parameterList;
  }
}
