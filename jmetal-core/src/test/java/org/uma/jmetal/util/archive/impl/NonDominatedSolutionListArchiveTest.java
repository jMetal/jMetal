package org.uma.jmetal.util.archive.impl;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Antonio J. Nebro <ajnebro@uma.es>.
 */
public class NonDominatedSolutionListArchiveTest {
  private static final double  EPSILON = 0.000000000001 ;

  @Test
  public void shouldConstructorCreateAnEmptyArchive() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    assertEquals(0, archive.getSolutionList().size()) ;
  }

  @Test
  public void shouldConstructorAssignThePassedComparator() {
    @SuppressWarnings("unchecked")
    DominanceComparator<IntegerSolution> comparator = mock(DominanceComparator.class) ;

    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<IntegerSolution>(comparator) ;

    assertSame(comparator, ReflectionTestUtils.getField(archive, "dominanceComparator")) ;
  }

  @Test
  public void shouldAddOnAnEmptyListHaveSizeOne() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    archive.add(mock(IntegerSolution.class)) ;

    assertEquals(1, archive.size()) ;
  }

  @Test
  public void shouldAddOnAnEmptyListInsertTheElement() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    archive.add(solution) ;

    assertSame(solution, archive.getSolutionList().get(0)) ;
  }

  @Test
  public void shouldAddADominatedSolutionInAnArchiveOfSize1DiscardTheNewSolution() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 2.0 ;

    archive.add(solution2) ;

    assertEquals(1, archive.size()) ;
    assertSame(solution1, archive.get(0)) ;
  }

  @Test
  public void shouldAddADominantSolutionInAnArchiveOfSize1DiscardTheExistingSolution() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 0.0 ;
    solution2.objectives()[1] = 0.0 ;

    boolean result = archive.add(solution2) ;

    assertEquals(1, archive.size()) ;
    assertTrue(result) ;
    assertSame(solution2, archive.get(0)) ;
  }

  @Test
  public void shouldAddADominantSolutionInAnArchiveOfSize3DiscardTheRestOfSolutions() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 0.0 ;
    solution2.objectives()[1] = 2.0 ;

    archive.add(solution2) ;

    DoubleSolution solution3 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution3.objectives()[0] = 0.5 ;
    solution3.objectives()[1] = 1.5 ;

    archive.add(solution3) ;

    DoubleSolution solution4 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution4.objectives()[0] = 0.0 ;
    solution4.objectives()[1] = 0.0 ;

    archive.add(solution3) ;

    boolean result = archive.add(solution4) ;

    assertEquals(1, archive.size()) ;
    assertTrue(result) ;
    assertSame(solution4, archive.get(0)) ;
  }

  @Test
  public void shouldAddANonDominantSolutionInAnArchiveOfSize1IncorporateTheNewSolution() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 0.0 ;

    archive.add(solution2) ;

    assertEquals(2, archive.size()) ;
  }

  @Test
  public void shouldAddASolutionEqualsToOneAlreadyInTheArchiveDoNothing() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 0.0 ;

    archive.add(solution2) ;

    DoubleSolution equalSolution = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    equalSolution.objectives()[0] = 1.0 ;
    equalSolution.objectives()[1] = 1.0 ;

    boolean result = archive.add(equalSolution) ;

    assertEquals(2, archive.size()) ;
    assertFalse(result) ;
    assertTrue(archive.getSolutionList().contains(solution1) ||
            archive.getSolutionList().contains(equalSolution)) ;
  }

  @Test
  public void shouldJoinTwoEmptyArchivesReturnAnEmptyArchive() {
    NonDominatedSolutionListArchive<IntegerSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    NonDominatedSolutionListArchive<IntegerSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    archive1.join(archive2) ;

    assertEquals(0.0, archive1.getSolutionList().size(), EPSILON);
  }

  @Test
  public void shouldJoinWithAnEmptyArchivesRemainTheArchiveWithTheSameNumberOfSolutions() {
    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    NonDominatedSolutionListArchive<DoubleSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    archive1.add(solution1) ;

    NonDominatedSolutionListArchive<DoubleSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    archive1.join(archive2) ;

    assertEquals(1, archive1.getSolutionList().size(), EPSILON);
  }

  @Test
  public void shouldJoinAnEAnEmptyArchiveProduceAnArchiveWithTheSameSolutions() {
    NonDominatedSolutionListArchive<DoubleSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    NonDominatedSolutionListArchive<DoubleSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 2.0 ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 1.0 ;

    archive2.add(solution1) ;
    archive2.add(solution2) ;

    archive1.join(archive2) ;

    assertEquals(2, archive1.getSolutionList().size(), EPSILON);
  }
}