package org.uma.jmetal.experimental.component.catalogue.evaluation;

import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class SequentialEvaluationTest extends EvaluationTestCases<DoubleSolution>{

  public SequentialEvaluationTest() {
    this.problem = new DummyDoubleProblem() ;
    this.evaluation = new SequentialEvaluation<>(problem) ;
  }
}
