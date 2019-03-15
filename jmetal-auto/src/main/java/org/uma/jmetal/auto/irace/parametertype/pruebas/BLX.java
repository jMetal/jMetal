package org.uma.jmetal.auto.irace.parametertype.pruebas;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class BLX {
  public ParameterType getParameter() {
    RealParameterType blxAlphaCrossoverAlphaValue = new RealParameterType("blxAlphaCrossoverAlphaValue", 0.0, 1.0) ;
    blxAlphaCrossoverAlphaValue.setParentTag("BLX_ALPHA");

    return blxAlphaCrossoverAlphaValue ;
  }
}
