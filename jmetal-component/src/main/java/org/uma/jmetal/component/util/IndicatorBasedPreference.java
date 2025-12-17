package org.uma.jmetal.component.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

/**
 * A preference based on indicator values (IBEA-style) that specifies preferences in the selection
 * and replacement components of an evolutionary algorithm.
 *
 * <p>This class computes indicator-based fitness values for solutions using the hypervolume
 * indicator, following the approach used in IBEA (Indicator-Based Evolutionary Algorithm). The
 * fitness value is stored as a solution attribute and can be used by both selection and replacement
 * components through the provided comparator.
 *
 * <p>Control parameters:
 *
 * <ul>
 *   <li><b>kappa</b>: Scaling factor for fitness calculation. Range: (0, +∞). Default: 0.05.
 *       Smaller values increase selection pressure toward better solutions.
 * </ul>
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class IndicatorBasedPreference<S extends Solution<?>> {

  public static final double DEFAULT_KAPPA = 0.05;

  private final double kappa;
  private final Fitness<S> solutionFitness;
  private List<List<Double>> indicatorValues;
  private double maxIndicatorValue;
  private IndicatorBasedPreference<S> relatedPreference;
  private boolean preferenceHasBeenComputedFirstTime = false;
  private int numberOfObjectives;

  /** Constructor with default kappa value. */
  public IndicatorBasedPreference(int numberOfObjectives) {
    this(numberOfObjectives, DEFAULT_KAPPA, null);
  }

  /**
   * Constructor with configurable kappa value.
   *
   * @param numberOfObjectives Number of objectives in the problem
   * @param kappa Scaling factor for fitness calculation (must be > 0)
   */
  public IndicatorBasedPreference(int numberOfObjectives, double kappa) {
    this(numberOfObjectives, kappa, null);
  }


  /**
   * Constructor with configurable kappa value and related preference for synchronization.
   *
   * @param numberOfObjectives Number of objectives in the problem
   * @param kappa Scaling factor for fitness calculation (must be > 0)
   * @param relatedPreference Related preference for synchronization between components
   */
  public IndicatorBasedPreference(
      int numberOfObjectives, double kappa, IndicatorBasedPreference<S> relatedPreference) {
    this.numberOfObjectives = numberOfObjectives;
    this.kappa = kappa;
    this.solutionFitness = new Fitness<>();
    this.relatedPreference = relatedPreference;
  }

  /**
   * Computes indicator-based fitness values for all solutions in the list. The fitness values are
   * stored as solution attributes.
   *
   * @param solutionList List of solutions to compute fitness for
   */
  public void compute(List<S> solutionList) {
    if (!preferenceHasBeenComputedFirstTime) {
      computeFitness(solutionList);
      preferenceHasBeenComputedFirstTime = true;
    } else if (relatedPreference != null) {
      // Only recompute if kappa values are different
      if (Math.abs(this.kappa - relatedPreference.getKappa()) > 1e-10) {
        computeFitness(solutionList);
      }
      // If kappa is the same, fitness values are already computed by related preference
    }
  }

  private void computeFitness(List<S> solutionList) {
    double[] maximumValues = new double[numberOfObjectives];
    double[] minimumValues = new double[numberOfObjectives];

    // Initialize bounds
    for (int i = 0; i < numberOfObjectives; i++) {
      maximumValues[i] = -Double.MAX_VALUE;
      minimumValues[i] = Double.MAX_VALUE;
    }

    // Find bounds
    for (S solution : solutionList) {
      for (int obj = 0; obj < numberOfObjectives; obj++) {
        double value = solution.objectives()[obj];
        if (value > maximumValues[obj]) {
          maximumValues[obj] = value;
        }
        if (value < minimumValues[obj]) {
          minimumValues[obj] = value;
        }
      }
    }

    computeIndicatorValues(solutionList, maximumValues, minimumValues);

    // Calculate fitness for each solution
    for (int pos = 0; pos < solutionList.size(); pos++) {
      calculateFitness(solutionList, pos);
    }
  }

  private void computeIndicatorValues(
      List<S> solutionList, double[] maximumValues, double[] minimumValues) {
    indicatorValues = new ArrayList<>();
    maxIndicatorValue = -Double.MAX_VALUE;

    DominanceWithConstraintsComparator<S> dominanceComparator =
        new DominanceWithConstraintsComparator<>();

    for (int j = 0; j < solutionList.size(); j++) {
      S solutionA = solutionList.get(j);
      List<Double> aux = new ArrayList<>();

      for (S solutionB : solutionList) {
        int flag = dominanceComparator.compare(solutionA, solutionB);

        double value;
        if (flag == -1) {
          value =
              -calculateHypervolumeIndicator(
                  solutionA, solutionB, numberOfObjectives, maximumValues, minimumValues);
        } else {
          value =
              calculateHypervolumeIndicator(
                  solutionB, solutionA, numberOfObjectives, maximumValues, minimumValues);
        }

        if (Math.abs(value) > maxIndicatorValue) {
          maxIndicatorValue = Math.abs(value);
        }
        aux.add(value);
      }
      indicatorValues.add(aux);
    }
  }


  /**
   * Calculates the hypervolume of that portion of the objective space that is dominated by
   * individual a but not by individual b.
   */
  private double calculateHypervolumeIndicator(
      S solutionA, S solutionB, int d, double[] maximumValues, double[] minimumValues) {
    double rho = 2.0;
    double r = rho * (maximumValues[d - 1] - minimumValues[d - 1]);
    double max = minimumValues[d - 1] + r;

    double a = solutionA.objectives()[d - 1];
    double b = (solutionB == null) ? max : solutionB.objectives()[d - 1];

    double volume;
    if (d == 1) {
      volume = (a < b) ? (b - a) / r : 0;
    } else {
      if (a < b) {
        volume =
            calculateHypervolumeIndicator(solutionA, null, d - 1, maximumValues, minimumValues)
                * (b - a)
                / r;
        volume +=
            calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues)
                * (max - b)
                / r;
      } else {
        volume =
            calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues)
                * (max - a)
                / r;
      }
    }

    return volume;
  }

  private void calculateFitness(List<S> solutionList, int pos) {
    double fitness = 0.0;

    for (int i = 0; i < solutionList.size(); i++) {
      if (i != pos) {
        fitness += Math.exp((-1 * indicatorValues.get(i).get(pos) / maxIndicatorValue) / kappa);
      }
    }
    solutionFitness.setAttribute(solutionList.get(pos), fitness);
  }

  /**
   * Returns a comparator based on the indicator-based fitness attribute. Lower fitness values are
   * preferred (minimization).
   *
   * @return Comparator for comparing solutions based on their fitness
   */
  public Comparator<S> getComparator() {
    return (s1, s2) -> {
      Double fitness1 = solutionFitness.getAttribute(s1);
      Double fitness2 = solutionFitness.getAttribute(s2);

      if (fitness1 == null && fitness2 == null) {
        return 0;
      } else if (fitness1 == null) {
        return 1;
      } else if (fitness2 == null) {
        return -1;
      }

      return Double.compare(fitness1, fitness2);
    };
  }

  /**
   * Gets the fitness value of a solution.
   *
   * @param solution Solution to get fitness for
   * @return Fitness value, or null if not computed
   */
  public Double getFitness(S solution) {
    return solutionFitness.getAttribute(solution);
  }

  public double getKappa() {
    return kappa;
  }

  public int getNumberOfObjectives() {
    return numberOfObjectives;
  }

  /**
   * Removes the worst solution from the list and updates fitness values. This method is useful for
   * iterative removal in replacement strategies.
   *
   * @param solutionList List of solutions (will be modified)
   */
  public void removeWorstAndUpdateFitness(List<S> solutionList) {
    // Find the worst (highest fitness)
    double worst = solutionFitness.getAttribute(solutionList.get(0));
    int worstIndex = 0;

    for (int i = 1; i < solutionList.size(); i++) {
      double fitness = solutionFitness.getAttribute(solutionList.get(i));
      if (fitness > worst) {
        worst = fitness;
        worstIndex = i;
      }
    }

    // Update fitness values for remaining solutions
    for (int i = 0; i < solutionList.size(); i++) {
      if (i != worstIndex) {
        double fitness = solutionFitness.getAttribute(solutionList.get(i));
        fitness -= Math.exp((-indicatorValues.get(worstIndex).get(i) / maxIndicatorValue) / kappa);
        solutionFitness.setAttribute(solutionList.get(i), fitness);
      }
    }

    // Remove worst from indicator values
    indicatorValues.remove(worstIndex);
    for (List<Double> indicatorValue : indicatorValues) {
      indicatorValue.remove(worstIndex);
    }

    solutionList.remove(worstIndex);
  }
}
