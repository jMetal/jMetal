package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class CrossoverParameter extends CategoricalParameter {
  public CrossoverParameter(String[] args, List<String> crossoverOperators) {
    super("crossover", args, crossoverOperators);
  }

  public CrossoverOperator<DoubleSolution> getParameter() {
    Double crossoverProbability = (Double) findGlobalParameter("crossoverProbability").getValue();
    RepairDoubleSolutionStrategyParameter repairDoubleSolution =
        (RepairDoubleSolutionStrategyParameter) findGlobalParameter("crossoverRepairStrategy");

    CrossoverOperator<DoubleSolution> result;
    switch (getValue()) {
      case "SBX":
        Double distributionIndex =
            (Double) findSpecificParameter("sbxDistributionIndex").getValue();
        result =
            new SBXCrossover(
                crossoverProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "BLX_ALPHA":
        Double alpha = (Double) findSpecificParameter("blxAlphaCrossoverAlphaValue").getValue();
        result =
            new BLXAlphaCrossover(crossoverProbability, alpha, repairDoubleSolution.getParameter());
        break;
      default:
        throw new JMetalException("Crossover operator does not exist: " + getName());
    }
    return result;
  }
}
