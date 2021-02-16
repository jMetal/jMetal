package org.uma.jmetal.problem.multiobjective.lz09;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LZ09F9
 */
@SuppressWarnings("serial")
public class LZ09F9 extends AbstractDoubleProblem {

  private LZ09 lz09;

  /**
   * Creates a default LZ09F9 problem (30 variables and 2 objectives)
   **/
  public LZ09F9() {
    this(22, 1, 22);
  }

  /**
   * Creates a LZ09F9 problem instance
   */
  public LZ09F9(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    setNumberOfVariables(30);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F9");

    lz09 = new LZ09(getNumberOfVariables(),
            getNumberOfObjectives(),
            ptype,
            dtype,
            ltype);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
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
      solution.setObjective(i, y.get(i));
    }

    return solution ;
  }
}

