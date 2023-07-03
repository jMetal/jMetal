package org.uma.jmetal.problem.multiobjective.rwa;

import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem Subasi2016 (RWA1) described in the paper "Engineering applications of
 * multi-objective evolutionary algorithms: A test suite of box-constrained real-world
 * problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192
 */
public class Subasi2016 extends AbstractDoubleProblem {
  public Subasi2016() {
    numberOfObjectives(2);
    List<Double> lowerLimit = List.of(20.0, 6.0, 20.0, 0.0, 8000.0) ;
    List<Double> upperLimit = List.of(60.0, 15.0, 40.0, 30.0, 25000.0) ;
    name("Subasi2016");

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double H = solution.variables().get(0);
    double t = solution.variables().get(1);
    double Sy = solution.variables().get(2);
    double theta = solution.variables().get(3);
    double Re = solution.variables().get(4);

    double f;
    double Nu;

    Nu = 89.027 + 0.300 * H - 0.096 * t - 1.124 * Sy - 0.968 * theta
        + 4.148 * 10e-3 * Re + 0.0464 * H * t - 0.0244 * H * Sy
        + 0.0159 * H * theta + 4.151 * 10e-5 * H * Re + 0.1111 * t * Sy
        - 4.121 * 10e-5 * Sy * Re + 4.192 * 10e-5 * theta * Re;

    f = 0.4753 - 0.0181 * H + 0.0420 * t + 5.481 * 10e-3 * Sy - 0.0191 * theta
        - 3.416 * 10e-6 * Re - 8.851 * 10e-4 * H * Sy
        + 8.702 * 10e-4 * H * theta + 1.536 * 10e-3 * t * theta
        - 2.761 * 10e-6 * t * Re - 4.400 * 10e-4 * Sy * theta
        + 9.714 * 10e-7 * Sy * Re + 6.777 * 10e-4 * H * H;

    solution.objectives()[0] = -Nu;
    solution.objectives()[1] = f;

    return solution ;
  }
}

