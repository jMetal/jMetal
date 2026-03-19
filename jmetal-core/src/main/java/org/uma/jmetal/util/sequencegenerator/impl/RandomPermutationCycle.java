package org.uma.jmetal.util.sequencegenerator.impl;

import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * A sequence generator that produces random permutations of integers from 0 (inclusive) to the
 * specified size (exclusive). Each time the sequence is exhausted, a new random permutation is
 * generated.
 *
 * <p>This implementation uses an efficient algorithm to generate random permutations without
 * repetition until all elements have been used, at which point a new random permutation is created.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Will generate sequences like: [2,0,1,3,4], [1,3,2,0,4], [4,2,0,1,3], ...
 * SequenceGenerator<Integer> generator = new RandomPermutationCycle(5);
 * }</pre>
 *
 * @author Antonio J. Nebro
 */
public class RandomPermutationCycle implements SequenceGenerator<Integer> {
  /** The current permutation of indices */
  private int[] sequence;
  
  /** Current position in the sequence */
  private int index;
  
  /** Size of the permutation */
  private final int size;
  
  /** Random number generator */
  private final JMetalRandom randomGenerator = JMetalRandom.getInstance();

  /**
   * Creates a new random permutation cycle generator.
   *
   * @param size the size of the permutation (must be positive)
   * @throws IllegalArgumentException if size is not positive
   */
  public RandomPermutationCycle(int size) {
    Check.that(size > 0, "Size " + size + " must be positive");
    this.size = size;
    this.sequence = randomPermutation(size);
    this.index = 0;
  }

  @Override
  public Integer getValue() {
    return sequence[index];
  }

  @Override
  public void generateNext() {
    index++;
    if (index == sequence.length) {
      // When we reach the end of the current permutation,
      // generate a new random permutation and reset the index
      sequence = randomPermutation(size);
      index = 0;
    }
  }

  /**
   * Generates a random permutation of integers from 0 to size-1.
   * 
   * <p>The algorithm works by:
   * 1. Creating an array of indices [0, 1, 2, ..., size-1]
   * 2. Randomly selecting unused indices to build the permutation
   * 3. Using a flag array to track which indices have been used
   *
   * @param size the size of the permutation to generate
   * @return an array containing a random permutation of the indices
   */
  private int[] randomPermutation(int size) {
    int[] permutation = new int[size];
    int[] index = new int[size];
    boolean[] flag = new boolean[size];

    // Initialize arrays
    for (int n = 0; n < size; n++) {
      index[n] = n;           // Values to permute
      flag[n] = true;         // All values are initially available
    }

    int num = 0;
    while (num < size) {
      // Start at a random position
      int start = randomGenerator.nextInt(0, size - 1);
      
      // Find the next available index
      while (true) {
        if (flag[start]) {
          // Found an unused index, add it to the permutation
          permutation[num] = index[start];
          flag[start] = false;  // Mark as used
          num++;
          break;
        }
        // Move to next index, wrapping around if needed
        start = (start == size - 1) ? 0 : start + 1;
      }
    }
    return permutation;
  }

  @Override
  public int getSequenceLength() {
    return size;
  }
}
