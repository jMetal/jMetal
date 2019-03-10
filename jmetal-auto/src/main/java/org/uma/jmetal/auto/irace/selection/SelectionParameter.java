package org.uma.jmetal.auto.irace.selection;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;

import java.util.Arrays;

public class SelectionParameter extends Parameter {

  public SelectionParameter() {
    super("selection",
        "--selection",
        ParameterType.c,
        "(N-aryTournament)",
        "",
        Arrays.asList(new NarityTournamentSelectionParameter())
    ) ;
  }
}
