package org.uma.jmetal.util.permutation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

public class PermutationFactory {
  static List<Integer> createIntegerPermutation(int length, PseudoRandomGenerator randomGenerator) {
    Check.valueIsNotNegative(length);
    Check.notNull(randomGenerator);

    List<Integer> integerList = new LinkedList<>() ;
    IntStream.range(0, length).forEach(integerList::add);

    List<Integer> permutation = new ArrayList<>(length) ;
    while(!integerList.isEmpty()) {
      int index = randomGenerator.nextInt(0, integerList.size()-1) ;
      permutation.add(integerList.get(index)) ;
      integerList.remove(index) ;
    }

    return permutation ;
  }

}