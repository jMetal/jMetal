package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;

/**
 * Replacement component implementing NSGA-III's environmental selection.
 *
 * <p>
 * This component implements the reference-point-based niching mechanism
 * described in the NSGA-III paper for selecting solutions from the critical
 * front.
 *
 * <p>
 * Reference: K. Deb and H. Jain, "An Evolutionary Many-Objective Optimization
 * Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part I:
 * Solving Problems With Box Constraints," IEEE TEVC, vol. 18, no. 4, 2014.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of solution
 */
public class NSGAIIIReplacement<S extends Solution<?>> implements Replacement<S> {

  private static final String NICHE_DISTANCE_ATTRIBUTE = "NSGA3_NICHE_DISTANCE";
  private static final String REFERENCE_POINT_ATTRIBUTE = "NSGA3_REF_POINT_INDEX";

  private final Ranking<S> ranking;
  private final List<double[]> referencePoints;
  private final int numberOfObjectives;
  private final int populationSize;

  /**
   * Constructor.
   *
   * @param ranking            Non-dominated sorting ranking
   * @param referencePoints    List of reference points
   * @param numberOfObjectives Number of objectives
   * @param populationSize     Target population size
   */
  public NSGAIIIReplacement(
      Ranking<S> ranking,
      List<double[]> referencePoints,
      int numberOfObjectives,
      int populationSize) {
    Check.notNull(ranking);
    Check.notNull(referencePoints);
    Check.that(!referencePoints.isEmpty(), "Reference points cannot be empty");

    this.ranking = ranking;
    this.referencePoints = referencePoints;
    this.numberOfObjectives = numberOfObjectives;
    this.populationSize = populationSize;
  }

  @Override
  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    // Combine populations
    List<S> jointPopulation = new ArrayList<>(population.size() + offspringPopulation.size());
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    // Non-dominated sorting
    ranking.compute(jointPopulation);

    // Fill with complete fronts until we exceed population size
    List<S> resultPopulation = new ArrayList<>(populationSize);
    List<List<S>> fronts = new ArrayList<>();
    int rankingIndex = 0;

    while (resultPopulation.size() + ranking.getSubFront(rankingIndex).size() <= populationSize) {
      List<S> front = ranking.getSubFront(rankingIndex);
      fronts.add(front);
      resultPopulation.addAll(front);
      rankingIndex++;

      if (rankingIndex >= ranking.getNumberOfSubFronts()) {
        break;
      }
    }

    // If we need more solutions, apply niching to the critical front
    if (resultPopulation.size() < populationSize && rankingIndex < ranking.getNumberOfSubFronts()) {
      List<S> criticalFront = ranking.getSubFront(rankingIndex);
      fronts.add(criticalFront);

      int remaining = populationSize - resultPopulation.size();
      List<S> selected = nicheBasedSelection(fronts, criticalFront, remaining);
      resultPopulation.addAll(selected);
    }

