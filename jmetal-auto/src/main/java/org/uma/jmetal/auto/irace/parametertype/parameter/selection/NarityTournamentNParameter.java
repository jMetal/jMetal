package org.uma.jmetal.auto.irace.parametertype.parameter.selection;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.IntegerParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

public class NarityTournamentNParameter extends IntegerParameterType {
  public NarityTournamentNParameter(int lowerBound, int upperBound) {
    super("selectionTournamentSize", lowerBound, upperBound) ;

    setParentTag(SelectionType.tournament.toString());
  }
}
