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

      MuPlusLambdaReplacement<DoubleSolution> replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    assertEquals(mu, replacement.replace(population, offspringPopulation).size()) ;
  }
}