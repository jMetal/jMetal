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
  private double[] idealPoint;
  private double[] nadirPoint;

  public RVEAEnvironmentalSelection(int numberOfObjectives, int maxGenerations, double alpha, double fr, int H) {
    Check.that(numberOfObjectives >= 2, "The number of objectives must be at least 2.");
    Check.valueIsPositive(maxGenerations, "maxGenerations");
    Check.valueIsNotNegative(alpha, "alpha");
    Check.valueIsInRange(fr, EPSILON, 1.0, "fr");

    this.numberOfObjectives = numberOfObjectives;
    this.maxGenerations = maxGenerations;
    this.currentGeneration = 0;
    this.alpha = alpha;
    this.adaptationFrequency = Math.max(1, (int) Math.ceil(maxGenerations * fr));

    List<double[]> generatedVectors = ReferencePointGenerator.generateSingleLayer(numberOfObjectives, H);

    this.referenceVectors = new double[generatedVectors.size()][numberOfObjectives];
    this.initialReferenceVectors = new double[generatedVectors.size()][numberOfObjectives];

    for (int i = 0; i < generatedVectors.size(); i++) {
      double length = calculateNorm(generatedVectors.get(i));
      for (int j = 0; j < numberOfObjectives; j++) {
        this.referenceVectors[i][j] = generatedVectors.get(i)[j] / length;
        this.initialReferenceVectors[i][j] = this.referenceVectors[i][j];
      }
    }
  }

  public List<S> execute(List<S> jointPopulation, int populationSize) {
    Check.notNull(jointPopulation);
    Check.that(jointPopulation.size() >= populationSize,
        "The joint population size must be at least the population size.");

    currentGeneration++;

    updateIdealPoint(jointPopulation);

    List<List<S>> subPopulations = new ArrayList<>(referenceVectors.length);
    for (int i = 0; i < referenceVectors.length; i++) {
      subPopulations.add(new ArrayList<>());
    }

    for (S solution : jointPopulation) {
      int closestVectorIndex = associateToReferenceVector(solution);
      subPopulations.get(closestVectorIndex).add(solution);
    }

    double[] gamma = calculateGamma();

    List<S> nextPopulation = new ArrayList<>();
    for (int i = 0; i < referenceVectors.length; i++) {
      List<S> subPopulation = subPopulations.get(i);
      if (!subPopulation.isEmpty()) {
        S bestSolution = selectBestFromSubpopulation(subPopulation, i, gamma[i]);
        nextPopulation.add(bestSolution);
      }
    }

    updateNadirPoint(nextPopulation);

    if (mustAdaptReferenceVectors()) {
      adaptReferenceVectors();
    }

    return nextPopulation;
  }

  private void updateIdealPoint(List<S> population) {
    if (idealPoint == null) {
      idealPoint = new double[numberOfObjectives];
      Arrays.fill(idealPoint, Double.POSITIVE_INFINITY);
    }

    for (S sol : population) {
      for (int i = 0; i < numberOfObjectives; i++) {
        idealPoint[i] = Math.min(idealPoint[i], sol.objectives()[i]);
      }
    }
  }

  private void updateNadirPoint(List<S> population) {
    if (population.isEmpty()) {
      nadirPoint = null;
      return;
    }

    nadirPoint = new double[numberOfObjectives];
    Arrays.fill(nadirPoint, Double.NEGATIVE_INFINITY);

    for (S sol : population) {
      for (int i = 0; i < numberOfObjectives; i++) {
        nadirPoint[i] = Math.max(nadirPoint[i], sol.objectives()[i]);
      }
    }
  }

  private boolean mustAdaptReferenceVectors() {
    return nadirPoint != null && currentGeneration % adaptationFrequency == 0;
  }

  private void adaptReferenceVectors() {
    for (int i = 0; i < referenceVectors.length; i++) {
      double length = 0.0;
      for (int j = 0; j < numberOfObjectives; j++) {
        double scale = Math.max(nadirPoint[j] - idealPoint[j], EPSILON);
        referenceVectors[i][j] = initialReferenceVectors[i][j] * scale;
        length += referenceVectors[i][j] * referenceVectors[i][j];
      }

      length = Math.sqrt(length);
      for (int j = 0; j < numberOfObjectives; j++) {
        referenceVectors[i][j] /= length;
      }
    }
  }

  private int associateToReferenceVector(S solution) {
    double[] translatedObjectives = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      translatedObjectives[i] = solution.objectives()[i] - idealPoint[i];
    }

    double minAngle = Double.POSITIVE_INFINITY;
    int bestIndex = 0;

    double norm = calculateNorm(translatedObjectives);
    if (norm == 0.0) {
      return 0;
    }

    for (int i = 0; i < referenceVectors.length; i++) {
      double cosine = calculateCosine(translatedObjectives, referenceVectors[i], norm);
      double angle = Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
      if (angle < minAngle) {
        minAngle = angle;
        bestIndex = i;
      }
    }
    return bestIndex;
  }

  private S selectBestFromSubpopulation(List<S> subPopulation, int referenceVectorIndex, double gamma) {
    double minAPD = Double.POSITIVE_INFINITY;
    S bestSolution = null;
    double progress = Math.pow((double) currentGeneration / maxGenerations, alpha);
    double objectivePenaltyFactor = numberOfObjectives > 2 ? numberOfObjectives : 1.0;

    for (S solution : subPopulation) {
      double[] translatedObjectives = new double[numberOfObjectives];
      for (int i = 0; i < numberOfObjectives; i++) {
        translatedObjectives[i] = solution.objectives()[i] - idealPoint[i];
      }

      double norm = calculateNorm(translatedObjectives);
      double cosine = calculateCosine(translatedObjectives, referenceVectors[referenceVectorIndex], norm);
      double theta = Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));

      double penalty = objectivePenaltyFactor * progress * (theta / gamma);
      double apd = (1.0 + penalty) * norm;

      if (apd < minAPD) {
        minAPD = apd;
        bestSolution = solution;
      }
    }
    return bestSolution;
  }

  private double[] calculateGamma() {
    double[] gamma = new double[referenceVectors.length];
    for (int i = 0; i < referenceVectors.length; i++) {
      double minAngle = Double.POSITIVE_INFINITY;
      for (int j = 0; j < referenceVectors.length; j++) {
        if (i == j) {
          continue;
        }

        double cosine = calculateCosine(referenceVectors[i], referenceVectors[j], 1.0);
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

  private double calculateNorm(double[] array) {
    double sum = 0.0;
    for (double v : array) {
      sum += v * v;
    }
    return Math.sqrt(sum);
  }

  private double calculateCosine(double[] v1, double[] v2, double norm1) {
    double dotProduct = 0;
    for (int i = 0; i < v1.length; i++) {
      dotProduct += v1[i] * v2[i];
    }

    double denominator = norm1;
    if (denominator == 0.0) {
      return 1.0;
    }

    return dotProduct / denominator;
  }
}
