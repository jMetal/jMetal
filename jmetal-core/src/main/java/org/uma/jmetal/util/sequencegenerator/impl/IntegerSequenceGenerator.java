package org.uma.jmetal.util.sequencegenerator.impl;

import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerSequenceGenerator implements SequenceGenerator<Integer> {
  private int index;
  private int size ;

  public IntegerSequenceGenerator(int size) {
    Check.that(size > 0, "Size " + size + " is not a positive number greater than zero");
    this.size = size ;
    index = 0;
  }

  @Override
  public Integer getValue() {
    return index ;
  }

  @Override
  public void generateNext() {
    index++;
    if (index == size) {
      index = 0;
    }
  }
}
