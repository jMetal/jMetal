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
    List<DoubleSolution> population = IntStream.range(0, mu).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      List<DoubleSolution> offspringPopulation = IntStream.range(0, lambda).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    assertEquals(mu, replacement.replace(population, offspringPopulation).size()) ;
  }

  @Test
  void ReplaceRaisesAnExceptionIfMuIsEqualToLambda() {
    int mu = 10;
    int lambda = 10;
    FakeDoubleProblem problem = new FakeDoubleProblem();
    List<DoubleSolution> population = IntStream.range(0, mu).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      List<DoubleSolution> offspringPopulation = IntStream.range(0, lambda).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;


    assertThrows(
        InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }

  @Test
  void replaceRaisesAnExceptionIfMuIsLowerThanLambda() {
    int mu = 10;
    int lambda = 8;
    FakeDoubleProblem problem = new FakeDoubleProblem();
    List<DoubleSolution> population = IntStream.range(0, mu).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      List<DoubleSolution> offspringPopulation = IntStream.range(0, lambda).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;


    assertThrows(InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }
}