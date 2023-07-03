package org.uma.jmetal.problem.multiobjective.rwa;


import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem Padhi2016 (RWA4) described in the paper "Engineering applications of
 * multi-objective evolutionary algorithms: A test suite of box-constrained real-world
 * problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192
 */
public class Padhi2016 extends AbstractDoubleProblem {

  public Padhi2016() {
    numberOfObjectives(3);
    name("Padhi2016");

    List<Double> lowerLimit = List.of(1.0, 10.0, 850.0, 20.0, 4.0) ;
    List<Double> upperLimit = List.of(1.4, 26.0, 1650.0, 40.0, 8.0) ;

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);
    double CR, Ra, DD;

    CR = 1.74 + 0.42 * x1 - 0.27 * x2 + 0.087 * x3 - 0.19 * x4 + 0.18 * x5
        + 0.11 * x1 * x1 + 0.036 * x4 * x4 - 0.025 * x5 * x5
        + 0.044 * x1 * x2 + 0.034 * x1 * x4 + 0.17 * x1 * x5
        - 0.028 * x2 * x4 + 0.093 * x3 * x4 - 0.033 * x4 * x5;

    Ra = 2.19 + 0.26 * x1 - 0.088 * x2 + 0.037 * x3 - 0.16 * x4 + 0.069 * x5
        + 0.036 * x1 * x1 + 0.11 * x1 * x3 - 0.077 * x1 * x4
        - 0.075 * x2 * x3 + 0.054 * x2 * x4 + 0.090 * x3 * x5
        + 0.041 * x4 * x5;

    DD = 0.095 + 0.013 * x1 - 8.625 * 1e-003 * x2 - 5.458 * 1e-003 * x3
        - 0.012 * x4 + 1.462 * 1e-003 * x1 * x1 - 6.635 * 1e-004 * x2 * x2
        - 1.788 * 1e-003 * x4 * x4 - 0.011 * x1 * x2
        - 6.188 * 1e-003 * x1 * x3 + 8.937 * 1e-003 * x1 * x4
        - 4.563 * 1e-003 * x1 * x5 - 0.012 * x2 * x3
        - 1.063 * 1e-003 * x2 * x4 + 2.438 * 1e-003 * x2 * x5
        - 1.937 * 1e-003 * x3 * x4 - 1.188 * 1e-003 * x3 * x5
        - 3.312 * 1e-003 * x4 * x5;

    solution.objectives()[0] = -CR; /* Maximization */
    solution.objectives()[1] = Ra; /* Minimization */
    solution.objectives()[2] = DD; /* Minimization */

    return solution ;
  }
}

