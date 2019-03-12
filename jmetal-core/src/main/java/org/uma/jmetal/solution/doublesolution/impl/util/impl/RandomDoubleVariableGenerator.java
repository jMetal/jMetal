package org.uma.jmetal.solution.doublesolution.impl.util.impl;

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

    List<Double> vars = new ArrayList<>(bounds.size());

    IntStream
        .range(0, bounds.size())
        .forEach(i -> vars.add(i, JMetalRandom.getInstance().nextDouble(bounds.get(i).getLeft(), bounds.get(i).getRight())));

    return vars;
  }
}
