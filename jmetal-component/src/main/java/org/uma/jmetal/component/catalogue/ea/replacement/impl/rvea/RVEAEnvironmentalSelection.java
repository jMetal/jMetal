package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Environmental Selection component for the RVEA algorithm.
 * Reference: R. Cheng, Y. Jin, M. Olhofer and B. Sendhoff,
 * "A Reference Vector Guided Evolutionary Algorithm for Many-Objective Optimization,"
 * IEEE Transactions on Evolutionary Computation, 2016.
 *
 * @param <S> Type of the solution
 */
public class RVEAEnvironmentalSelection<S extends Solution<?>> {
  private static final double EPSILON = 1.0e-64;

  private final int maxGenerations;
  private final int adaptationFrequency;
  private int currentGeneration;
  private final double alpha;
  private final int numberOfObjectives;
  private double[][] referenceVectors;
  private final double[][] initialReferenceVectors;
  private final double[] referenceVectorNorms;
  private double[] idealPoint;
  private double[] nadirPoint;
  private double[] gamma;

  /**
   * Creates the environmental selection component of RVEA.
   *
   * @param referenceVectors Reference vectors provided by the caller
   * @param maxGenerations Maximum number of generations
   * @param alpha APD penalty exponent
   * @param fr Reference vector adaptation frequency ratio
   */
  public RVEAEnvironmentalSelection(double[][] referenceVectors, int maxGenerations, double alpha, double fr) {
    Check.arrayIsNotEmpty(referenceVectors, "referenceVectors");
    Check.notNull(referenceVectors[0], "referenceVectors[0]");

    int numberOfObjectives = referenceVectors[0].length;
    Check.that(numberOfObjectives >= 2, "The number of objectives must be at least 2.");
    Check.valueIsPositive(maxGenerations, "maxGenerations");
    Check.valueIsNotNegative(alpha, "alpha");
    Check.valueIsInRange(fr, EPSILON, 1.0, "fr");
    Check.notNull(generatedVectors);
    Check.that(!generatedVectors.isEmpty(), "The reference vector list cannot be empty.");

    this.numberOfObjectives = numberOfObjectives;
    this.maxGenerations = maxGenerations;
    this.currentGeneration = -1;
    this.alpha = alpha;
    this.adaptationFrequency = Math.max(1, (int) Math.ceil(maxGenerations * fr));

    validateReferenceVectors(referenceVectors);
    this.referenceVectors = normalizedReferenceVectors(referenceVectors);
    this.initialReferenceVectors = copyReferenceVectors(this.referenceVectors);
  }

  public List<S> execute(List<S> jointPopulation, int populationSize) {
    Check.notNull(jointPopulation);
    Check.that(jointPopulation.size() >= populationSize,
        "The joint population size must be at least the population size.");
    Check.that(populationSize <= referenceVectors.length,
        "Population size must be less than or equal to the number of reference vectors.");

    currentGeneration++;

    // RVEA translates the current candidate population by the current-generation ideal point.
    idealPoint = calculateMinimumValues(jointPopulation);
    double[][] translatedObjectives = translatedObjectives(jointPopulation, idealPoint);
    double[] translatedObjectiveNorms = translatedObjectiveNorms(translatedObjectives);

    List<List<Integer>> subPopulations = new ArrayList<>(referenceVectors.length);
    for (int i = 0; i < referenceVectors.length; i++) {
      subPopulations.add(new ArrayList<>());
    }

    for (int i = 0; i < jointPopulation.size(); i++) {
      int closestVectorIndex =
          associateToReferenceVector(translatedObjectives[i], translatedObjectiveNorms[i]);
      subPopulations.get(closestVectorIndex).add(i);
    }

    List<S> nextPopulation = new ArrayList<>(populationSize);
    boolean[] selectedSolutions = new boolean[jointPopulation.size()];
    for (int i = 0; i < referenceVectors.length; i++) {
      int survivorIndex =
          selectBestIndexForReferenceVector(
              subPopulations.get(i),
              i,
              gamma[i],
              translatedObjectives,
              translatedObjectiveNorms,
              selectedSolutions);
      Check.that(
          survivorIndex != -1,
          "Unable to select a survivor for reference vector " + i + ".");
      selectedSolutions[survivorIndex] = true;
      nextPopulation.add(jointPopulation.get(survivorIndex));
    }

    Check.that(
        nextPopulation.size() == populationSize,
        "The next population size must be " + populationSize + " but is "
            + nextPopulation.size() + ".");

    // Reference-vector adaptation uses the current survivor bounds instead of historical extrema.
    double[] survivorIdealPoint = calculateMinimumValues(nextPopulation);
    nadirPoint = calculateMaximumValues(nextPopulation);

    if (mustAdaptReferenceVectors()) {
      adaptReferenceVectors(survivorIdealPoint, nadirPoint);
    }

    return nextPopulation;
  }

