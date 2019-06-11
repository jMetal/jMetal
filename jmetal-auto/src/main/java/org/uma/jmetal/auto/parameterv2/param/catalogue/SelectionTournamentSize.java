package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.IntegerParameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SelectionTournamentSize extends IntegerParameter {

  public SelectionTournamentSize(String args[], Integer lowerBound, Integer upperBound) {
    super(lowerBound, upperBound) ;
    value = on("--selectionTournamentSize", args, Integer::parseInt);
    check(value) ;
  }

  @Override
  public String getName() {
    return "selectionTournamentSize";
  }
}
