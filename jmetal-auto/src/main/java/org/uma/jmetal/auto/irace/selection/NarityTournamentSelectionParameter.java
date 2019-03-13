package org.uma.jmetal.auto.irace.selection;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Collections;

public class NarityTournamentSelectionParameter extends Parameter {
  public NarityTournamentSelectionParameter() {
    super("selection-tournament-arity",
        "--selection-tournament-arity",
        ParameterType.i,
        "(2, 10)",
        "| selection %in% c(\"Tournament\")",
        Collections.emptyList()
        ) ;
  }
}
