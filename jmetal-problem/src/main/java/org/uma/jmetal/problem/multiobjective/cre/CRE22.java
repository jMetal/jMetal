package org.uma.jmetal.problem.multiobjective.cre;

import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CRE22. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE22 extends AbstractDoubleProblem {
  private double P = 6000;
  private double L = 14;
  private double E = 30 * 1e6;

  /** Constructor */
  public CRE22() {
    setNumberOfObjectives(2);
    setNumberOfConstraints(4);
    setName("CRE22");

    List<Double> lowerLimit = List.of(0.125, 0.1, 0.1, 0.125);
    List<Double> upperLimit = List.of(5.0, 10.0, 10.0, 5.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    solution.objectives()[0] = (1.10471 * x1 * x1 * x2) + (0.04811 * x3 * x4) * (14.0 + x2);
    solution.objectives()[1] = (4 * P * L * L * L) / (E * x4 * x3 * x3 * x3);

    evaluateConstraints(solution);

    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];
    double x1, x2, x3, x4;

    x1 = solution.variables().get(0);
    x2 = solution.variables().get(1);
    x3 = solution.variables().get(2);
    x4 = solution.variables().get(3);

    double G = 12 * 1e6;
    double tauMax = 13600;
    double sigmaMax = 30000;

    double M = P * (L + (x2 / 2));
    double tmpVar = ((x2 * x2) / 4.0) + Math.pow((x1 + x3) / 2.0, 2);
    double R = Math.sqrt(tmpVar);
    tmpVar = ((x2 * x2) / 12.0) + Math.pow((x1 + x3) / 2.0, 2);
    double J = 2 * Math.sqrt(2) * x1 * x2 * tmpVar;

    double tauDashDash = (M * R) / J;
    double tauDash = P / (Math.sqrt(2) * x1 * x2);
    tmpVar = tauDash * tauDash + ((2 * tauDash * tauDashDash * x2) / (2 * R)) + (tauDashDash * tauDashDash);
    double tau = Math.sqrt(tmpVar);
    double sigma = (6 * P * L) / (x4 * x3 * x3);
    tmpVar = 4.013 * E * Math.sqrt((x3 * x3 * x4 * x4 * x4 * x4 * x4 * x4) / 36.0) / (L * L);
    double tmpVar2 = (x3 / (2 * L)) * Math.sqrt(E / (4 * G));
    double PC = tmpVar * (1 - tmpVar2);

    constraint[0] = tauMax - tau;
    constraint[1] = sigmaMax - sigma;
    constraint[2] = x4 - x1;
    constraint[3] = PC - P;

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        constraint[i] = -constraint[i];
      } else {
        constraint[i] = 0;
      }
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
