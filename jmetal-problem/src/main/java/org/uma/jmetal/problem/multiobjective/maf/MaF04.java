package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem MaF04
 */
@SuppressWarnings("serial")
public class MaF04 extends AbstractDoubleProblem {
  public static double const4[];

  /**
   * Default constructor
   */
  public MaF04() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF04 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF04(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF04") ;

    List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(
        getNumberOfVariables());

    for (int var = 0; var < numberOfVariables; var++) {
      lower.add(0.0);
      upper.add(1.0);
    }

    setLowerLimit(lower);
    setUpperLimit(upper);

    //other constants during the whole process once M&D are defined
    double[] c4 = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      c4[i] = Math.pow(2, i + 1);
    }
    const4 = c4;
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public void evaluate(DoubleSolution solution) {

    int numberOfVariables = solution.getNumberOfVariables();
    int numberOfObjectives = solution.getNumberOfObjectives();

    double[] x = new double[numberOfVariables];
    double[] f = new double[numberOfObjectives];

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i);
    }

    double g = 0;
    // evaluate g
    for (int i = numberOfObjectives - 1; i < numberOfVariables; i++) {
      g += (Math.pow(x[i] - 0.5, 2) - Math.cos(20 * Math.PI * (x[i] - 0.5)));
    }
    g = 100 * (numberOfVariables - numberOfObjectives + 1 + g);
    double subf1 = 1, subf3 = 1 + g;
    // evaluate fm,fm-1,...2,f1
    f[numberOfObjectives - 1] =
        const4[numberOfObjectives - 1] * (1 - Math.sin(Math.PI * x[0] / 2)) * subf3;
    // fi=2^i*(1-subf1*subf2)*(subf3)
    for (int i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= Math.cos(Math.PI * x[numberOfObjectives - i - 2] / 2);
      f[i] =
          const4[i] * (1 - subf1 * Math.sin(Math.PI * x[numberOfObjectives - i - 1] / 2)) * subf3;
    }
    f[0] = const4[0] * (1 - subf1 * Math.cos(Math.PI * x[numberOfObjectives - 2] / 2)) * subf3;

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }

  }
}
