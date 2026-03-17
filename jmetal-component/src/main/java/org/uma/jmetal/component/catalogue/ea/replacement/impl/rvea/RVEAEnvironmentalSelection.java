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

  private final int maxGenerations;
  private int currentGeneration;
  private final double alpha;
  private final double fr; // frequency of reference vector adaptation
  private final int numberOfObjectives;
  private double[][] referenceVectors;
  private final double[][] initialReferenceVectors;
  private double[] zMin;
  private double[] zMax;

  public RVEAEnvironmentalSelection(int numberOfObjectives, int maxGenerations, double alpha, double fr, int H) {
    this.numberOfObjectives = numberOfObjectives;
    this.maxGenerations = maxGenerations;
    this.currentGeneration = 0;
    this.alpha = alpha;
    this.fr = fr;

    // Generate initial reference vectors using ReferencePointGenerator (single layer or two layers)
    List<double[]> generatedVectors;
    if (numberOfObjectives <= 5) {
      generatedVectors = ReferencePointGenerator.generateSingleLayer(numberOfObjectives, H);
    } else {
      // 2 layers for many-objective setups, but for simplicity we rely on H for single layer if not specific
      // In jMetal's RVEA, often it's configured similar to NSGA-III
      // For general cases, let's just use H as the number of divisions
      generatedVectors = ReferencePointGenerator.generateSingleLayer(numberOfObjectives, H);
    }

    this.referenceVectors = new double[generatedVectors.size()][numberOfObjectives];
    this.initialReferenceVectors = new double[generatedVectors.size()][numberOfObjectives];

    for (int i = 0; i < generatedVectors.size(); i++) {
        // Normalize the generated vector
        double length = 0.0;
        for (int j = 0; j < numberOfObjectives; j++) {
            length += generatedVectors.get(i)[j] * generatedVectors.get(i)[j];
        }
        length = Math.sqrt(length);
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

    updateIdealAndNadirPoints(jointPopulation);
    
    // RVA: Reference Vector Adaptation
    if (currentGeneration % Math.ceil(maxGenerations * fr) == 0) {
        adaptReferenceVectors();
    }

    // Associate
    List<List<S>> subPopulations = new ArrayList<>(referenceVectors.length);
    for (int i = 0; i < referenceVectors.length; i++) {
        subPopulations.add(new ArrayList<>());
    }

    for (S solution : jointPopulation) {
        int closestVectorIndex = associateToReferenceVector(solution);
        subPopulations.get(closestVectorIndex).add(solution);
    }

    // Calculate gamma (minimum angle between reference vectors)
    double[] gamma = calculateGamma();

    // Select
    List<S> nextPopulation = new ArrayList<>();
    for (int i = 0; i < referenceVectors.length; i++) {
        List<S> subPopulation = subPopulations.get(i);
        if (!subPopulation.isEmpty()) {
            S bestSolution = selectBestFromSubpopulation(subPopulation, i, gamma[i]);
            nextPopulation.add(bestSolution);
        }
    }

    // If nextPopulation is smaller than required (because some vectors have no associated solutions),
    // fill with remaining solutions randomly or by APD to other vectors.
    // In standard RVEA, empty vectors are just ignored, but to maintain exactly populationSize:
    if (nextPopulation.size() < populationSize) {
        fillWithRemaining(nextPopulation, jointPopulation, populationSize);
    } else if (nextPopulation.size() > populationSize) {
        // Just truncate if oversized (rare, because referenceVectors.length is typically ~populationSize)
        nextPopulation = new ArrayList<>(nextPopulation.subList(0, populationSize));
    }

    return nextPopulation;
  }

  private void updateIdealAndNadirPoints(List<S> population) {
    if (zMin == null) {
      zMin = new double[numberOfObjectives];
      zMax = new double[numberOfObjectives];
      Arrays.fill(zMin, Double.POSITIVE_INFINITY);
      Arrays.fill(zMax, Double.NEGATIVE_INFINITY);
    }
    
    // Always recalculate zMax every generation for RVA, and update zMin dynamically
    Arrays.fill(zMax, Double.NEGATIVE_INFINITY);
    
    for (S sol : population) {
        for (int i = 0; i < numberOfObjectives; i++) {
            double obj = sol.objectives()[i];
            if (obj < zMin[i]) zMin[i] = obj;
            if (obj > zMax[i]) zMax[i] = obj;
        }
    }
  }

  private void adaptReferenceVectors() {
      for (int i = 0; i < referenceVectors.length; i++) {
          double length = 0.0;
          for (int j = 0; j < numberOfObjectives; j++) {
              referenceVectors[i][j] = initialReferenceVectors[i][j] * (zMax[j] - zMin[j]);
              length += referenceVectors[i][j] * referenceVectors[i][j];
          }
          length = Math.sqrt(length);
          if (length > 0) {
              for (int j = 0; j < numberOfObjectives; j++) {
                  referenceVectors[i][j] /= length;
              }
          }
      }
  }

  private int associateToReferenceVector(S solution) {
      double[] normalizedObj = new double[numberOfObjectives];
      for (int i = 0; i < numberOfObjectives; i++) {
          normalizedObj[i] = solution.objectives()[i] - zMin[i];
      }

      double minAngle = Double.POSITIVE_INFINITY;
      int bestIndex = 0;

      double norm = calculateNorm(normalizedObj);
      if (norm == 0.0) return 0;

      for (int i = 0; i < referenceVectors.length; i++) {
          double cosine = calculateCosine(normalizedObj, referenceVectors[i], norm);
          // Angle is acos(cosine)
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

      double penaltyRatio = ((double) currentGeneration / maxGenerations);
      
      for (S solution : subPopulation) {
          double[] normalizedObj = new double[numberOfObjectives];
          for (int i = 0; i < numberOfObjectives; i++) {
              normalizedObj[i] = solution.objectives()[i] - zMin[i];
          }
          
          double norm = calculateNorm(normalizedObj);
          double cosine = calculateCosine(normalizedObj, referenceVectors[referenceVectorIndex], norm);
          double theta = Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
          
          // APD calculation
          double pTheta = numberOfObjectives * Math.pow(penaltyRatio, alpha) * (theta / gamma);
          double apd = (1.0 + pTheta) * norm;
          
          if (apd < minAPD) {
              minAPD = apd;
              bestSolution = solution;
          }
      }
      return bestSolution;
  }

  private double[] calculateGamma() {
      // Gamma is the minimum angle between referenceVector i and any other referenceVector j
      double[] gamma = new double[referenceVectors.length];
      for (int i = 0; i < referenceVectors.length; i++) {
          double minAngle = Double.POSITIVE_INFINITY;
          for (int j = 0; j < referenceVectors.length; j++) {
              if (i == j) continue;
              double cosine = calculateCosine(referenceVectors[i], referenceVectors[j], 1.0); // Vectors are normalized
              double angle = Math.acos(Math.max(-1.0, Math.min(1.0, cosine)));
              if (angle < minAngle) {
                  minAngle = angle;
              }
          }
          gamma[i] = minAngle;
      }
      return gamma;
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
      // v2 is a reference vector, so its norm is 1.0
      double denom = norm1 * 1.0; 
      if (denom == 0) return 1.0;
      return dotProduct / denom;
  }

  private void fillWithRemaining(List<S> nextPopulation, List<S> jointPopulation, int targetSize) {
      List<S> pool = new ArrayList<>(jointPopulation);
      pool.removeAll(nextPopulation);
      // For simplicity, shuffle or randomly pick. RVEA standard is typically strict about vector counts 
      // but if population isn't full, we add random remaining solutions to maintain diversity.
      // Easiest is to add them sequentially or by some simple metric.
      // Here we randomly add remaining solutions
      java.util.Collections.shuffle(pool);
      while (nextPopulation.size() < targetSize && !pool.isEmpty()) {
          nextPopulation.add(pool.remove(0));
      }
  }
}
