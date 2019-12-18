package org.uma.jmetal.util.sequencegenerator.impl;

import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntegerPermutationGenerator implements SequenceGenerator<Integer> {
  private List<Integer> sequence;
  private int index;
  private Random random;

  public IntegerPermutationGenerator(int size, Random random) {
    sequence = IntStream.range(0, size).boxed().collect(Collectors.toList());
    Collections.shuffle(sequence, random);

    this.random = random;

    index = 0;
  }

  @Override
  public Integer getValue() {
    return sequence.get(index);
  }

  @Override
  public void generateNext() {
    index++;
    if (index == sequence.size()) {
      Collections.shuffle(sequence, random);
      index = 0;
    }
  }
}
