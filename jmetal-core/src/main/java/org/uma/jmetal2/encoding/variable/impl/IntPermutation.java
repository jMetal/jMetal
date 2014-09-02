package org.uma.jmetal2.encoding.variable.impl;

import org.uma.jmetal2.core.Variable;
import org.uma.jmetal2.encoding.variable.Int;
import org.uma.jmetal2.encoding.variable.Permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public class IntPermutation implements Permutation {
  private List<Integer> permutation ;

  /** Default constructor */
  public IntPermutation(int size) {
    ArrayList<Integer> randomSequence = new ArrayList<>(size);

    for (int i = 0; i < size; i++) {
      randomSequence.add(i);
    }

    java.util.Collections.shuffle(randomSequence);

    permutation = new ArrayList<Integer>(size) ;
    Collections.copy(permutation, randomSequence) ;
  }

  /** Constructor */
  public IntPermutation(List<Integer> permutation) {
    this.permutation = new ArrayList<Integer>(permutation.size()) ;
    Collections.copy(this.permutation, permutation) ;
  }

  /** Copy constructor */
  public IntPermutation(IntPermutation permutation) {
    this.permutation = new ArrayList<Integer>(permutation.permutation.size()) ;
    Collections.copy(this.permutation, permutation.permutation) ;
  }

  @Override
  public List<Integer> getValue() {
    return permutation;
  }

  @Override
  public void setValue(List value) {
    this.permutation = value ;
  }

  @Override
  public Variable copy() {
    return new IntPermutation(this);
  }
}
