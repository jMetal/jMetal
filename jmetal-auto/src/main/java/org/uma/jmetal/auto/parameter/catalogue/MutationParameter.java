package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.*;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class MutationParameter extends CategoricalParameter {

  public MutationParameter(List<String> mutationOperators) {
    super("mutation", mutationOperators);
  }

  public MutationOperator<DoubleSolution> getDoubleSolutionParameter() {
    MutationOperator<DoubleSolution> result;
    int numberOfProblemVariables = (int) getNonConfigurableParameter("numberOfProblemVariables");
    double mutationProbability = (double) findGlobalParameter(
        "mutationProbabilityFactor").value() / numberOfProblemVariables;
    RepairDoubleSolutionStrategyParameter repairDoubleSolution =
        (RepairDoubleSolutionStrategyParameter) findGlobalParameter("mutationRepairStrategy");

    switch (value()) {
      case "polynomial":
        Double distributionIndex =
            (Double) findSpecificParameter("polynomialMutationDistributionIndex").value();
        result =
            new PolynomialMutation(
                mutationProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "linkedPolynomial":
        distributionIndex =
            (Double) findSpecificParameter("linkedPolynomialMutationDistributionIndex").value();
        result =
            new LinkedPolynomialMutation(
                mutationProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "uniform":
        Double perturbation = (Double) findSpecificParameter(
            "uniformMutationPerturbation").value();
        result =
            new UniformMutation(mutationProbability, perturbation,
                repairDoubleSolution.getParameter());
        break;
      case "nonUniform":
        perturbation = (Double) findSpecificParameter("nonUniformMutationPerturbation").value();
        int maxIterations = (Integer) getNonConfigurableParameter("maxIterations");
        result =
            new NonUniformMutation(mutationProbability, perturbation, maxIterations,
                repairDoubleSolution.getParameter());
        break;
      default:
        throw new JMetalException("Mutation operator does not exist: " + name());
    }
    return result;
  }

  public MutationOperator<BinarySolution> getBinarySolutionParameter() {
    MutationOperator<BinarySolution> result;
    int numberOfBitsInASolution = (int) getNonConfigurableParameter("numberOfBitsInASolution");
    double mutationProbability = (double) findGlobalParameter(
        "mutationProbabilityFactor").value() / numberOfBitsInASolution;

    if ("bitFlip".equals(value())) {
      result = new BitFlipMutation<>(mutationProbability);
    } else {
      throw new JMetalException("Mutation operator does not exist: " + name());
    }
    return result;
  }

  public MutationOperator<PermutationSolution<Integer>> getPermutationParameter() {
    MutationOperator<PermutationSolution<Integer>> result;

    int permutationLength = (int) getNonConfigurableParameter("permutationLength");
    double mutationProbability = (double) findGlobalParameter(
            "mutationProbabilityFactor").value() / permutationLength;

    if ("PermutationSwap".equals(value())) {
      result = new PermutationSwapMutation<>(mutationProbability) ;
    } else {
      throw new JMetalException("Mutation operator does not exist: " + name());
    }

    return result ;
  }

  @Override
  public String name() {
    return "mutation";
  }
}
