package org.uma.jmetal.component.catalogue.ea.replacement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

class MuPlusLambdaReplacementTest {

  @Test
  public void replaceReturnsAPopulationOfTheRequiredSizeIfMuIs10AndLambdaIs2() {
    int mu = 10;
    int lambda = 2;
    FakeDoubleProblem problem = new FakeDoubleProblem();
    List<DoubleSolution> population = IntStream.range(0, mu).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      List<DoubleSolution> offspringPopulation = IntStream.range(0, lambda).mapToObj(i -> problem.createSolution()).collect(Collectors.toList());

      MuPlusLambdaReplacement<DoubleSolution> replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    assertEquals(mu, replacement.replace(population, offspringPopulation).size()) ;
  }
}