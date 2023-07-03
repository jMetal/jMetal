package org.uma.jmetal.problem.multiobjective.rwa;


import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem Liao2008 (RWA2) described in the paper "Engineering applications of
 * multi-objective evolutionary algorithms: A test suite of box-constrained real-world
 * problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192
 */
public class Liao2008 extends AbstractDoubleProblem {

  public Liao2008() {
    numberOfObjectives(3);
    name("Liao2008");

    int numberOfVariables = 5;
    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(1.0);
      upperLimit.add(3.0);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double Mass;
    double Ain;
    double Intrusion;
    double t1 = solution.variables().get(0);
    double t2 = solution.variables().get(1);
    double t3 = solution.variables().get(2);
    double t4 = solution.variables().get(3);
    double t5 = solution.variables().get(4);
    Mass = 1640.2823 + 2.3573285 * t1 + 2.3220035 * t2 + 4.5688768 * t3
        + 7.7213633 * t4 + 4.4559504 * t5;
    Ain = 6.5856 + 1.15 * t1 - 1.0427 * t2 + 0.9738 * t3 + 0.8364 * t4
        - 0.3695 * t1 * t4 + 0.0861 * t1 * t5 + 0.3628 * t2 * t4
        - 0.1106 * t1 * t1 - 0.3437 * t3 * t3 + 0.1764 * t4 * t4;
    Intrusion = -0.0551 + 0.0181 * t1 + 0.1024 * t2 + 0.0421 * t3
        - 0.0073 * t1 * t2 + 0.024 * t2 * t3 - 0.0118 * t2 * t4
        - 0.0204 * t3 * t4 - 0.008 * t3 * t5 - 0.0241 * t2 * t2
        + 0.0109 * t4 * t4;

    solution.objectives()[0] = Mass; /* Minimization */
    solution.objectives()[1] = Ain; /* Minimization */
    solution.objectives()[2] = Intrusion; /* Minimization */

    return solution;
  }
}
