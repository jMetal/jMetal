package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithOppositeBoundValue;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithRandomValue;

import java.util.List;
import java.util.function.Function;

public class RepairDoubleSolutionStrategyParameter extends CategoricalParameter {
  public RepairDoubleSolutionStrategyParameter(String name, String args[], List<String> strategies) {
    super(name, args, strategies) ;
  }

  public RepairDoubleSolution getParameter() {
    RepairDoubleSolution result ;
    switch (getValue()) {
      case "random":
        result = new RepairDoubleSolutionWithRandomValue() ;
        break ;
      case "bounds":
        result = new RepairDoubleSolutionWithBoundValue() ;
        break ;
      case "round":
        result = new RepairDoubleSolutionWithOppositeBoundValue() ;
        break ;
      default:
        throw new RuntimeException("Repair strategy unknown: " + getName()) ;
    }

    return result ;
  }
}
