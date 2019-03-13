package org.uma.jmetal.auto.irace.old.selection;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Collections;

public class NarityTournamentSelectionParameter extends Parameter {
  public NarityTournamentSelectionParameter() {
    super("selection-tournament-arity",
        "--selection-tournament-arity",
        ParameterTypes.i,
        "(2, 10)",
        "| selection %in% c(\"Tournament\")",
        Collections.emptyList()
        ) ;
  }
}
