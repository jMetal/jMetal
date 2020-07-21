package org.uma.jmetal.component.replacement;


import org.junit.Test;
import org.uma.jmetal.component.replacement.impl.MuCommaLambdaReplacement;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MuCommaLambdaReplacementTest   {

  @Test
  public void shouldReplaceReturnAPopulationOfTheRequiredSizeIfMuIs10AndLambdaIs12() {
    int mu = 10;
    int lambda = 12;
    DummyDoubleProblem problem = new DummyDoubleProblem();
    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < mu; i++) {
      population.add(problem.createSolution());
    }

    List<DoubleSolution> offspringPopulation = new ArrayList<>();
    for (int i = 0; i < lambda; i++) {
      offspringPopulation.add(problem.createSolution());
    }

    MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    assertEquals(mu, replacement.replace(population, offspringPopulation).size()) ;
  }

  @Test
  public void shouldReplaceRaiseAnExceptionIfMuIsEqualToLambda() {
    int mu = 10;
    int lambda = 10;
    DummyDoubleProblem problem = new DummyDoubleProblem();
    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < mu; i++) {
      population.add(problem.createSolution());
    }

    List<DoubleSolution> offspringPopulation = new ArrayList<>();
    for (int i = 0; i < lambda; i++) {
      offspringPopulation.add(problem.createSolution());
    }

    MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;


    assertThrows(InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }

  @Test
  public void shouldReplaceRaiseAnExceptionIfMuIsLowerThanLambda() {
    int mu = 10;
    int lambda = 8;
    DummyDoubleProblem problem = new DummyDoubleProblem();
    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < mu; i++) {
      population.add(problem.createSolution());
    }

    List<DoubleSolution> offspringPopulation = new ArrayList<>();
    for (int i = 0; i < lambda; i++) {
      offspringPopulation.add(problem.createSolution());
    }

    MuCommaLambdaReplacement<DoubleSolution> replacement = new MuCommaLambdaReplacement<>(new ObjectiveComparator<>(0)) ;


    assertThrows(InvalidConditionException.class, () -> replacement.replace(population, offspringPopulation)) ;
  }
}