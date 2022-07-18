package org.uma.jmetal.problem.multiobjective.lsmop;

import org.jetbrains.annotations.NotNull;

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

    for (var i = getNumberOfObjectives(); i <= getNumberOfVariables(); i++) {
        var aux = (1.0 + Math.cos((double) i / (double) getNumberOfVariables() * Math.PI / 2.0))
          * variables.get(i - 1);
      aux = aux - variables.get(0) * 10;
      variables.set(i - 1, aux);
    }

      var arr = new double[10];
      var count = 0;
      var bound3 = getNumberOfObjectives();
    for (var i3 = 0; i3 < bound3; i3++) {
        var v = 0.0;
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
      var G = arr;

    for (var i = 1; i <= getNumberOfObjectives(); i += 2) {
      for (var j = 1; j <= this.nk; j++) {

        List<Double> x = new ArrayList<>(getNumberOfVariables());
          var bound = len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1);
        for (var k = len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1; k <= bound; k++) {
            var aDouble = variables.get(k - 1);
          x.add(aDouble);
        }
        G[i - 1] += getOddFunction().evaluate(x);
      }
    }

    for (var i = 2; i <= getNumberOfObjectives(); i += 2) {
      for (var j = 1; j <= this.nk; j++) {

        @NotNull List<Double> x = new ArrayList<>(getNumberOfVariables());
          var bound = len.get(i - 1) + getNumberOfObjectives() - 1 + j * subLen.get(i - 1);
        for (var k = len.get(i - 1) + getNumberOfObjectives() - 1 + (j - 1) * subLen.get(i - 1) + 1; k <= bound; k++) {
            var aDouble = variables.get(k - 1);
          x.add(aDouble);
        }

        G[i - 1] += getEvenFunction().evaluate(x);
      }
    }

    for (var i = 0; i < G.length; i++) {
      G[i] = G[i] / this.subLen.get(i) / this.nk;
    }

    List<Double> leftHand = new ArrayList<>(getNumberOfObjectives());
    List<Double> rightHand = new ArrayList<>(getNumberOfObjectives());

    leftHand.add(1.0);
    for (var i = 1; i <= getNumberOfObjectives() - 1; i++) {
      leftHand.add(variables.get(i - 1));
    }

      var cum = 1.0;
    for (var i = 1; i <= getNumberOfObjectives(); i++) {
      cum = cum * leftHand.get(i - 1);
      leftHand.set(i - 1, cum);
    }

    @NotNull List<Double> inverted = new ArrayList<>();
      var bound2 = getNumberOfObjectives();
    for (var i2 = 0; i2 < bound2; i2++) {
        var aDouble2 = leftHand.get(leftHand.size() - i2 - 1);
      inverted.add(aDouble2);
    }

    rightHand.add(1.0);
    for (var i = getNumberOfObjectives() - 1; i >= 1; i--) {
      rightHand.add(1.0 - variables.get(i - 1));
    }

    @NotNull List<Double> operand = new ArrayList<>();
      var bound1 = getNumberOfObjectives();
    for (var i1 = 0; i1 < bound1; i1++) {
      @NotNull Double aDouble1 = inverted.get(i1) * rightHand.get(i1);
      operand.add(aDouble1);
    }

    List<Double> y = new ArrayList<>(getNumberOfObjectives());
      var bound = getNumberOfObjectives();
    for (var i = 0; i < bound; i++) {
      Double aDouble = (1.0 + G[i]) * operand.get(i);
      y.add(aDouble);
    }
    return y;
  }

}
