package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.*;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Factory for crossover operators.
 */
public class CrossoverParameter extends CategoricalParameter {

  public CrossoverParameter(List<String> crossoverOperators) {
    super("crossover", crossoverOperators);
  }

  public CrossoverOperator<DoubleSolution> getDoubleSolutionParameter() {
    Double crossoverProbability = (Double) findGlobalParameter("crossoverProbability").value();
    RepairDoubleSolutionStrategyParameter repairDoubleSolution =
        (RepairDoubleSolutionStrategyParameter) findGlobalParameter("crossoverRepairStrategy");

    CrossoverOperator<DoubleSolution> result;
    switch (value()) {
      case "SBX":
        Double distributionIndex =
            (Double) findSpecificParameter("sbxDistributionIndex").value();
        result =
            new SBXCrossover(
                crossoverProbability, distributionIndex, repairDoubleSolution.getParameter());
        break;
      case "BLX_ALPHA":
        Double alpha = (Double) findSpecificParameter("blxAlphaCrossoverAlphaValue").value();
        result =
            new BLXAlphaCrossover(crossoverProbability, alpha, repairDoubleSolution.getParameter());
        break;
      case "wholeArithmetic":
        result =
            new WholeArithmeticCrossover(crossoverProbability, repairDoubleSolution.getParameter());
        break;
      default:
        throw new JMetalException("Crossover operator does not exist: " + name());
    }
    return result;
  }

  public CrossoverOperator<BinarySolution> getBinarySolutionParameter() {
    Double crossoverProbability = (Double) findGlobalParameter("crossoverProbability").value();

    CrossoverOperator<BinarySolution> result;
    switch (value()) {
      case "HUX":
        result = new HUXCrossover<>(crossoverProbability);
        break;
      case "uniform":
        result = new UniformCrossover<>(crossoverProbability);
        break;
      case "singlePoint":
        result = new SinglePointCrossover<>(crossoverProbability);
        break;
      default:
        throw new JMetalException("Crossover operator does not exist: " + name());
    }
    return result;
  }

  public CrossoverOperator<PermutationSolution<Integer>> getPermutationParameter() {
    Double crossoverProbability = (Double) findGlobalParameter("crossoverProbability").value();

    CrossoverOperator<PermutationSolution<Integer>> result;

    switch (value()) {
      case "PMX":
        result = new PMXCrossover(crossoverProbability) ;
        break;
      case "CX":
        result = new CycleCrossover(crossoverProbability);
        break;
      case "OXD":
        result = new OXDCrossover(crossoverProbability);
        break;
      case "positionBased":
        result = new PositionBasedCrossover(crossoverProbability);
        break;
      case "edgeRecombination":
        result = new EdgeRecombinationCrossover(crossoverProbability);
        break;
      default:
        throw new JMetalException("Crossover operator does not exist: " + name());
    }

    return result ;
  }
}
