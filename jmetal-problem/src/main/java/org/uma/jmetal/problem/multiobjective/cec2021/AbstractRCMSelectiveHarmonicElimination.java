package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Base class for the RWMOP "SOPWM" (Selective Harmonic Elimination for multilevel inverters)
 * problems RCM30-RCM35. They all share the same objective/constraint structure, differing only in
 * the number of switching angles (decision variables), the modulation index and the harmonic sign
 * pattern.
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
abstract class AbstractRCMSelectiveHarmonicElimination extends AbstractDoubleProblem {
  private static final int[] HARMONIC_ORDERS = {
      5, 7, 11, 13, 17, 19, 23, 25, 29, 31, 35, 37, 41, 43, 47, 49, 53, 55, 59, 61, 65, 67, 71, 73,
      77, 79, 83, 85, 91, 95, 97};

  private final double modulationIndex;
  private final int[] signPattern;

  protected AbstractRCMSelectiveHarmonicElimination(
      int numberOfVariables, double modulationIndex, int[] signPattern, String name) {
    numberOfObjectives(2);
    numberOfConstraints(numberOfVariables - 1);
    name(name);

    this.modulationIndex = modulationIndex;
    this.signPattern = signPattern;

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);
    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(90.0);
    }
    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int d = numberOfVariables();
    double[] angleRad = new double[d];
    double sumCos = 0;
    for (int l = 0; l < d; l++) {
      angleRad[l] = solution.variables().get(l) * Math.PI / 180.0;
      sumCos += signPattern[l] * Math.cos(angleRad[l]);
    }

    double su = 0;
    double sumInverseK4 = 0;
    for (int k : HARMONIC_ORDERS) {
      double su2 = 0;
      for (int l = 0; l < d; l++) {
        su2 += signPattern[l] * Math.cos(k * angleRad[l]);
      }
      su += su2 * su2 / Math.pow(k, 4);
      sumInverseK4 += 1.0 / Math.pow(k, 4);
    }

    solution.objectives()[0] = Math.sqrt(su) / Math.sqrt(sumInverseK4);
    solution.objectives()[1] = Math.pow(sumCos - modulationIndex, 2);

    for (int i = 0; i < d - 1; i++) {
      double xi = solution.variables().get(i);
      double xi1 = solution.variables().get(i + 1);
      double g = xi - xi1 + 1e-6;
      solution.constraints()[i] = -g;
    }

    return solution;
  }
}
