package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.util.List;
import org.uma.jmetal.auto.parameter.Parameter;

public interface AutoConfigurableAlgorithm {
  void parse(String[] args) ;
  void configure() ;
  List<Parameter<?>> configurableParameterList() ;
}
