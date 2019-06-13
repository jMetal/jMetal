package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolution;

import java.util.List;
import java.util.function.Function;

public class CrossoverParameter extends CategoricalParameter<String> {
  private String[] args;

  public CrossoverParameter(String args[], List<String> crossoverOperators) {
    super(crossoverOperators);
    this.args = args;
  }

  public CategoricalParameter<String> parse() {
    value = on("--crossover", args, Function.identity());

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    getSpecificParameters()
        .forEach(
            (key, parameter) -> {
              if (key.equals(this.value)) {
                parameter.parse().check();
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
