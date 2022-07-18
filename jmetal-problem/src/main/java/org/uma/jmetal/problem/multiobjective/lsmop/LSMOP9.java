package org.uma.jmetal.problem.multiobjective.lsmop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Ackley;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Sphere;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class LSMOP9 extends AbstractLSMOP {

  /**
   * Creates a default LSMOP9 problem (7 variables and 3 objectives)
   */
  public LSMOP9() {
    this(5, 300, 3);
  }

  /**
   * Creates a LSMOP6 problem instance
   *
   * @param nk                 Number of subcomponents in each variable group
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */


  public LSMOP9(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
    super(nk, numberOfVariables, numberOfObjectives);
    setName("LSMOP9");
  }


  @Override
  protected Function getOddFunction() {
    return new Sphere();
  }

  @Override
  protected Function getEvenFunction() {
    return new Ackley();
  }

  @Override
  protected List<Double> evaluate(@NotNull List<Double> variables) {
    double[] G;

    for (int i = getNumberOfObjectives(); i <= getNumberOfVariables(); i++) {
      double aux = (1.0 + Math.cos((double) i / (double) getNumberOfVariables() * Math.PI / 2.0))
          * variables.get(i - 1);
      aux = aux - variables.get(0) * 10;
      variables.set(i - 1, aux);
    }

      double[] arr = new double[10];
      int count = 0;
      int bound1 = getNumberOfObjectives();
      for (int i2 = 0; i2 < bound1; i2++) {
          double v2 = 0.0;
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v2;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      G = arr;

    for (int i = 1; i <= getNumberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

          List<Double> x = new ArrayList<>(getNumberOfVariables());
          int bound = len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1);
          for (int k = len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1; k <= bound; k++) {
              Double aDouble = variables.get(k - 1);
              x.add(aDouble);
          }
          G[i - 1] += getOddFunction().evaluate(x);
      }
    }

    for (int i = 2; i <= getNumberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

          List<Double> x = new ArrayList<>(getNumberOfVariables());
          int bound = len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1);
          for (int k = len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1; k <= bound; k++) {
              Double aDouble = variables.get(k - 1);
              x.add(aDouble);
          }

          G[i - 1] += getEvenFunction().evaluate(x);
      }
    }

      double cofficientG = 0.0;
      for (double v : G) {
          double v1 = (v / this.nk);
          cofficientG += v1;
      }
      cofficientG = 1 + cofficientG;

      List<Double> y = new ArrayList<>(getNumberOfObjectives());
      int bound = getNumberOfObjectives() - 1;
      for (int i1 = 0; i1 < bound; i1++) {
          Double aDouble = variables.get(i1);
          y.add(aDouble);
      }

      double sum = 0.0;
    for (int i = 1; i <= getNumberOfObjectives() - 1; i++) {
      sum += y.get(i - 1) / (1.0 + cofficientG) * (1.0 + Math.sin(3.0 * Math.PI * y.get(i - 1)));
    }

    y.add((1.0 + cofficientG) * (getNumberOfObjectives() - sum));
    return y;
  }
}
