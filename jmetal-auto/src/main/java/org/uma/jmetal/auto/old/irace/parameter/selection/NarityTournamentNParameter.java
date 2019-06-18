package org.uma.jmetal.auto.old.irace.parameter.selection;

import org.uma.jmetal.auto.old.irace.parametertype.impl.IntegerParameterType;

public class NarityTournamentNParameter extends IntegerParameterType {
  public NarityTournamentNParameter(int lowerBound, int upperBound) {
    super("selectionTournamentSize", lowerBound, upperBound) ;

    setParentTag(SelectionType.tournament.toString());
  }
}
