package org.uma.jmetal.solution.integersolution.impl.util.impl;

import org.uma.jmetal.solution.doublesolution.impl.util.DoubleVariableGenerator;
import org.uma.jmetal.solution.integersolution.impl.util.IntegerVariableGenerator;
import org.uma.jmetal.solution.util.VariableGenerator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RandomIntegerVariableGenerator extends IntegerVariableGenerator {

  @Override
  public List<Integer> generate() {
    if (!configured) {
      throw new JMetalException("The generator is not configured");
    }

    List<Integer> vars = new ArrayList<>(numberOfVariables);

    IntStream
        .range(0, numberOfVariables)
        .forEach(i -> vars.add(i, JMetalRandom.getInstance().nextInt(lowerBounds.get(i), upperBounds.get(i))));

    return vars;
  }
}