    return resultPopulation;
  }

  /**
   * Performs niching-based selection from the critical front.
   * Implements Algorithm 4 from the NSGA-III paper.
   */
  private List<S> nicheBasedSelection(List<List<S>> fronts, List<S> criticalFront, int remaining) {
    // Step 1: Normalize objectives
    double[] idealPoint = computeIdealPoint(fronts);
    List<double[]> extremePoints = findExtremePoints(fronts.get(0), idealPoint);
    double[] intercepts = constructHyperplane(extremePoints, fronts.get(0), idealPoint);

    // Step 2: Associate all solutions with reference points and compute niche
    // counts
    int[] nicheCounts = new int[referencePoints.size()];
    List<List<SolutionWithDistance<S>>> referencePointMembers = new ArrayList<>(referencePoints.size());
    for (int i = 0; i < referencePoints.size(); i++) {
      referencePointMembers.add(new ArrayList<>());
    }

    // Process fronts before critical front (increment niche counts only)
    for (int f = 0; f < fronts.size() - 1; f++) {
      for (S solution : fronts.get(f)) {
        double[] normalized = normalizeObjectives(solution, idealPoint, intercepts);
        int refPointIndex = findClosestReferencePoint(normalized);
        nicheCounts[refPointIndex]++;

        solution.attributes().put(REFERENCE_POINT_ATTRIBUTE, refPointIndex);
      }
    }

    // Process critical front (track as potential members)
    for (S solution : criticalFront) {
      double[] normalized = normalizeObjectives(solution, idealPoint, intercepts);
      int refPointIndex = findClosestReferencePoint(normalized);
      double distance = perpendicularDistance(referencePoints.get(refPointIndex), normalized);

      referencePointMembers.get(refPointIndex).add(new SolutionWithDistance<>(solution, distance));
      solution.attributes().put(NICHE_DISTANCE_ATTRIBUTE, distance);
      solution.attributes().put(REFERENCE_POINT_ATTRIBUTE, refPointIndex);
    }

    // Sort potential members by distance for each reference point
    for (List<SolutionWithDistance<S>> members : referencePointMembers) {
      members.sort(Comparator.comparingDouble(m -> m.distance));
    }

    // Step 3: Niching-based selection (Algorithm 4)
    List<S> selected = new ArrayList<>(remaining);
    JMetalRandom random = JMetalRandom.getInstance();

    // Use TreeMap to efficiently find reference points with minimum niche count
    TreeMap<Integer, List<Integer>> nicheCountToRefPoints = new TreeMap<>();
    for (int i = 0; i < referencePoints.size(); i++) {
      if (!referencePointMembers.get(i).isEmpty()) {
        nicheCountToRefPoints.computeIfAbsent(nicheCounts[i], k -> new ArrayList<>()).add(i);
      }
    }

    while (selected.size() < remaining && !nicheCountToRefPoints.isEmpty()) {
      // Get reference points with minimum niche count
      int minNicheCount = nicheCountToRefPoints.firstKey();
      List<Integer> minNicheRefPoints = nicheCountToRefPoints.get(minNicheCount);

      // Randomly select one reference point from those with minimum count
      int selectedRefPointIdx = minNicheRefPoints.size() == 1
          ? 0
          : random.nextInt(0, minNicheRefPoints.size() - 1);
      int refPointIndex = minNicheRefPoints.get(selectedRefPointIdx);

      List<SolutionWithDistance<S>> potentialMembers = referencePointMembers.get(refPointIndex);

      if (!potentialMembers.isEmpty()) {
        // Select solution: closest if niche count is 0, random otherwise
        SolutionWithDistance<S> chosen;
        if (nicheCounts[refPointIndex] == 0) {
          chosen = potentialMembers.remove(0); // Closest
        } else {
          int randomIdx = potentialMembers.size() == 1
              ? 0
              : random.nextInt(0, potentialMembers.size() - 1);
          chosen = potentialMembers.remove(randomIdx);
        }

        selected.add(chosen.solution);

        // Update niche count
        minNicheRefPoints.remove(selectedRefPointIdx);
        if (minNicheRefPoints.isEmpty()) {
          nicheCountToRefPoints.remove(minNicheCount);
        }

        nicheCounts[refPointIndex]++;
        if (!potentialMembers.isEmpty()) {
          nicheCountToRefPoints.computeIfAbsent(nicheCounts[refPointIndex], k -> new ArrayList<>())
              .add(refPointIndex);
        }
      } else {
        // No more potential members for this reference point
        minNicheRefPoints.remove(selectedRefPointIdx);
        if (minNicheRefPoints.isEmpty()) {
          nicheCountToRefPoints.remove(minNicheCount);
        }
      }
    }

    return selected;
  }

  private double[] computeIdealPoint(List<List<S>> fronts) {
    double[] idealPoint = new double[numberOfObjectives];
    for (int f = 0; f < numberOfObjectives; f++) {
      idealPoint[f] = Double.MAX_VALUE;
    }

    for (S solution : fronts.get(0)) { // Ideal point from first front only
      for (int f = 0; f < numberOfObjectives; f++) {
        idealPoint[f] = Math.min(idealPoint[f], solution.objectives()[f]);
      }
    }
    return idealPoint;
  }

  private List<double[]> findExtremePoints(List<S> firstFront, double[] idealPoint) {
    List<double[]> extremePoints = new ArrayList<>(numberOfObjectives);

    for (int f = 0; f < numberOfObjectives; f++) {
      double minASF = Double.MAX_VALUE;
      double[] extremePoint = null;

      for (S solution : firstFront) {
        double[] translated = new double[numberOfObjectives];
        for (int i = 0; i < numberOfObjectives; i++) {
          translated[i] = solution.objectives()[i] - idealPoint[i];
        }

        double asf = achievementScalarizingFunction(translated, f);
        if (asf < minASF) {
          minASF = asf;
          extremePoint = translated;
        }
      }
      extremePoints.add(extremePoint);
    }
    return extremePoints;
  }

  private double achievementScalarizingFunction(double[] objectives, int index) {
    double maxRatio = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < objectives.length; i++) {
      double weight = (i == index) ? 1.0 : 0.000001;
      maxRatio = Math.max(maxRatio, objectives[i] / weight);
    }
    return maxRatio;
  }

  /**
   * Intercepts of the hyperplane through the extreme points, in the translated space. When the
   * extreme points do not define a hyperplane with positive intercepts, the largest translated
   * value of each objective in the first front is used instead, as the paper proposes.
   */
  private double[] constructHyperplane(List<double[]> extremePoints, List<S> firstFront,
      double[] idealPoint) {
    double[] intercepts = solveIntercepts(extremePoints);

    if (intercepts == null) {
      intercepts = new double[numberOfObjectives];
      for (S solution : firstFront) {
        for (int f = 0; f < numberOfObjectives; f++) {
          intercepts[f] = Math.max(intercepts[f], solution.objectives()[f] - idealPoint[f]);
        }
      }
    }

    for (int f = 0; f < numberOfObjectives; f++) {
      intercepts[f] = Math.max(intercepts[f], 1e-10);
    }
    return intercepts;
  }

  /** Solves E x = 1 by Gaussian elimination and returns 1 / x, or null if it is not valid. */
  private double[] solveIntercepts(List<double[]> extremePoints) {
    int n = numberOfObjectives;
    double[][] matrix = new double[n][n + 1];
    for (int i = 0; i < n; i++) {
      System.arraycopy(extremePoints.get(i), 0, matrix[i], 0, n);
      matrix[i][n] = 1.0;
    }

    for (int column = 0; column < n; column++) {
      int pivot = column;
      for (int row = column + 1; row < n; row++) {
        if (Math.abs(matrix[row][column]) > Math.abs(matrix[pivot][column])) {
          pivot = row;
        }
      }
      if (Math.abs(matrix[pivot][column]) < 1e-12) {
        return null;
      }
      double[] swap = matrix[column];
      matrix[column] = matrix[pivot];
      matrix[pivot] = swap;
      for (int row = column + 1; row < n; row++) {
        double factor = matrix[row][column] / matrix[column][column];
        for (int k = column; k <= n; k++) {
          matrix[row][k] -= factor * matrix[column][k];
        }
      }
    }

    double[] intercepts = new double[n];
    for (int row = n - 1; row >= 0; row--) {
      double value = matrix[row][n];
      for (int k = row + 1; k < n; k++) {
        value -= matrix[row][k] * intercepts[k];
      }
      double x = value / matrix[row][row];
      if (!Double.isFinite(x) || x <= 0.0) {
        return null;
      }
      intercepts[row] = x;
    }
    for (int f = 0; f < n; f++) {
      intercepts[f] = 1.0 / intercepts[f];
    }
    return intercepts;
  }

  private double[] normalizeObjectives(S solution, double[] idealPoint, double[] intercepts) {
    double[] normalized = new double[numberOfObjectives];
    for (int f = 0; f < numberOfObjectives; f++) {
      normalized[f] = (solution.objectives()[f] - idealPoint[f]) / intercepts[f];
    }
    return normalized;
  }

  private int findClosestReferencePoint(double[] normalizedObjectives) {
    int closest = 0;
    double minDistance = Double.MAX_VALUE;

    for (int r = 0; r < referencePoints.size(); r++) {
      double distance = perpendicularDistance(referencePoints.get(r), normalizedObjectives);
      if (distance < minDistance) {
        minDistance = distance;
        closest = r;
      }
    }
    return closest;
  }

  private double perpendicularDistance(double[] referenceDirection, double[] point) {
    double numerator = 0;
    double denominator = 0;

    for (int i = 0; i < referenceDirection.length; i++) {
      numerator += referenceDirection[i] * point[i];
      denominator += referenceDirection[i] * referenceDirection[i];
    }

    if (denominator < 1e-10) {
      return Double.MAX_VALUE;
    }

    double k = numerator / denominator;

    double distance = 0;
    for (int i = 0; i < referenceDirection.length; i++) {
      distance += Math.pow(k * referenceDirection[i] - point[i], 2.0);
    }

    return Math.sqrt(distance);
  }

  /**
   * Helper class to associate a solution with its distance to a reference point.
   */
  private static class SolutionWithDistance<S> {
    final S solution;
    final double distance;

    SolutionWithDistance(S solution, double distance) {
      this.solution = solution;
      this.distance = distance;
    }
  }
}
