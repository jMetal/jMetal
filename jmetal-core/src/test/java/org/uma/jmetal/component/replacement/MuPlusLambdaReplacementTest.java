package org.uma.jmetal.component.replacement;

import org.junit.Test;
import org.uma.jmetal.component.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MuPlusLambdaReplacementTest {

  @Test
  public void shouldReplaceReturnAPopulationOfTheRequiredSizeIfMuIs10AndLambdaIs2() {
    int mu = 10;
    int lambda = 2;
    DummyDoubleProblem problem = new DummyDoubleProblem();
    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < mu; i++) {
      population.add(problem.createSolution());
    }

    List<DoubleSolution> offspringPopulation = new ArrayList<>();
    for (int i = 0; i < lambda; i++) {
      offspringPopulation.add(problem.createSolution());
    }

    MuPlusLambdaReplacement<DoubleSolution> replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    assertEquals(mu, replacement.replace(population, offspringPopulation).size()) ;
  }
}
