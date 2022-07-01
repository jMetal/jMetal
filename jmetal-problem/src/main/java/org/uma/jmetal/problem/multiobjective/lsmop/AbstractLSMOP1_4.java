package org.uma.jmetal.problem.multiobjective.lsmop;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLSMOP1_4 extends AbstractLSMOP {

  protected AbstractLSMOP1_4(int nk, int numberOfVariables, int numberOfObjectives) {
    super(nk, numberOfVariables, numberOfObjectives);
  }

  protected List<Double> evaluate(List<Double> variables) {
    double[] G = new double[getNumberOfObjectives()];

    for (int i = getNumberOfObjectives(); i <= getNumberOfVariables(); i++) {
      double aux = (1.0 + Math.cos((double) i / (double) getNumberOfVariables() * Math.PI / 2.0))
          * variables.get(i - 1);
      aux = aux - variables.get(0) * 10;
      variables.set(i - 1, aux);
    }

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      G[i] = 0.0;
    }

    for (int i = 1; i <= getNumberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

        List<Double> x = new ArrayList<>(getNumberOfVariables());
        for (int k = len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1;
            k <= len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1);
            k++) {
          x.add(variables.get(k - 1));
        }
        G[i - 1] += getOddFunction().evaluate(x);
      }
    }

    for (int i = 2; i <= getNumberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

        List<Double> x = new ArrayList<>(getNumberOfVariables());

        for (int k = len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1;
            k <= len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1);
            k++) {
          x.add(variables.get(k - 1));
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
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      inverted.add(leftHand.get(leftHand.size() - i - 1));
    }

    rightHand.add(1.0);
    for (int i = getNumberOfObjectives() - 1; i >= 1; i--) {
      rightHand.add(1.0 - variables.get(i - 1));
    }

    List<Double> operand = new ArrayList<>();
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      operand.add(inverted.get(i) * rightHand.get(i));
    }

    List<Double> y = new ArrayList<>(getNumberOfObjectives());
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      y.add((1.0 + G[i]) * operand.get(i));
    }
    return y;
  }

}
