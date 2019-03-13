package org.uma.jmetal.solution.doublesolution.impl.util.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.doublesolution.impl.util.DoubleVariableGenerator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

public class ScatterSearchDoubleVariableGenerator extends DoubleVariableGenerator {
  protected int numberOfSubRanges;
  protected int[] sumOfFrequencyValues;
  protected int[] sumOfReverseFrequencyValues;
  protected int[][] frequency;
  protected int[][] reverseFrequency;

  public ScatterSearchDoubleVariableGenerator() {
    this(4);
  }

  public ScatterSearchDoubleVariableGenerator(int numberOfSubRanges) {
    this.numberOfSubRanges = numberOfSubRanges;
  }

  @Override
  public void configure(List<Pair<Double, Double>> bounds) {
    super.configure(bounds);

    if (!configured) {
      sumOfFrequencyValues = new int[bounds.size()];
      sumOfReverseFrequencyValues = new int[bounds.size()];
      frequency = new int[numberOfSubRanges][bounds.size()];
      reverseFrequency = new int[numberOfSubRanges][bounds.size()];
      configured = true ;
    }
  }

  @Override
  public List<Double> generate() {
    if (!configured) {
      throw new JMetalException("The generator is not configured");
    }

    List<Double> vars = new ArrayList<>(bounds.size());

    double value;
    int range;

    for (int i = 0; i < bounds.size(); i++) {
      sumOfReverseFrequencyValues[i] = 0;
      for (int j = 0; j < numberOfSubRanges; j++) {
        reverseFrequency[j][i] = sumOfFrequencyValues[i] - frequency[j][i];
        sumOfReverseFrequencyValues[i] += reverseFrequency[j][i];
      }

      if (sumOfReverseFrequencyValues[i] == 0) {
        range = JMetalRandom.getInstance().nextInt(0, numberOfSubRanges - 1);
      } else {
        value = JMetalRandom.getInstance().nextInt(0, sumOfReverseFrequencyValues[i] - 1);
        range = 0;
        while (value > reverseFrequency[range][i]) {
          value -= reverseFrequency[range][i];
          range++;
        }
      }

      frequency[range][i]++;
      sumOfFrequencyValues[i]++;

      double low =
          bounds.get(i).getLeft()
              + range * (bounds.get(i).getRight() - bounds.get(i).getLeft()) / numberOfSubRanges;
      double high = low + (bounds.get(i).getRight() - bounds.get(i).getLeft()) / numberOfSubRanges;

      vars.add(i, JMetalRandom.getInstance().nextDouble(low, high));
    }

    return vars;
  }
}
