package org.uma.jmetal.problem.multiobjective.rwa;


import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem Ganesan2013 (RWA3) described in the paper "Engineering applications of
 * multi-objective evolutionary algorithms: A test suite of box-constrained real-world
 * problems". DOI: https://doi.org/10.1016/j.engappai.2023.106192
 */

public class Ganesan2013 extends AbstractDoubleProblem {

  public Ganesan2013() {
    numberOfObjectives(3);
    name("Ganesan2013") ;

    List<Double> lowerLimit = List.of(0.25, 10000.0, 600.0) ;
    List<Double> upperLimit = List.of(0.55, 20000.0, 1100.0) ;

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double O2CH4 = solution.variables().get(0);
    double GV = solution.variables().get(1);
    double T = solution.variables().get(2);

    double HC4_conversion, CO_selectivity, H2_CO_ratio;

    HC4_conversion = (-8.87e-6)
        * (86.74 + 14.6 * O2CH4 - 3.06 * GV + 18.82 * T + 3.14 * O2CH4 * GV
        - 6.91 * O2CH4 * O2CH4 - 13.31 * T * T);

    CO_selectivity = (-2.152e-9)
        * (39.46 + 5.98 * O2CH4 - 2.4 * GV + 13.06 * T + 2.5 * O2CH4 * GV
        + 1.64 * GV * T - 3.9 * O2CH4 * O2CH4 - 10.15 * T * T
        - 3.69 * GV * GV * O2CH4) + 45.7;

    H2_CO_ratio = (4.425e-10)
        * (1.29 - 0.45 * T - 0.112 * O2CH4 * GV - 0.142 * T * GV
        + 0.109 * O2CH4 * O2CH4 + 0.405 * T * T
        + 0.167 * T * T * GV) + 0.18;

    solution.objectives()[0] = -HC4_conversion; /* maximization */
    solution.objectives()[1] = -CO_selectivity; /* maximization */
    solution.objectives()[2] = H2_CO_ratio; /* minimization */

    return solution ;
  }
}

