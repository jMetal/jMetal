package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

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
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF06");

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

      for (int i = 0; i < numberOfVariables; i++) {
          lower.add(0.0);
          upper.add(1.0);
      }

      setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {

    int numberOfVariables_ = solution.variables().size();
    int numberOfObjectives_ = solution.objectives().length;

    double[] x;
    double[] f = new double[numberOfObjectives_];

      double[] arr = new double[10];
      int count = 0;
      for (int i2 = 0; i2 < numberOfVariables_; i2++) {
          double v = solution.variables().get(i2);
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      x = arr;
    double[] thet = new double[numberOfObjectives_ - 1];
    double g, sub1, sub2;
    // evaluate g,thet
      double sum = 0.0;
      for (int i1 = numberOfObjectives_ - 1; i1 < numberOfVariables_; i1++) {
          double pow = Math.pow(x[i1] - 0.5, 2);
          sum += pow;
      }
      g = sum;
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
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
