package org.uma.jmetal.auto.irace.parametertype.parameter.crossover;

import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class BLXAlphaCrossoverAlphaValueParameter extends RealParameterType {
  public BLXAlphaCrossoverAlphaValueParameter() {
    super("blxAlphaCrossoverAlphaValue", 0.0, 1.0) ;
    setParentTag(CrossoverType.BLX_ALPHA.name());
  }
}
