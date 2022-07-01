package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

public class MuPlusLambdaReplacementTest {

  @Test
  public void shouldReplaceReturnAPopulationOfTheRequiredSizeIfMuIs10AndLambdaIs2() {
    int mu = 10;
    int lambda = 2;
    FakeDoubleProblem problem = new FakeDoubleProblem();
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
