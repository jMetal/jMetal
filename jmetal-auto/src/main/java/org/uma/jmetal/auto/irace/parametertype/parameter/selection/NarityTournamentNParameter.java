package org.uma.jmetal.auto.irace.parametertype.parameter.selection;

import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class NarityTournamentNParameter extends RealParameterType {
  public NarityTournamentNParameter(double lowerBound, double upperBound) {
    super("selectionTournamentSize", lowerBound, upperBound) ;

    setParentTag("tournament");
  }
}
