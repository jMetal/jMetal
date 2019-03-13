package org.uma.jmetal.auto.irace.old.selection;

import org.uma.jmetal.auto.irace.old.Parameter;
import org.uma.jmetal.auto.irace.old.ParameterTypes;

import java.util.Arrays;

public class SelectionParameter extends Parameter {
  public SelectionParameter() {
    super("selection",
        "--selection",
        ParameterTypes.c,
        "(Tournament)",
        "",
        Arrays.asList(new NarityTournamentSelectionParameter())
    ) ;
  }
}
