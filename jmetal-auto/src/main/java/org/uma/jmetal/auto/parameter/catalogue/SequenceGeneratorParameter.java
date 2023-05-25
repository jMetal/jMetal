package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerBoundedSequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

public class SequenceGeneratorParameter extends CategoricalParameter {
  int sequenceLength;
  public SequenceGeneratorParameter(List<String> sequenceGenerators) {
    super("sequenceGenerator", sequenceGenerators);
  }

  public void sequenceLength(int length) {
    this.sequenceLength = length ;
  }

  public SequenceGenerator<Integer> getParameter() {
    SequenceGenerator<Integer> sequenceGenerator ;
    switch (value()) {
      case "permutation":
        sequenceGenerator = new IntegerPermutationGenerator(sequenceLength) ;
        break;
      case "integerSequence":
        sequenceGenerator = new IntegerBoundedSequenceGenerator(sequenceLength);
        break;
      default:
        throw new JMetalException("Sequence generator does not exist: " + name());
    }

    return sequenceGenerator;
  }
}
