package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing problem DENT, a bi-objective problem with 2 variables. Variables are bounded
 * in [-2.0, 2.0]
 */
public class Dent extends AbstractDoubleProblem {
  public Dent() {
    numberOfObjectives(2);
    numberOfConstraints(0);
    name("DENT");

    List<Double> lowerLimit = Arrays.asList(-2.0, -2.0);
    List<Double> upperLimit = Arrays.asList(2.0, 2.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    double term1 = Math.sqrt(1.0 + x1 * x1);
    double term2 = Math.sqrt(1.0 + x2 * x2);
    double expTerm = 0.85 * Math.exp(-Math.pow(x1 - x2, 2));

    double f1 = 0.5 * (term1 + term2 + x1 - x2) + expTerm;
    double f2 = 0.5 * (term1 + term2 - x1 + x2) + expTerm;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    return solution;
  }
}
