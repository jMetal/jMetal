package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

public class HypervolumeContribution<S extends Solution<?>> {
  private static final double DEFAULT_OFFSET = 100.0;
  private double offset = DEFAULT_OFFSET;
  private Hypervolume hypervolume ;
  public String attributeId = getClass().getName() ;

  public HypervolumeContribution(Hypervolume hypervolume) {
    this(hypervolume, DEFAULT_OFFSET) ;
  }
  public HypervolumeContribution(Hypervolume hypervolume, double offset) {
    this.hypervolume = hypervolume ;
    this.offset = offset ;
  }

  public List<S> computeHypervolumeContribution(List<S> solutionList, List<S> referenceSolutionList) {
    double[][] referenceFront = getMatrixWithObjectiveValues(referenceSolutionList) ;
    if (solutionList.size() > 1) {
      double[][] front = getMatrixWithObjectiveValues(solutionList) ;
      double[][] normalizedFront =
              NormalizeUtils.normalize(
                      front,
                      NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                      NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      double[] minimumValues = NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront);
      double[] maximumValues = NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront);

      // compute offsets for reference point in normalized space
      double[] offsets = new double[maximumValues.length];
      for (int i = 0; i < maximumValues.length; i++) {
        offsets[i] = offset / (maximumValues[i] - minimumValues[i]);
      }
      // STEP 3. Inverse the pareto front. This is needed because the original
      // metric by Zitzler is for maximization problem
      double[][] invertedFront = VectorUtils.getInvertedFront(normalizedFront);

      // shift away from origin, so that boundary points also get a contribution > 0
      for (double[] point : invertedFront) {
        for (int j = 0; j < point.length; j++) {
          point[j] = point[j] + offsets[j];
        }
      }

      // calculate contributions and sort
      double[] contributions = hvContributions(invertedFront);
      for (int i = 0; i < contributions.length; i++) {
        solutionList.get(i).attributes().put(attributeId, contributions[i]) ;
      }

      solutionList.sort(new HypervolumeContributionComparator<S>());
    }
    return solutionList;
  }

  public double getOffset() {
    return offset;
  }

  public void setOffset(double offset) {
    this.offset = offset ;
  }

  /**
   * Calculates how much hypervolume each point dominates exclusively. The points have to be
   * transformed beforehand, to accommodate the assumptions of Zitzler's hypervolume code.
   *
   * @param front transformed objective values
   * @return HV contributions
   */
  private double[] hvContributions(double[][] front) {
    double[] contributions = new double[front.length];
    double[][] frontSubset = new double[front.length - 1][front[0].length];
    LinkedList<double[]> frontCopy = new LinkedList<double[]>();
    Collections.addAll(frontCopy, front);
    double[][] totalFront = frontCopy.toArray(frontSubset);
    double totalVolume = hypervolume.compute(totalFront) ;
    for (int i = 0; i < front.length; i++) {
      double[] evaluatedPoint = frontCopy.remove(i);
      frontSubset = frontCopy.toArray(frontSubset);

      double hv = hypervolume.compute(frontSubset);
      double contribution = totalVolume - hv;
      contributions[i] = contribution;

      frontCopy.add(i, evaluatedPoint);
    }
    return contributions;
  }
}
