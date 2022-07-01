package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class SequentialEvaluationTest extends EvaluationTestCases<DoubleSolution>{

  public SequentialEvaluationTest() {
    this.problem = new FakeDoubleProblem() ;
    this.evaluation = new SequentialEvaluation<>(problem) ;
  }
}
