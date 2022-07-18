package org.uma.jmetal.component.catalogue.ea.replacement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuCommaLambdaReplacement;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

class MuCommaLambdaReplacementTest {

  @Test
  void ReplaceReturnsAPopulationOfTheRequiredSizeIfMuIs10AndLambdaIs12() {
      var mu = 10;
      var lambda = 12;
      var problem = new FakeDoubleProblem();
      List<DoubleSolution> population = new ArrayList<>();
      for (var i1 = 0; i1 < mu; i1++) {
          var problemSolution = problem.createSolution();
          population.add(problemSolution);
      }

      List<DoubleSolution> offspringPopulation = new ArrayList<>();
      for (var i = 0; i < lambda; i++) {
          var solution = problem.createSolution();
          offspringPopulation.add(solution);
      }

      var replacement = new MuCommaLambdaReplacement<DoubleSolution>(new ObjectiveComparator<>(0)) ;

    assertEquals(mu, replacement.replace(population, offspringPopulation).size()) ;
  }

  @Test
  void ReplaceRaisesAnExceptionIfMuIsEqualToLambda() {
      var mu = 10;
      var lambda = 10;
      var problem = new FakeDoubleProblem();
      List<DoubleSolution> population = new ArrayList<>();
      for (var i1 = 0; i1 < mu; i1++) {
          var problemSolution = problem.createSolution();
          population.add(problemSolution);
      }

      List<DoubleSolution> offspringPopulation = new ArrayList<>();
      for (var i = 0; i < lambda; i++) {
          var solution = problem.createSolution();
          offspringPopulation.add(solution);
      }

      var replacement = new MuCommaLambdaReplacement<DoubleSolution>(new ObjectiveComparator<>(0)) ;


    assertThrows(
        InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }

  @Test
  void replaceRaisesAnExceptionIfMuIsLowerThanLambda() {
      var mu = 10;
      var lambda = 8;
      var problem = new FakeDoubleProblem();
      List<DoubleSolution> population = new ArrayList<>();
      for (var i1 = 0; i1 < mu; i1++) {
          var problemSolution = problem.createSolution();
          population.add(problemSolution);
      }

      List<DoubleSolution> offspringPopulation = new ArrayList<>();
      for (var i = 0; i < lambda; i++) {
          var solution = problem.createSolution();
          offspringPopulation.add(solution);
      }

      var replacement = new MuCommaLambdaReplacement<DoubleSolution>(new ObjectiveComparator<>(0)) ;


    assertThrows(InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }
}