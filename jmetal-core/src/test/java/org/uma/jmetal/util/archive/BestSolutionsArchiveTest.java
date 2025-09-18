package org.uma.jmetal.util.archive;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BestSolutionsArchiveTest {
  @Test
  public void givenAnEmptyArchiveWhenAddingASolutionThenTheArchiveContainsTheSolution() {
    // Arrange
    BestSolutionsArchive<DoubleSolution> archive =
        new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), 100);

    DoubleSolution solution = new FakeDoubleProblem(3, 2, 0).createSolution();

    // Act
    archive.add(solution);

    // Assert
    assertEquals(1, archive.size());
  }

  @Test
  public void givenAnArchiveWithASolutionWhenSelectingTheBestSolutionsThenReturnsTheSolution() {
    // Arrange  
    BestSolutionsArchive<DoubleSolution> archive =
        new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), 100);

    DoubleSolution solution = new FakeDoubleProblem(3, 2, 0).createSolution();
    solution.objectives()[0] = 4.0 ;
    solution.objectives()[1] = 4.0 ;
    
    System.out.println(solution) ;
    archive.add(solution);

    // Act
    var bestSolutions = archive.solutions();

    // Assert
    assertEquals(1, bestSolutions.size());
    assertSame(solution, bestSolutions.get(0));
  }

  @Test
  void givenAnArchiveWithASolutionWhenAddingTheSameSolutionThenTheArchiveDoesNotChange() {
    // Arrange
    BestSolutionsArchive<DoubleSolution> archive =
        new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), 100);

    DoubleSolution solution1 = new FakeDoubleProblem(3, 2, 0).createSolution();
    solution1.objectives()[0] = 4.0 ;
    solution1.objectives()[1] = 2.0 ;

    DoubleSolution solution2 = new FakeDoubleProblem(3, 2, 0).createSolution();
    solution2.objectives()[0] = 4.0 ;
    solution2.objectives()[1] = 2.0 ;

    archive.add(solution1);

    // Act
    archive.add(solution2) ;

    // Assert
    assertEquals(1, archive.size());
  }
}
