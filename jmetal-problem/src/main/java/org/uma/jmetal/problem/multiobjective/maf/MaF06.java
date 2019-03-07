package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem MaF06
 */
@SuppressWarnings("serial")
public class MaF06 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public MaF06() {
    this(12, 3);
  }

  /**
   * Creates a MaF06 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF06(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF06");

    List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(
        getNumberOfVariables());

    for (int var = 0; var < numberOfVariables; var++) {
      lower.add(0.0);
      upper.add(1.0);
    }

    setLowerLimit(lower);
    setUpperLimit(upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public void evaluate(DoubleSolution solution) {

    int numberOfVariables_ = solution.getNumberOfVariables();
    int numberOfObjectives_ = solution.getNumberOfObjectives();

    double[] x = new double[numberOfVariables_];
    double[] f = new double[numberOfObjectives_];

    for (int i = 0; i < numberOfVariables_; i++) {
      x[i] = solution.getVariableValue(i);
    }
    double[] thet = new double[numberOfObjectives_ - 1];
    double g = 0, sub1, sub2;
    // evaluate g,thet
    for (int i = numberOfObjectives_ - 1; i < numberOfVariables_; i++) {
      g += Math.pow(x[i] - 0.5, 2);
    }
    sub1 = 100 * g + 1;
    sub2 = 1 + g;
    for (int i = 0; i < 1; i++) {
      thet[i] = Math.PI * x[i] / 2;
    }
    for (int i = 1; i < numberOfObjectives_ - 1; i++) {
      thet[i] = Math.PI * (1 + 2 * g * x[i]) / (4 * sub2);
    }
    // evaluate fm,fm-1,...,2,f1
    f[numberOfObjectives_ - 1] = Math.sin(thet[0]) * sub1;
    double subf1 = 1;
    // fi=cos(thet1)cos(thet2)...cos(thet[m-i])*sin(thet(m-i+1))*(1+g[i]),fi=subf1*subf2*subf3
    for (int i = numberOfObjectives_ - 2; i > 0; i--) {
      subf1 *= Math.cos(thet[numberOfObjectives_ - i - 2]);
      f[i] = subf1 * Math.sin(thet[numberOfObjectives_ - i - 1]) * sub1;
    }
    f[0] = subf1 * Math.cos(thet[numberOfObjectives_ - 2]) * sub1;

    for (int i = 0; i < numberOfObjectives_; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}
