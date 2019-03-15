package org.uma.jmetal.auto.irace.parametertype.pruebas;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class SBX {
  public ParameterType getParameter() {
    RealParameterType sbxDistributionIndex = new RealParameterType("sbxCrossoverDistributionIndex", 5.0, 400.0) ;
    sbxDistributionIndex.setParentTag("SBX");

    return sbxDistributionIndex ;
  }
}
