package org.uma.jmetal.util.sequencegenerator.impl;

import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

public class IntegerPermutationGenerator implements SequenceGenerator<Integer> {
  private int[] sequence;
  private int index;
  private int size ;

  public IntegerPermutationGenerator(int size) {
    Check.that(size > 0, "Size " + size + " is not a positive number greater than zero");
    this.size = size ;
    sequence = randomPermutation(size) ;

    index = 0;
  }

  @Override
  public Integer getValue() {
    return sequence[index];
  }

  @Override
  public void generateNext() {
    index++;
    if (index == sequence.length) {
      sequence = randomPermutation(size) ;
      index = 0;
    }
  }

  private int[] randomPermutation(int size) {
    int[] permutation = new int[size] ;
    JMetalRandom randomGenerator = JMetalRandom.getInstance() ;
    int[] index = new int[size];
    boolean[] flag = new boolean[size];

    for (int n = 0; n < size; n++) {
      index[n] = n;
      flag[n] = true;
    }

    int num = 0;
    while (num < size) {
      int start = randomGenerator.nextInt(0, size - 1);
      while (true) {
        if (flag[start]) {
          permutation[num] = index[start];
          flag[start] = false;
          num++;
          break;
        }
        if (start == (size - 1)) {
          start = 0;
        } else {
          start++;
        }
      }
    }
    return permutation ;
  }

  @Override
  public int getSequenceLength() {
    return size ;
  }
}
