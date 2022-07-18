package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF04
 */
@SuppressWarnings("serial")
public class MaF04 extends AbstractDoubleProblem {
  public double const4[];

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
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF04") ;

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

    IntStream.range(0, numberOfVariables).forEach(i -> {
      lower.add(0.0);
      upper.add(1.0);
    });

    setVariableBounds(lower, upper);

    //other constants during the whole process once M&D are defined
    double[] c4 = IntStream.range(0, numberOfObjectives).mapToDouble(i -> Math.pow(2, i + 1)).toArray();
      const4 = c4;
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {

    int numberOfVariables = solution.variables().size();
    int numberOfObjectives = solution.objectives().length;

    double[] x;
    double[] f = new double[numberOfObjectives];

      x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

    double g = IntStream.range(numberOfObjectives - 1, numberOfVariables).mapToDouble(i -> (Math.pow(x[i] - 0.5, 2) - Math.cos(20 * Math.PI * (x[i] - 0.5)))).sum();
    // evaluate g
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
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
