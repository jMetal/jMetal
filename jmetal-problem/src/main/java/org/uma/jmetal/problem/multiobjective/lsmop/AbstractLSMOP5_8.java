package org.uma.jmetal.problem.multiobjective.lsmop;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLSMOP5_8 extends AbstractLSMOP {

  protected AbstractLSMOP5_8(int nk, int numberOfVariables, int numberOfObjectives) {
    super(nk, numberOfVariables, numberOfObjectives);
  }

  @Override
  protected List<Double> evaluate(List<Double> variables) {
    double[] G = new double[numberOfObjectives()];

    for (int i = numberOfObjectives(); i <= numberOfVariables(); i++) {
      double aux = (1.0 + Math.cos((double) i / (double) numberOfVariables() * Math.PI / 2.0))
          * variables.get(i - 1);
      aux = aux - variables.get(0) * 10;
      variables.set(i - 1, aux);
    }

    for (int i = 0; i < numberOfObjectives(); i++) {
      G[i] = 0.0;
    }

    for (int i = 1; i <= numberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

        List<Double> x = new ArrayList<>(numberOfVariables());
        for (int k = len.get(i - 1) + numberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1;
            k <= len.get(i - 1) + numberOfObjectives() - 1 + j * subLen.get(i - 1);
            k++) {
          x.add(variables.get(k - 1));
        }
        G[i - 1] += getOddFunction().evaluate(x);
      }
    }

    for (int i = 2; i <= numberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

        List<Double> x = new ArrayList<>(numberOfVariables());

        for (int k = len.get(i - 1) + numberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1;
            k <= len.get(i - 1) + numberOfObjectives() - 1 + j * subLen.get(i - 1);
            k++) {
          x.add(variables.get(k - 1));
        }
        G[i - 1] += getEvenFunction().evaluate(x);
      }
    }

    for (int i = 0; i < G.length; i++) {
      G[i] = G[i] / this.subLen.get(i) / this.nk;
    }

    List<Double> leftHand = new ArrayList<>(numberOfObjectives());
    List<Double> rightHand = new ArrayList<>(numberOfObjectives());

    leftHand.add(1.0);
    for (int i = 1; i <= numberOfObjectives() - 1; i++) {
      leftHand.add(Math.cos(variables.get(i - 1) * Math.PI / 2.0));
    }

    double cum = 1.0;
    for (int i = 1; i <= numberOfObjectives(); i++) {
      cum = cum * leftHand.get(i - 1);
      leftHand.set(i - 1, cum);
    }

    List<Double> inverted = new ArrayList<>();
    for (int i = 0; i < numberOfObjectives(); i++) {
      inverted.add(leftHand.get(leftHand.size() - i - 1));
    }

    rightHand.add(1.0);
    for (int i = numberOfObjectives() - 1; i >= 1; i--) {
      rightHand.add(Math.sin(variables.get(i - 1) * Math.PI / 2.0));
    }

    List<Double> operand = new ArrayList<>();
    for (int i = 0; i < numberOfObjectives(); i++) {
      operand.add(inverted.get(i) * rightHand.get(i));
    }

    List<Double> shiftedG = new ArrayList<>();
    for (int i = 2; i <= G.length; i++) {
      shiftedG.add(G[i - 1]);
    }
    shiftedG.add(0.0);

    List<Double> y = new ArrayList<>(numberOfObjectives());
    for (int i = 0; i < numberOfObjectives(); i++) {
      y.add((1.0 + G[i] + shiftedG.get(i)) * operand.get(i));
    }
    return y;
  }
}
