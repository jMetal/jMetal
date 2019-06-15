package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithOppositeBoundValue;
import org.uma.jmetal.solution.util.impl.RepairDoubleSolutionWithRandomValue;
import weka.Run;

import java.util.List;
import java.util.function.Function;

public class RepairDoubleSolutionStrategyParameter extends CategoricalParameter<String> {
  public RepairDoubleSolutionStrategyParameter(String name, String args[], List<String> strategies) {
    super(name, args, strategies) ;
  }

  @Override
  public CategoricalParameter<String> parse() {
    setValue(on("--"+getName(), getArgs(), Function.identity()));
    return this ;
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
