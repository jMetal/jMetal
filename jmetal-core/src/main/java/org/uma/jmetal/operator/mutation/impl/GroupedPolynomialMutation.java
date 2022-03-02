package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.naming.grouping.CollectionGrouping;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

import java.util.List;

/**
 * This class implements the grouped polynomial mutation operator presented in:
 * https://doi.org/10.1109/SSCI.2016.7850214
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GroupedPolynomialMutation implements MutationOperator<DoubleSolution> {
  private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0;
  private double distributionIndex;
  private RepairDoubleSolution solutionRepair;
  private CollectionGrouping<List<Double>> variableGrouping;

  private PseudoRandomGenerator randomGenerator;

  /** Constructor */
  public GroupedPolynomialMutation(CollectionGrouping<List<Double>> variableGrouping) {
    this(DEFAULT_DISTRIBUTION_INDEX, variableGrouping);
  }

  /** Constructor */
  public GroupedPolynomialMutation(
      double distributionIndex, CollectionGrouping<List<Double>> variableGrouping) {
    this(distributionIndex, new RepairDoubleSolutionWithBoundValue(), variableGrouping);
  }

  /** Constructor */
  public GroupedPolynomialMutation(
      double distributionIndex,
      RepairDoubleSolution solutionRepair,
      CollectionGrouping<List<Double>> variableGrouping) {
    this(distributionIndex, solutionRepair, new JavaRandomGenerator(), variableGrouping);
  }

  /** Constructor */
  public GroupedPolynomialMutation(
      double distributionIndex,
      RepairDoubleSolution solutionRepair,
      PseudoRandomGenerator randomGenerator,
      CollectionGrouping<List<Double>> variableGrouping) {
    Check.that(distributionIndex >= 0, "Distribution index is negative: " + distributionIndex);
    this.distributionIndex = distributionIndex;
    this.solutionRepair = solutionRepair;
    this.randomGenerator = randomGenerator;
    this.variableGrouping = variableGrouping;
  }

  /* Getters */
  @Override
  public double getMutationProbability() {
    return 1.0;
  }

  public double getDistributionIndex() {
    return distributionIndex;
  }

  public void setDistributionIndex(double distributionIndex) {
    this.distributionIndex = distributionIndex;
  }

  /** Execute() method */
  @Override
  public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
    Check.notNull(solution);

    doMutation(solution);

    return solution;
  }

  /** Perform the mutation operation */
  private void doMutation(DoubleSolution solution) {
    double rnd, delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;

    variableGrouping.computeGroups(solution.variables());
    int groupIndex = randomGenerator.nextInt(0, variableGrouping.numberOfGroups() - 1);
    List<Integer> variableIndex = variableGrouping.getGroup(groupIndex);

    for (int i = 0; i < variableIndex.size(); i++) {
      y = solution.variables().get(variableIndex.get(i));
      Bounds<Double> bounds = solution.getBounds(variableIndex.get(i));
      yl = bounds.getLowerBound();
      yu = bounds.getUpperBound();
      if (yl == yu) {
        y = yl;
      } else {
        delta1 = (y - yl) / (yu - yl);
        delta2 = (yu - y) / (yu - yl);
        rnd = randomGenerator.nextDouble();
        mutPow = 1.0 / (distributionIndex + 1.0);
        if (rnd <= 0.5) {
          xy = 1.0 - delta1;
          val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, distributionIndex + 1.0));
          deltaq = Math.pow(val, mutPow) - 1.0;
        } else {
          xy = 1.0 - delta2;
          val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
          deltaq = 1.0 - Math.pow(val, mutPow);
        }
        y = y + deltaq * (yu - yl);
        y = solutionRepair.repairSolutionVariableValue(y, yl, yu);
      }
      solution.variables().set(variableIndex.get(i), y);
    }
  }
}
