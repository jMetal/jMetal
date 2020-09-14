package org.uma.jmetal.parallel.synchronous;

import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SparkEvaluationTest {

  @Test
  public void shouldEvaluateWorksProperly() {
    Logger.getLogger("org").setLevel(Level.OFF) ;

    SparkConf sparkConf = new SparkConf()
            .setMaster("local[8]")
            .setAppName("Spark evaluation");

    DoubleProblem doubleProblem = new DummyDoubleProblem() ;
    JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
    SparkEvaluation<DoubleSolution> solutionSparkEvaluation = new SparkEvaluation<>(sparkContext, doubleProblem) ;

    List<DoubleSolution> solutions = new ArrayList<>();

    IntStream.range(0, 100).forEach(i -> solutions.add(doubleProblem.createSolution()));
    solutionSparkEvaluation.evaluate(solutions) ;
    assertEquals(100, solutions.size());
  }
}