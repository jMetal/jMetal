package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;
import java.util.function.Function;

public class CrossoverParameter extends CategoricalParameter<String> {
  public CrossoverParameter(String args[], List<String> crossoverOperators) {
    super("crossover", args, crossoverOperators);
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--crossover", getArgs(), Function.identity()));

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    getSpecificParameters()
        .forEach(
            pair -> {
              if (pair.getKey().equals(getValue())) {
                pair.getValue().parse().check();
              }
            });

    return this;
  }

  public CrossoverOperator<DoubleSolution> getParameter() {
    CrossoverOperator<DoubleSolution> result;
    Double crossoverProbability = (Double) findGlobalParameter("crossoverProbability").getValue();
    RepairDoubleSolutionStrategyParameter repairDoubleSolution =
        (RepairDoubleSolutionStrategyParameter) findGlobalParameter("crossoverRepairStrategy");

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
        throw new RuntimeException("Crossover operator does not exist: " + getName());
    }
    return result;
  }

  @Override
  public String getName() {
    return "crossover";
  }
}
