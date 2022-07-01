package org.uma.jmetal.problem.multiobjective.lz09;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem LZ09F7
 */
@SuppressWarnings("serial")
public class LZ09F7 extends AbstractDoubleProblem {

  private LZ09 lz09;

  /**
   * Creates a default LZ09F7 problem (10 variables and 2 objectives)
   */
  public LZ09F7() {
    this(21, 3, 21);
  }

  /**
   * Creates a LZ09F7 problem instance
   */
  public LZ09F7(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    int numberOfVariables = 10;
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F7");

    lz09 = new LZ09(numberOfVariables,
            getNumberOfObjectives(),
            ptype,
            dtype,
            ltype);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    List<Double> x = new ArrayList<Double>(getNumberOfVariables());
    List<Double> y = new ArrayList<Double>(solution.objectives().length);

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x.add(solution.variables().get(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (int i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = y.get(i);
    }
    return solution ;
  }
}
