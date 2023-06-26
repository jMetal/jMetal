package org.uma.jmetal.problem.multiobjective.rwa;


import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class Xu2020 extends AbstractDoubleProblem {

  public Xu2020() {
    numberOfObjectives(3);
    name("Xu2020");

    List<Double> lowerLimit = List.of(12.56, 0.02, 1.0, 0.5);
    List<Double> upperLimit = List.of(25.12, 0.06, 5.0, 2.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double vc = solution.variables().get(0);
    double fz = solution.variables().get(1);
    double ap = solution.variables().get(2);
    double ae = solution.variables().get(3);

    double Ft, Ra, MRR;
    double d = 2.5;
    double z = 1.0;

    Ft = -54.3 - 1.18 * vc - 2429 * fz + 104.2 * ap + 129.0 * ae
        - 18.9 * vc * fz - 0.209 * vc * ap - 0.673 * vc * ae + 265 * fz * ap
        + 1209 * fz * ae + 22.76 * ap * ae + 0.066 * vc * vc
        + 32117 * fz * fz - 16.98 * ap * ap - 47.6 * ae * ae;

    Ra = 0.227 - 0.0072 * vc + 1.89 * fz - 0.0203 * ap + 0.3075 * ae
        - 0.198 * vc * fz - 0.000955 * vc * ap - 0.00656 * vc * ae
        + 0.209 * fz * ap + 0.783 * fz * ae + 0.02275 * ap * ae
        + 0.000355 * vc * vc + 35 * fz * fz + 0.00037 * ap * ap
        - 0.0791 * ae * ae;

    MRR = (1000.0 * vc * fz * z * ap * ae) / (Math.PI * d);

    solution.objectives()[0] = Ft; // Minimization
    solution.objectives()[1] = Ra; // Minimization
    solution.objectives()[2] = -MRR; // Maximization

    return solution ;
  }
}

