package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class CrossoverParameter extends CategoricalParameter {
  public CrossoverParameter(String[] args, List<String> crossoverOperators) {
    super("crossover", args, crossoverOperators);
  }

  public @NotNull CrossoverOperator<DoubleSolution> getParameter() {
    CrossoverOperator<DoubleSolution> result;
    var crossoverProbability = (Double) findGlobalParameter("crossoverProbability").getValue();
    var repairDoubleSolution =
        (RepairDoubleSolutionStrategyParameter) findGlobalParameter("crossoverRepairStrategy");

    switch (getValue()) {
      case "SBX":
        var distributionIndex =
            (Double) findSpecificParameter("sbxDistributionIndex").getValue();
        result =
            new SBXCrossover(
                crossoverProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "BLX_ALPHA":
        var alpha = (Double) findSpecificParameter("blxAlphaCrossoverAlphaValue").getValue();
        result =
            new BLXAlphaCrossover(crossoverProbability, alpha, repairDoubleSolution.getParameter());
        break;
      default:
        throw new RuntimeException("Crossover operator does not exist: " + getName());
    }
    return result;
  }
}
