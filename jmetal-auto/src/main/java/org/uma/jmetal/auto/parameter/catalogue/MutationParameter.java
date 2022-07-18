package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.LinkedPolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class MutationParameter extends CategoricalParameter {

  public MutationParameter(String[] args, List<String> mutationOperators) {
    super("mutation", args, mutationOperators);
  }

  public MutationOperator<DoubleSolution> getDoubleSolutionParameter() {
    MutationOperator<DoubleSolution> result;
    var numberOfProblemVariables = (int) getNonConfigurableParameter("numberOfProblemVariables");
    var mutationProbability = (double) findGlobalParameter(
        "mutationProbabilityFactor").getValue() / numberOfProblemVariables;
    var repairDoubleSolution =
        (RepairDoubleSolutionStrategyParameter) findGlobalParameter("mutationRepairStrategy");

    switch (getValue()) {
      case "polynomial":
        var distributionIndex =
            (Double) findSpecificParameter("polynomialMutationDistributionIndex").getValue();
        result =
            new PolynomialMutation(
                mutationProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "linkedPolynomial":
        distributionIndex =
            (Double) findSpecificParameter("linkedPolynomialMutationDistributionIndex").getValue();
        result =
            new LinkedPolynomialMutation(
                mutationProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "uniform":
        var perturbation = (Double) findSpecificParameter(
            "uniformMutationPerturbation").getValue();
        result =
            new UniformMutation(mutationProbability, perturbation,
                repairDoubleSolution.getParameter());
        break;
      case "nonUniform":
        perturbation = (Double) findSpecificParameter("nonUniformMutationPerturbation").getValue();
        int maxIterations = (Integer) getNonConfigurableParameter("maxIterations");
        result =
            new NonUniformMutation(mutationProbability, perturbation, maxIterations,
                repairDoubleSolution.getParameter());
        break;
      default:
        throw new JMetalException("Mutation operator does not exist: " + getName());
    }
    return result;
  }

  public MutationOperator<BinarySolution> getBinarySolutionParameter() {
    MutationOperator<BinarySolution> result;
    var numberOfBitsInASolution = (int) getNonConfigurableParameter("numberOfBitsInASolution");
    var mutationProbability = (double) findGlobalParameter(
        "mutationProbabilityFactor").getValue() / numberOfBitsInASolution;

    switch (getValue()) {
      case "bitFlip":
        result = new BitFlipMutation(mutationProbability);
        break;
      default:
        throw new JMetalException("Mutation operator does not exist: " + getName());
    }
    return result;
  }

  @Override
  public String getName() {
    return "mutation";
  }
}
