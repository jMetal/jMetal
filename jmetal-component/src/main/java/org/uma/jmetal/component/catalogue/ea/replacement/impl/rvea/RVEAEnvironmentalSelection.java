package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

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

  public RVEAEnvironmentalSelection(int numberOfObjectives, int maxGenerations, double alpha, double fr, int H) {
    this(numberOfObjectives, maxGenerations, alpha, fr,
        ReferencePointGenerator.generateSingleLayer(numberOfObjectives, H));
  }

  public RVEAEnvironmentalSelection(
      int numberOfObjectives,
      int maxGenerations,
      double alpha,
      double fr,
      List<double[]> generatedVectors) {
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

    this.referenceVectors = new double[generatedVectors.size()][numberOfObjectives];
    this.initialReferenceVectors = new double[generatedVectors.size()][numberOfObjectives];
    this.referenceVectorNorms = new double[generatedVectors.size()];

    for (int i = 0; i < generatedVectors.size(); i++) {
      Check.that(generatedVectors.get(i).length == numberOfObjectives,
          "Reference vector dimension " + generatedVectors.get(i).length
              + " does not match the number of objectives " + numberOfObjectives + ".");
      double length = calculateNorm(generatedVectors.get(i));
      for (int j = 0; j < numberOfObjectives; j++) {
        this.referenceVectors[i][j] = generatedVectors.get(i)[j] / length;
        this.initialReferenceVectors[i][j] = this.referenceVectors[i][j];
      }
      this.referenceVectorNorms[i] = calculateNorm(this.referenceVectors[i]);
    }

    this.gamma = calculateGammaValues();
  }

  public List<S> execute(List<S> jointPopulation, int populationSize) {
    Check.notNull(jointPopulation);
    Check.that(jointPopulation.size() >= populationSize,
        "The joint population size must be at least the population size.");
    Check.that(referenceVectors.length == populationSize,
        "The population size must match the number of reference vectors. Expected "
            + referenceVectors.length + " and found " + populationSize + ".");

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

  private double[] calculateMinimumValues(List<S> population) {
    double[] minimumValues = new double[numberOfObjectives];
    Arrays.fill(minimumValues, Double.POSITIVE_INFINITY);

    for (S solution : population) {
      for (int i = 0; i < numberOfObjectives; i++) {
        minimumValues[i] = Math.min(minimumValues[i], solution.objectives()[i]);
      }
    }

    return minimumValues;
  }

  private double[] calculateMaximumValues(List<S> population) {
    double[] maximumValues = new double[numberOfObjectives];
    Arrays.fill(maximumValues, Double.NEGATIVE_INFINITY);

    for (S solution : population) {
      for (int i = 0; i < numberOfObjectives; i++) {
        maximumValues[i] = Math.max(maximumValues[i], solution.objectives()[i]);
      }
    }

    return maximumValues;
  }

  private double[][] translatedObjectives(List<S> population, double[] currentIdealPoint) {
    double[][] translatedObjectives = new double[population.size()][numberOfObjectives];

    for (int i = 0; i < population.size(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        translatedObjectives[i][j] = population.get(i).objectives()[j] - currentIdealPoint[j];
      }
    }

    return translatedObjectives;
  }

  private double[] translatedObjectiveNorms(double[][] translatedObjectives) {
    double[] norms = new double[translatedObjectives.length];

    for (int i = 0; i < translatedObjectives.length; i++) {
      norms[i] = calculateNorm(translatedObjectives[i]);
    }

    return norms;
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
