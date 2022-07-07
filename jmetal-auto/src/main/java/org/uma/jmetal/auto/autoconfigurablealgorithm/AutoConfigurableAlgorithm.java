package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.util.List;
import org.uma.jmetal.auto.parameter.Parameter;

public interface AutoConfigurableAlgorithm {
  void parseAndCheckParameters(String[] args) ;
  List<Parameter<?>> getAutoConfigurableParameterList() ;
}
