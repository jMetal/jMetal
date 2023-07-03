package org.uma.jmetal.problem.multiobjective.rwa;


import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem Ahmad2017 (RWA10) described in the paper "Engineering applications of
 * multi-objective evolutionary algorithms: A test suite of box-constrained real-world
 * problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192
 */

public class Ahmad2017 extends AbstractDoubleProblem {

  public Ahmad2017() {
    numberOfObjectives(7);
    name("Ahmad2017");

    List<Double> lowerLimit = List.of(10.0, 10.0, 150.0);
    List<Double> upperLimit = List.of(50.0, 50.0, 170.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double X1 = solution.variables().get(0) ;
    double X2 = solution.variables().get(1) ;
    double X3 = solution.variables().get(2) ;

    double WCA;
    double OCA;
    double AP;
    double CRA;
    double Stiffness;
    double Tear;
    double Tensile;

    // maximization
    WCA = -1331.04 + 1.99 * X1 + 0.33 * X2 + 17.12 * X3 - 0.02 * X1 * X1
        - 0.05 * X3 * X3 - 15.33;

    // maximization
    OCA = -4231.14 + 4.27 * X1 + 1.50 * X2 + 52.30 * X3 - 0.04 * X1 * X2
        - 0.04 * X1 * X1 - 0.16 * X3 * X3 - 29.33;

    // maximization
    AP = 1766.80 - 32.32 * X1 - 24.56 * X2 - 10.48 * X3 + 0.24 * X1 * X3
        + 0.19 * X2 * X3 - 0.06 * X1 * X1 - 0.10 * X2 * X2 - 413.33;

    // maximization
    CRA = -2342.13 - 1.556 * X1 + 0.77 * X2 + 31.14 * X3 + 0.03 * X1 * X1
        - 0.10 * X3 * X3 - 73.33;

    // minimization
    Stiffness = 9.34 + 0.02 * X1 - 0.03 * X2 - 0.03 * X3 - 0.001 * X1 * X2
        + 0.0009 * X2 * X2 + 0.22;

    // maximization
    Tear = 1954.71 + 14.246 * X1 + 5.00 * X2 - 4.30 * X3 - 0.22 * X1 * X1
        - 0.33 * X2 * X2 - 8413.33;

    // maximization
    Tensile = 828.16 + 3.55 * X1 + 73.65 * X2 + 10.80 * X3 - 0.56 * X2 * X3
        + 0.20 * X2 * X2 - 2814.83;

    solution.objectives()[0] = -WCA; // maximization
    solution.objectives()[1] = -OCA; // maximization
    solution.objectives()[2] = -AP; // maximization
    solution.objectives()[3] = -CRA; // maximization
    solution.objectives()[4] = Stiffness; // minimization
    solution.objectives()[5] = -Tear; // maximization
    solution.objectives()[6] = -Tensile; // maximization

    return solution ;
  }
}
