package org.uma.jmetal.auto.irace.parametertype.parameter.crossover;

import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class BLXCrossoverParameter extends RealParameterType {
  public BLXCrossoverParameter() {
    super("blxAlphaCrossoverAlphaValue", 0.0, 1.0) ;
    setParentTag("BLX_ALPHA");
  }
}
