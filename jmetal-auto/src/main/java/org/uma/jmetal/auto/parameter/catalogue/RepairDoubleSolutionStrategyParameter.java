package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithOppositeBoundValue;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithRandomValue;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class RepairDoubleSolutionStrategyParameter extends CategoricalParameter {
  public RepairDoubleSolutionStrategyParameter(String name, List<String> strategies) {
    super(name, strategies) ;
  }

  public RepairDoubleSolution getParameter() {
    RepairDoubleSolution result ;
    switch (value()) {
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
        throw new JMetalException("Repair strategy unknown: " + name()) ;
    }

    return result ;
  }
}
