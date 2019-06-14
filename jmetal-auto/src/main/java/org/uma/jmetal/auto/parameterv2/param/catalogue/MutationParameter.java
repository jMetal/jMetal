package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;
import java.util.function.Function;

public class MutationParameter extends CategoricalParameter<String> {
  private String[] args;

  public MutationParameter(String args[], List<String> crossoverOperators) {
    super(crossoverOperators);
    this.args = args;
  }

  public CategoricalParameter<String> parse() {
    value = on("--mutation", args, Function.identity());

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

  public MutationOperator<DoubleSolution> getParameter() {
    MutationOperator<DoubleSolution> result;
    Double mutationProbability = (Double) findGlobalParameter("mutationProbability").getValue();
    RepairDoubleSolutionStrategyParameter repairDoubleSolution =
            (RepairDoubleSolutionStrategyParameter) findGlobalParameter("mutationRepairStrategy");

    switch (getValue()) {
      case "polynomial":
        Double distributionIndex =
                (Double) findSpecificParameter("polynomialMutationDistributionIndex").getValue();
        result =
                new PolynomialMutation(
                        mutationProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "uniform":
        Double perturbation = (Double) findSpecificParameter("uniformMutationPerturbation").getValue();
        result =
                new UniformMutation(mutationProbability, perturbation, repairDoubleSolution.getParameter());
        break;
      default:
        throw new RuntimeException("Mutation operator does not exist: " + getName());
    }
    return result;
  }

  @Override
  public String getName() {
    return "mutation";
  }
}