  private boolean mustAdaptReferenceVectors() {
    return nadirPoint != null && currentGeneration % adaptationFrequency == 0;
  }

  private void adaptReferenceVectors(double[] survivorIdealPoint, double[] survivorNadirPoint) {
    for (int i = 0; i < referenceVectors.length; i++) {
      double length = 0.0;
      for (int j = 0; j < numberOfObjectives; j++) {
        double scale = Math.max(survivorNadirPoint[j] - survivorIdealPoint[j], EPSILON);
        referenceVectors[i][j] = initialReferenceVectors[i][j] * scale;
        length += referenceVectors[i][j] * referenceVectors[i][j];
      }

      length = Math.sqrt(length);
      for (int j = 0; j < numberOfObjectives; j++) {
        referenceVectors[i][j] /= length;
      }
      referenceVectorNorms[i] = calculateNorm(referenceVectors[i]);
    }
    gamma = calculateGammaValues();
  }

  private int associateToReferenceVector(double[] translatedObjectives, double norm) {
    double minAngle = Double.POSITIVE_INFINITY;
    int bestIndex = 0;

    if (norm == 0.0) {
      return 0;
    }

    for (int i = 0; i < referenceVectors.length; i++) {
      double cosine =
          calculateCosine(translatedObjectives, norm, referenceVectors[i], referenceVectorNorms[i]);
      double angle = Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
      if (angle < minAngle) {
        minAngle = angle;
        bestIndex = i;
      }
    }
    return bestIndex;
  }

  private int selectBestIndexForReferenceVector(
      List<Integer> associatedCandidates,
      int referenceVectorIndex,
      double gamma,
      double[][] translatedObjectives,
      double[] translatedObjectiveNorms,
      boolean[] selectedSolutions) {
    double minAPD = Double.POSITIVE_INFINITY;
    int bestIndex = -1;
    double progress = Math.pow((double) currentGeneration / maxGenerations, alpha);
    double objectivePenaltyFactor = numberOfObjectives;

    for (int candidateIndex : associatedCandidates) {
      if (selectedSolutions[candidateIndex]) {
        continue;
      }

      double apd =
          calculateApd(
              candidateIndex,
              referenceVectorIndex,
              gamma,
              progress,
              objectivePenaltyFactor,
              translatedObjectives,
              translatedObjectiveNorms);
      if (apd < minAPD) {
        minAPD = apd;
        bestIndex = candidateIndex;
      }
    }

    if (bestIndex != -1) {
      return bestIndex;
    }

    // Empty niches fall back to the best remaining APD candidate to keep the survivor count fixed.
    for (int candidateIndex = 0; candidateIndex < selectedSolutions.length; candidateIndex++) {
      if (selectedSolutions[candidateIndex]) {
        continue;
      }

      double apd =
          calculateApd(
              candidateIndex,
              referenceVectorIndex,
              gamma,
              progress,
              objectivePenaltyFactor,
              translatedObjectives,
              translatedObjectiveNorms);
      if (apd < minAPD) {
        minAPD = apd;
        bestIndex = candidateIndex;
      }
    }

    return bestIndex;
  }

