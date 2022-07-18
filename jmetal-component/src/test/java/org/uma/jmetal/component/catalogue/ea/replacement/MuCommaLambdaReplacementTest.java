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
    int mu = 10;
    int lambda = 12;
    FakeDoubleProblem problem = new FakeDoubleProblem();
      List<DoubleSolution> population = new ArrayList<>();
      for (int i1 = 0; i1 < mu; i1++) {
          DoubleSolution problemSolution = problem.createSolution();
          population.add(problemSolution);
      }

      List<DoubleSolution> offspringPopulation = new ArrayList<>();
      for (int i = 0; i < lambda; i++) {
          DoubleSolution solution = problem.createSolution();
          offspringPopulation.add(solution);
      }

      MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    assertEquals(mu, replacement.replace(population, offspringPopulation).size()) ;
  }

  @Test
  void ReplaceRaisesAnExceptionIfMuIsEqualToLambda() {
    int mu = 10;
    int lambda = 10;
    FakeDoubleProblem problem = new FakeDoubleProblem();
      List<DoubleSolution> population = new ArrayList<>();
      for (int i1 = 0; i1 < mu; i1++) {
          DoubleSolution problemSolution = problem.createSolution();
          population.add(problemSolution);
      }

      List<DoubleSolution> offspringPopulation = new ArrayList<>();
      for (int i = 0; i < lambda; i++) {
          DoubleSolution solution = problem.createSolution();
          offspringPopulation.add(solution);
      }

      MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;


    assertThrows(
        InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }

  @Test
  void replaceRaisesAnExceptionIfMuIsLowerThanLambda() {
    int mu = 10;
    int lambda = 8;
    FakeDoubleProblem problem = new FakeDoubleProblem();
      List<DoubleSolution> population = new ArrayList<>();
      for (int i1 = 0; i1 < mu; i1++) {
          DoubleSolution problemSolution = problem.createSolution();
          population.add(problemSolution);
      }

      List<DoubleSolution> offspringPopulation = new ArrayList<>();
      for (int i = 0; i < lambda; i++) {
          DoubleSolution solution = problem.createSolution();
          offspringPopulation.add(solution);
      }

      MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;


    assertThrows(InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }
}