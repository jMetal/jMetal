package org.uma.jmetal.util.archive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/**
 * @author Antonio J. Nebro <ajnebro@uma.es>.
 */
public class NonDominatedSolutionListArchiveTest {
  private static final double  EPSILON = 0.000000000001 ;

  @Test
  public void shouldConstructorCreateAnEmptyArchive() {
    var archive = new NonDominatedSolutionListArchive<IntegerSolution>();

    assertEquals(0, archive.getSolutionList().size()) ;
  }

  @Test
  public void shouldConstructorAssignThePassedComparator() {
    DominanceWithConstraintsComparator<IntegerSolution> comparator = mock(
        DominanceWithConstraintsComparator.class) ;

    var archive = new NonDominatedSolutionListArchive<IntegerSolution>(comparator);

    assertSame(comparator, ReflectionTestUtils.getField(archive, "dominanceComparator")) ;
  }

  @Test
  public void shouldAddOnAnEmptyListHaveSizeOne() {
    var archive = new NonDominatedSolutionListArchive<IntegerSolution>();

    archive.add(mock(IntegerSolution.class)) ;

    assertEquals(1, archive.size()) ;
  }

  @Test
  public void shouldAddOnAnEmptyListInsertTheElement() {
    var archive = new NonDominatedSolutionListArchive<IntegerSolution>();

    var solution = mock(IntegerSolution.class) ;
    archive.add(solution) ;

    assertSame(solution, archive.getSolutionList().get(0)) ;
  }

  @Test
  public void shouldAddADominatedSolutionInAnArchiveOfSize1DiscardTheNewSolution() {
    var archive = new NonDominatedSolutionListArchive<DoubleSolution>();

    var solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 2.0 ;

    archive.add(solution2) ;

    assertEquals(1, archive.size()) ;
    assertSame(solution1, archive.get(0)) ;
  }

  @Test
  public void shouldAddADominantSolutionInAnArchiveOfSize1DiscardTheExistingSolution() {
    var archive = new NonDominatedSolutionListArchive<DoubleSolution>();

    var solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 0.0 ;
    solution2.objectives()[1] = 0.0 ;

    var result = archive.add(solution2) ;

    assertEquals(1, archive.size()) ;
    assertTrue(result) ;
    assertSame(solution2, archive.get(0)) ;
  }

  @Test
  public void shouldAddADominantSolutionInAnArchiveOfSize3DiscardTheRestOfSolutions() {
    var archive = new NonDominatedSolutionListArchive<DoubleSolution>();

    var solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 0.0 ;
    solution2.objectives()[1] = 2.0 ;

    archive.add(solution2) ;

    var solution3 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution3.objectives()[0] = 0.5 ;
    solution3.objectives()[1] = 1.5 ;

    archive.add(solution3) ;

    var solution4 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution4.objectives()[0] = 0.0 ;
    solution4.objectives()[1] = 0.0 ;

    archive.add(solution3) ;

    var result = archive.add(solution4) ;

    assertEquals(1, archive.size()) ;
    assertTrue(result) ;
    assertSame(solution4, archive.get(0)) ;
  }

  @Test
  public void shouldAddANonDominantSolutionInAnArchiveOfSize1IncorporateTheNewSolution() {
    var archive = new NonDominatedSolutionListArchive<DoubleSolution>();

    var solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 0.0 ;

    archive.add(solution2) ;

    assertEquals(2, archive.size()) ;
  }

  @Test
  public void shouldAddASolutionEqualsToOneAlreadyInTheArchiveDoNothing() {
    var archive = new NonDominatedSolutionListArchive<DoubleSolution>();

    var solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 0.0 ;

    archive.add(solution2) ;

    var equalSolution = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    equalSolution.objectives()[0] = 1.0 ;
    equalSolution.objectives()[1] = 1.0 ;

    var result = archive.add(equalSolution) ;

    assertEquals(2, archive.size()) ;
    assertFalse(result) ;
    assertTrue(archive.getSolutionList().contains(solution1) ||
            archive.getSolutionList().contains(equalSolution)) ;
  }

  @Test
  public void shouldJoinTwoEmptyArchivesReturnAnEmptyArchive() {
    var archive1 = new NonDominatedSolutionListArchive<IntegerSolution>();

    var archive2 = new NonDominatedSolutionListArchive<IntegerSolution>();

    archive1.join(archive2) ;

    assertEquals(0.0, archive1.getSolutionList().size(), EPSILON);
  }

  @Test
  public void shouldJoinWithAnEmptyArchivesRemainTheArchiveWithTheSameNumberOfSolutions() {
    var solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    var archive1 = new NonDominatedSolutionListArchive<DoubleSolution>();

    archive1.add(solution1) ;

    var archive2 = new NonDominatedSolutionListArchive<DoubleSolution>();

    archive1.join(archive2) ;

    assertEquals(1, archive1.getSolutionList().size(), EPSILON);
  }

  @Test
  public void shouldJoinAnEAnEmptyArchiveProduceAnArchiveWithTheSameSolutions() {
    var archive1 = new NonDominatedSolutionListArchive<DoubleSolution>();

    var archive2 = new NonDominatedSolutionListArchive<DoubleSolution>();

    var solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 2.0 ;

    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 1.0 ;

    archive2.add(solution1) ;
    archive2.add(solution2) ;

    archive1.join(archive2) ;

    assertEquals(2, archive1.getSolutionList().size(), EPSILON);
  }
}