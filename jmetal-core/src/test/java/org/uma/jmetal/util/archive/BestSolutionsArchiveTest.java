package org.uma.jmetal.util.archive;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

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

  @Test
  void givenAnArchiveWithThreeSolutionsWhenAnObjectiveIsTheSameThenAnExceptionIsRaised() {
    // Arrange
    BestSolutionsArchive<DoubleSolution> archive =
        new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), 2);

    DoubleSolution solution1 = new FakeDoubleProblem(3, 3, 0).createSolution();
    solution1.objectives()[0] = 4.0 ;
    solution1.objectives()[1] = 2.0 ;
    solution1.objectives()[2] = 1.0 ;

    DoubleSolution solution2 = new FakeDoubleProblem(3, 3, 0).createSolution();
    solution2.objectives()[0] = 4.0 ;
    solution2.objectives()[1] = 1.0 ;
    solution2.objectives()[2] = 2.0 ;

    DoubleSolution solution3 = new FakeDoubleProblem(3, 3, 0).createSolution();
    solution3.objectives()[0] = 4.0 ;
    solution3.objectives()[1] = 1.5 ;
    solution3.objectives()[2] = 1.5 ;

    archive.add(solution1);
    archive.add(solution2) ;
    archive.add(solution3) ;

    // Act
    var result = archive.solutions() ;

    // Assert
    assertEquals(2, result.size());
    assertEquals(3, archive.size());
  }
}
