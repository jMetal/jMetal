package org.uma.jmetal.auto.irace.selection;

import org.uma.jmetal.auto.irace.Parameter;
import org.uma.jmetal.auto.irace.ParameterType;
import picocli.CommandLine;

import java.util.Arrays;

public class SelectionParameter extends Parameter {
  public SelectionParameter() {
    super("selection",
        "--selection",
        ParameterType.c,
        "(Tournament)",
        "",
        Arrays.asList(new NarityTournamentSelectionParameter())
    ) ;
  }
}
