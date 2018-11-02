package org.uma.jmetal.problem.multiobjective.lz09;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LZ09F2
 */
@SuppressWarnings("serial")
public class LZ09F2Novelty extends AbstractDoubleProblem {

  private LZ09 lz09;

  /**
   * Creates a default LZ09F2 problem (30 variables and 3 objectives)
   */
  public LZ09F2Novelty() {
    this(21, 1, 22);
  }

  /**
   * Creates a LZ09F2 problem instance
   */
  public LZ09F2Novelty(Integer ptype,
                       Integer dtype,
                       Integer ltype) throws JMetalException {
    setNumberOfVariables(30);
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("LZ09F2");

    lz09 = new LZ09(getNumberOfVariables(),
            getNumberOfObjectives()-1,
            ptype,
            dtype,
            ltype);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    List<Double> x = new ArrayList<Double>(getNumberOfVariables());
    List<Double> y = new ArrayList<Double>(getNumberOfObjectives());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x.add(solution.getVariableValue(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    solution.setObjective(0, y.get(0));
    solution.setObjective(1, y.get(1));
  }
}

