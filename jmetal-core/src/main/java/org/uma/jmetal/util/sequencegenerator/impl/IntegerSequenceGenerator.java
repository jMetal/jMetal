package org.uma.jmetal.util.sequencegenerator.impl;

import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerSequenceGenerator implements SequenceGenerator<Integer> {
  private List<Integer> sequence;
  private int index;

  public IntegerSequenceGenerator(int size) {
    sequence = IntStream.range(0, size).boxed().collect(Collectors.toList());
    index = 0;
  }

  @Override
  public Integer getValue() {
    return sequence.get(index) ;
  }

  @Override
  public void generateNext() {
    index++;
    if (index == sequence.size()) {
      index = 0;
    }
  }
}
