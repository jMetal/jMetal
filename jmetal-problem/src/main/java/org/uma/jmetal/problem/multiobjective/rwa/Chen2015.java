package org.uma.jmetal.problem.multiobjective.rwa;


import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem Chen2015 (RWA9) described in the paper "Engineering applications of
 * multi-objective evolutionary algorithms: A test suite of box-constrained real-world
 * problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192
 */

public class Chen2015 extends AbstractDoubleProblem {

  public Chen2015() {
    numberOfObjectives(5);
    name("Chen2015");

    List<Double> lowerLimit = List.of(17.5, 17.5, 2.0, 2.0, 5.0, 5.0);
    List<Double> upperLimit = List.of(22.5, 22.5, 3.0, 3.0, 7.0, 6.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double l1 = solution.variables().get(0);
    double w1 = solution.variables().get(1);
    double l2 = solution.variables().get(2);
    double w2 = solution.variables().get(3);
    double a1 = solution.variables().get(4);
    double b1 = solution.variables().get(5);
    double a2 = l1 * w1 * l2 * w2;
    double b2 = l1 * w1 * l2 * a1;
    double d2 = w1 * w2 * a1 * b1;

    double F1, F2, F3, F4, F5;

    // F1 (minimization)
    F1 = 502.94 - 27.18 * ((w1 - 20.0) / 0.5) + 43.08 * ((l1 - 20.0) / 2.5)
        + 47.75 * (a1 - 6.0) + 32.25 * ((b1 - 5.5) / 0.5)
        + 31.67 * (a2 - 11.0)
        - 36.19 * ((w1 - 20.0) / 0.5) * ((w2 - 2.5) / 0.5)
        - 39.44 * ((w1 - 20.0) / 0.5) * (a1 - 6.0)
        + 57.45 * (a1 - 6.0) * ((b1 - 5.5) / 0.5);

    // F2 (maximization)
    F2 = (130.53 + 45.97 * ((l1 - 20.0) / 2.5) - 52.93 * ((w1 - 20.0) / 0.5)
        - 78.93 * (a1 - 6.0) + 79.22 * (a2 - 11.0)
        + 47.23 * ((w1 - 20.0) / 0.5) * (a1 - 6.0)
        - 40.61 * ((w1 - 20.0) / 0.5) * (a2 - 11.0)
        - 50.62 * (a1 - 6.0) * (a2 - 11.0));

    // F3 (maximization)
    F3 = (203.16 - 42.75 * ((w1 - 20.0) / 0.5) + 56.67 * (a1 - 6.0)
        + 19.88 * ((b1 - 5.5) / 0.5) - 12.89 * (a2 - 11.0)
        - 35.09 * (a1 - 6.0) * ((b1 - 5.5) / 0.5)
        - 22.91 * ((b1 - 5.5) / 0.5) * (a2 - 11.0));

    // F4 (maximization)
    F4 = (0.76 - 0.06 * ((l1 - 20.0) / 2.5) + 0.03 * ((l2 - 2.5) / 0.5)
        + 0.02 * (a2 - 11.0) - 0.02 * ((b2 - 6.5) / 0.5)
        - 0.03 * ((d2 - 12.0) / 0.5)
        + 0.03 * ((l1 - 20.0) / 2.5) * ((w1 - 20.0) / 0.5)
        - 0.02 * ((l1 - 20.0) / 2.5) * ((l2 - 2.5) / 0.5)
        + 0.02 * ((l1 - 20.0) / 2.5) * ((b2 - 6.5) / 0.5));

    // F5 (minimization)
    F5 = 1.08 - 0.12 * ((l1 - 20.0) / 2.5) - 0.26 * ((w1 - 20.0) / 0.5)
        - 0.05 * (a2 - 11.0) - 0.12 * ((b2 - 6.5) / 0.5)
        + 0.08 * (a1 - 6.0) * ((b2 - 6.5) / 0.5)
        + 0.07 * (a2 - 6.0) * ((b2 - 5.5) / 0.5);

    solution.objectives()[0] = F1; // minimization
    solution.objectives()[1] = -F2; // maximization
    solution.objectives()[2] = -F3; // maximization
    solution.objectives()[3] = -F4; // maximization
    solution.objectives()[4] = F5; // minimization

    return solution ;
  }
}
