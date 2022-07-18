package org.uma.jmetal.problem.multiobjective.lsmop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractLSMOP1_4 extends AbstractLSMOP {

  protected AbstractLSMOP1_4(int nk, int numberOfVariables, int numberOfObjectives) {
    super(nk, numberOfVariables, numberOfObjectives);
  }

  protected List<Double> evaluate(List<Double> variables) {
    double[] G;

    for (int i = getNumberOfObjectives(); i <= getNumberOfVariables(); i++) {
      double aux = (1.0 + Math.cos((double) i / (double) getNumberOfVariables() * Math.PI / 2.0))
          * variables.get(i - 1);
      aux = aux - variables.get(0) * 10;
      variables.set(i - 1, aux);
    }

    double[] arr = new double[10];
    int count = 0;
    int bound3 = getNumberOfObjectives();
    for (int i3 = 0; i3 < bound3; i3++) {
      double v = 0.0;
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v;
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

    for (int i = 0; i < G.length; i++) {
      G[i] = G[i] / this.subLen.get(i) / this.nk;
    }

    List<Double> leftHand = new ArrayList<>(getNumberOfObjectives());
    List<Double> rightHand = new ArrayList<>(getNumberOfObjectives());

    leftHand.add(1.0);
    for (int i = 1; i <= getNumberOfObjectives() - 1; i++) {
      leftHand.add(variables.get(i - 1));
    }

    double cum = 1.0;
    for (int i = 1; i <= getNumberOfObjectives(); i++) {
      cum = cum * leftHand.get(i - 1);
      leftHand.set(i - 1, cum);
    }

    List<Double> inverted = new ArrayList<>();
    int bound2 = getNumberOfObjectives();
    for (int i2 = 0; i2 < bound2; i2++) {
      Double aDouble2 = leftHand.get(leftHand.size() - i2 - 1);
      inverted.add(aDouble2);
    }

    rightHand.add(1.0);
    for (int i = getNumberOfObjectives() - 1; i >= 1; i--) {
      rightHand.add(1.0 - variables.get(i - 1));
    }

    List<Double> operand = new ArrayList<>();
    int bound1 = getNumberOfObjectives();
    for (int i1 = 0; i1 < bound1; i1++) {
      Double aDouble1 = inverted.get(i1) * rightHand.get(i1);
      operand.add(aDouble1);
    }

    List<Double> y = new ArrayList<>(getNumberOfObjectives());
    int bound = getNumberOfObjectives();
    for (int i = 0; i < bound; i++) {
      Double aDouble = (1.0 + G[i]) * operand.get(i);
      y.add(aDouble);
    }
    return y;
  }

}
