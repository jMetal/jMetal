package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.HypervolumeContribution2D;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ranking.Ranking;

/**
 * Improved version of SMSEMOAReplacement that uses efficient O(n log n) algorithms to compute
 * hypervolume contributions for 2-objective problems.
 *
 * <p>For 3+ objectives, it uses the PISA algorithm with contribution calculation.
 *
 * <p><b>Implementation status:</b>
 *
 * <ul>
 *   <li>✅ 2D: Implemented and tested - O(n log n)
 *   <li>✅ 3D+: Uses PISAHypervolume.computeHypervolumeContribution() - O(n²)
 * </ul>
 *
 * @param <S> Solution type
 * @author Antonio J. Nebro
 */
public class SMSEMOAReplacement<S extends Solution<?>> implements Replacement<S> {

  private final Ranking<S> ranking;
  private final Hypervolume hypervolume;

  public SMSEMOAReplacement(Ranking<S> ranking) {
    this.ranking = ranking;
    this.hypervolume = new WFGHypervolume();
  }

  public SMSEMOAReplacement(Ranking<S> ranking, Hypervolume hypervolume) {
    this.ranking = ranking;
    this.hypervolume = hypervolume;
  }

  @Override
  public List<S> replace(List<S> solutionList, List<S> offspringList) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(solutionList);
    jointPopulation.addAll(offspringList);

    ranking.compute(jointPopulation);

    List<S> lastSubFront = ranking.getSubFront(ranking.getNumberOfSubFronts() - 1);

    // Use efficient algorithm for 2D, standard algorithm for 3D+
    int numberOfObjectives = jointPopulation.get(0).objectives().length;
    if (numberOfObjectives == 2) {
      compute2DHypervolumeContributions(lastSubFront, jointPopulation);
    } else {
      computeNDHypervolumeContributions(lastSubFront, jointPopulation);
    }

    List<S> resultPopulation = new ArrayList<>();
    for (int i = 0; i < ranking.getNumberOfSubFronts() - 1; i++) {
      resultPopulation.addAll(ranking.getSubFront(i));
    }

    for (int i = 0; i < lastSubFront.size() - 1; i++) {
      resultPopulation.add(lastSubFront.get(i));
    }

    return resultPopulation;
  }

  /**
   * Computes hypervolume contributions for 2-objective problems.
   *
   * @param front Pareto front (last sub-front)
   * @param solutionList Complete list of solutions
   * @return Front sorted by HV contribution (descending)
   */
  private List<S> compute2DHypervolumeContributions(List<S> front, List<S> solutionList) {
    // Convert solutions to objectives matrix
    double[][] points = new double[front.size()][2];
    for (int i = 0; i < front.size(); i++) {
      System.arraycopy(front.get(i).objectives(), 0, points[i], 0, 2);
    }

    // Calculate reference point
    double[] referencePoint = calculateReferencePoint(solutionList);

    // Compute contributions using the efficient algorithm
    double[] contributions = HypervolumeContribution2D.compute(points, referencePoint);

    // Assign contributions to solutions as an attribute
    for (int i = 0; i < front.size(); i++) {
      front.get(i).attributes().put("HYPERVOLUME_CONTRIBUTION", contributions[i]);
    }

    // Sort by contribution DESCENDING (highest first, lowest last)
    // This way, the last solution has the lowest contribution and will be removed
    front.sort(
        Comparator.comparingDouble((S s) -> (Double) s.attributes().get("HYPERVOLUME_CONTRIBUTION"))
            .reversed());

    return front;
  }

  /**
   * Computes hypervolume contributions for 3 or more objectives problems. Uses the PISA algorithm
   * (O(n²) per point).
   *
   * @param front Pareto front (last sub-front)
   * @param solutionList Complete list of solutions
   * @return Front sorted by HV contribution (descending)
   */
  private List<S> computeNDHypervolumeContributions(List<S> front, List<S> solutionList) {
    // Convert solutions to objectives matrix
    int numberOfObjectives = solutionList.get(0).objectives().length;
    double[][] points = new double[front.size()][numberOfObjectives];
    for (int i = 0; i < front.size(); i++) {
      System.arraycopy(front.get(i).objectives(), 0, points[i], 0, numberOfObjectives);
    }

    // Calculate reference point
    double[] referencePoint = calculateReferencePoint(solutionList);

    // Configure hypervolume with the reference point
    hypervolume.setReferencePoint(referencePoint);

    // Compute contributions using PISAHypervolume
    double[] contributions = ((WFGHypervolume) hypervolume).computeHypervolumeContribution(points);

    // Assign contributions to solutions as an attribute
    for (int i = 0; i < front.size(); i++) {
      front.get(i).attributes().put("HYPERVOLUME_CONTRIBUTION", contributions[i]);
    }

    // Sort by contribution DESCENDING (highest first, lowest last)
    front.sort(
        Comparator.comparingDouble((S s) -> (Double) s.attributes().get("HYPERVOLUME_CONTRIBUTION"))
            .reversed());

    return front;
  }

  /** Calculates the reference point as the "worst" point in each objective plus a small offset. */
  private double[] calculateReferencePoint(List<S> solutionList) {
    int numberOfObjectives = solutionList.get(0).objectives().length;
    double[] referencePoint = new double[numberOfObjectives];

    for (int i = 0; i < numberOfObjectives; i++) {
      double maxValue = Double.NEGATIVE_INFINITY;
      for (S solution : solutionList) {
        if (solution.objectives()[i] > maxValue) {
          maxValue = solution.objectives()[i];
        }
      }
      referencePoint[i] = maxValue * 1.1; // 10% offset
    }

    return referencePoint;
  }
}
