package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;
import java.util.function.Function;

public class MutationParameter extends CategoricalParameter<String> {
  public MutationParameter(String args[], List<String> mutationOperators) {
    super("mutation", args, mutationOperators);
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--mutation", getArgs(), Function.identity()));

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
