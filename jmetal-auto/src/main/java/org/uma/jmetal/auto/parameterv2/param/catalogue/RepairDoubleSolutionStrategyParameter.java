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
  private String name ;
  private String[] args ;

  public RepairDoubleSolutionStrategyParameter(String args[], String name, List<String> strategies) {
    super(strategies) ;
    this.name = name ;
    this.args = args ;
  }

  @Override
  public CategoricalParameter<String> parse() {
    value = on("--"+name, args, Function.identity());
    return this ;
  }

  public RepairDoubleSolution getParameter() {
    RepairDoubleSolution result ;
    switch (getName()) {
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

  @Override
  public String getName() {
    return name;
  }
}
