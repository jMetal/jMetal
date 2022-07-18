package org.uma.jmetal.problem.multiobjective.lsmop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractLSMOP5_8 extends AbstractLSMOP {

  protected AbstractLSMOP5_8(int nk, int numberOfVariables, int numberOfObjectives) {
    super(nk, numberOfVariables, numberOfObjectives);
  }

  @Override
  protected List<Double> evaluate(List<Double> variables) {
    double[] G;

    for (int i = getNumberOfObjectives(); i <= getNumberOfVariables(); i++) {
      double aux = (1.0 + Math.cos((double) i / (double) getNumberOfVariables() * Math.PI / 2.0))
          * variables.get(i - 1);
      aux = aux - variables.get(0) * 10;
      variables.set(i - 1, aux);
    }

      G = IntStream.range(0, getNumberOfObjectives()).mapToDouble(i -> 0.0).toArray();

    for (int i = 1; i <= getNumberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

        List<Double> x = IntStream.rangeClosed(len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1, len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1)).mapToObj(k -> variables.get(k - 1)).collect(Collectors.toCollection(() -> new ArrayList<>(getNumberOfVariables())));
          G[i - 1] += getOddFunction().evaluate(x);
      }
    }

    for (int i = 2; i <= getNumberOfObjectives(); i += 2) {
      for (int j = 1; j <= this.nk; j++) {

        List<Double> x = IntStream.rangeClosed(len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1, len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1)).mapToObj(k -> variables.get(k - 1)).collect(Collectors.toCollection(() -> new ArrayList<>(getNumberOfVariables())));

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
      leftHand.add(Math.cos(variables.get(i - 1) * Math.PI / 2.0));
    }

    double cum = 1.0;
    for (int i = 1; i <= getNumberOfObjectives(); i++) {
      cum = cum * leftHand.get(i - 1);
      leftHand.set(i - 1, cum);
    }

    List<Double> inverted = IntStream.range(0, getNumberOfObjectives()).mapToObj(i -> leftHand.get(leftHand.size() - i - 1)).collect(Collectors.toList());

      rightHand.add(1.0);
    for (int i = getNumberOfObjectives() - 1; i >= 1; i--) {
      rightHand.add(Math.sin(variables.get(i - 1) * Math.PI / 2.0));
    }

    List<Double> operand = IntStream.range(0, getNumberOfObjectives()).mapToObj(i -> inverted.get(i) * rightHand.get(i)).collect(Collectors.toList());

      List<Double> shiftedG = IntStream.rangeClosed(2, G.length).mapToObj(i -> G[i - 1]).collect(Collectors.toList());
      shiftedG.add(0.0);

    List<Double> y = IntStream.range(0, getNumberOfObjectives()).mapToObj(i -> (1.0 + G[i] + shiftedG.get(i)) * operand.get(i)).collect(Collectors.toCollection(() -> new ArrayList<>(getNumberOfObjectives())));
      return y;
  }
}
