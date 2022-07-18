package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithOppositeBoundValue;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithRandomValue;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class RepairDoubleSolutionStrategyParameter extends CategoricalParameter {
  public RepairDoubleSolutionStrategyParameter(String name, String args[], List<String> strategies) {
    super(name, args, strategies) ;
  }

  public @NotNull RepairDoubleSolution getParameter() {
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
        throw new JMetalException("Repair strategy unknown: " + getName()) ;
    }

    return result ;
  }
}
