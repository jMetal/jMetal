package org.uma.jmetal.auto.irace.parameter.selection;

import org.uma.jmetal.auto.irace.parametertype.impl.IntegerParameterType;

public class NarityTournamentNParameter extends IntegerParameterType {
  public NarityTournamentNParameter(int lowerBound, int upperBound) {
    super("selectionTournamentSize", lowerBound, upperBound) ;

    setParentTag(SelectionType.tournament.toString());
  }
}
