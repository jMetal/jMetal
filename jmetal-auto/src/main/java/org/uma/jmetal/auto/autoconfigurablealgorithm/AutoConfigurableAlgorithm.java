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

  /**
   * Given a list of parameters, returns a list with of all of them and all of their sub-parameters
   *
   * @param parameters
   * @return A list of parameters
   */
  static List<Parameter<?>> parameterFlattening(List<Parameter<?>> parameters) {
    List<Parameter<?>> parameterList = new ArrayList<>() ;
    parameters.forEach(parameter -> {
      parameterList.add(parameter);
      parameterList.addAll(parameterFlattening(parameter.globalParameters()));
      List<Parameter<?>> specificParameters = parameter.specificParameters().stream().map(
          Pair::getRight).collect(
          Collectors.toList());
      parameterList.addAll(parameterFlattening(specificParameters));
    });
    return parameterList ;
  }
}