  private double calculateApd(
      int candidateIndex,
      int referenceVectorIndex,
      double gamma,
      double progress,
      double objectivePenaltyFactor,
      double[][] translatedObjectives,
      double[] translatedObjectiveNorms) {
    double norm = translatedObjectiveNorms[candidateIndex];
    double cosine = calculateCosine(
        translatedObjectives[candidateIndex],
        norm,
        referenceVectors[referenceVectorIndex],
        referenceVectorNorms[referenceVectorIndex]);
    double theta = Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));

    // APD balances convergence and angular diversity relative to the assigned vector.
    double penalty = objectivePenaltyFactor * progress * (theta / gamma);
    return (1.0 + penalty) * norm;
  }

  private double[] calculateGammaValues() {
    double[] gamma = new double[referenceVectors.length];
    for (int i = 0; i < referenceVectors.length; i++) {
      double minAngle = Double.POSITIVE_INFINITY;
      for (int j = 0; j < referenceVectors.length; j++) {
        if (i == j) {
          continue;
        }

        double cosine = calculateCosine(
            referenceVectors[i], referenceVectorNorms[i], referenceVectors[j], referenceVectorNorms[j]);
        double angle = Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
        if (angle < minAngle) {
          minAngle = angle;
        }
      }

      gamma[i] = Math.max(minAngle, EPSILON);
    }
    return gamma;
  }

  double[][] referenceVectors() {
    double[][] copy = new double[referenceVectors.length][];
    for (int i = 0; i < referenceVectors.length; i++) {
      copy[i] = referenceVectors[i].clone();
    }
    return copy;
  }

  double[] idealPoint() {
    return idealPoint == null ? null : idealPoint.clone();
  }

  double[] nadirPoint() {
    return nadirPoint == null ? null : nadirPoint.clone();
  }

  int currentGeneration() {
    return currentGeneration;
  }

  private void validateReferenceVectors(double[][] vectors) {
    for (int i = 0; i < vectors.length; i++) {
      double[] vector = vectors[i];
      Check.notNull(vector, "referenceVectors[" + i + "]");
      Check.that(vector.length == numberOfObjectives,
          "All reference vectors must have " + numberOfObjectives + " components.");

      double norm = 0.0;
      for (int j = 0; j < vector.length; j++) {
        Check.valueIsFinite(vector[j], "referenceVectors[" + i + "][" + j + "]");
        Check.that(vector[j] >= 0.0, "Reference vectors must contain non-negative values.");
        norm += vector[j] * vector[j];
      }

      Check.that(norm > 0.0, "Reference vectors must not be the zero vector.");
    }
  }

  private double[][] normalizedReferenceVectors(double[][] vectors) {
    double[][] normalizedVectors = new double[vectors.length][numberOfObjectives];

    for (int i = 0; i < vectors.length; i++) {
      double length = calculateNorm(vectors[i]);
      for (int j = 0; j < numberOfObjectives; j++) {
        normalizedVectors[i][j] = vectors[i][j] / length;
      }
    }

    return normalizedVectors;
  }

  private double[][] copyReferenceVectors(double[][] vectors) {
    double[][] copiedVectors = new double[vectors.length][];

    for (int i = 0; i < vectors.length; i++) {
      copiedVectors[i] = vectors[i].clone();
    }

    return copiedVectors;
  }

  private double calculateNorm(double[] array) {
    double sum = 0.0;
    for (double v : array) {
      sum += v * v;
    }
    return Math.sqrt(sum);
  }

  private double calculateCosine(double[] v1, double norm1, double[] v2, double norm2) {
    double dotProduct = 0;
    for (int i = 0; i < v1.length; i++) {
      dotProduct += v1[i] * v2[i];
    }

    double denominator = norm1 * norm2;
    if (denominator == 0.0) {
      return 1.0;
    }

    return dotProduct / denominator;
  }
}
