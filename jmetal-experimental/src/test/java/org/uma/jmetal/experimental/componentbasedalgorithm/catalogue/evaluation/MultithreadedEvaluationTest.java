package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.MultithreadedEvaluation;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class MultithreadedEvaluationTest extends EvaluationTestCases<DoubleSolution>{

  public MultithreadedEvaluationTest() {
    this.problem = new DummyDoubleProblem() ;
    this.evaluation = new MultithreadedEvaluation<>(8, problem) ;
  }
}
