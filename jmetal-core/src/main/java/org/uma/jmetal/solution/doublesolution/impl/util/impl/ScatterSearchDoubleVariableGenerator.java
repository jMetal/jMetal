package org.uma.jmetal.solution.doublesolution.impl.util.impl;

import com.sun.tools.doclint.Checker;
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
    this(4) ;
  }

  public ScatterSearchDoubleVariableGenerator(int numberOfSubRanges) {
    this.numberOfSubRanges = numberOfSubRanges ;
  }

  @Override
  public void configure(int numberOfVariables, List<Double> lowerBounds, List<Double> upperBounds) {
    super.configure(numberOfVariables, lowerBounds, upperBounds) ;

    sumOfFrequencyValues = new int[numberOfVariables] ;
    sumOfReverseFrequencyValues = new int[numberOfVariables] ;
    frequency = new int[numberOfSubRanges][numberOfVariables] ;
    reverseFrequency = new int[numberOfSubRanges][numberOfVariables] ;
  }

  @Override
  public List<Double> generate() {
    if (!configured) {
      throw new JMetalException("The generator is not configured");
    }

    List<Double> vars = new ArrayList<>(numberOfVariables);

    double value;
    int range;

    for (int i = 0; i < numberOfVariables; i++) {
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

      double low = lowerBounds.get(i) + range * (upperBounds.get(i) - lowerBounds.get(i)) / numberOfSubRanges;
      double high = low + (upperBounds.get(i) - lowerBounds.get(i))/ numberOfSubRanges;

      vars.add(i, JMetalRandom.getInstance().nextDouble(low, high)) ;
    }

    return vars;
  }
}
