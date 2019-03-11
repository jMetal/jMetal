package org.uma.jmetal.solution.doublesolution.impl.util.impl;

import com.sun.tools.doclint.Checker;
import org.uma.jmetal.solution.doublesolution.impl.util.DoubleVariableGenerator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RandomDoubleVariableGenerator extends DoubleVariableGenerator {

  @Override
  public List<Double> generate() {
    if (!configured) {
      throw new JMetalException("The generator is not configured");
    }

    List<Double> vars = new ArrayList<>(numberOfVariables);

    IntStream
        .range(0, numberOfVariables)
        .forEach(i -> vars.add(i, JMetalRandom.getInstance().nextDouble(lowerBounds.get(i), upperBounds.get(i))));

    return vars;
  }
}
