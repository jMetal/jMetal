package org.uma.jmetal.util.archive.impl;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.0) ;
    when(solution.getObjective(1)).thenReturn(1.0) ;
    archive.add(solution) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    when(solution2.getObjective(0)).thenReturn(2.0) ;
    when(solution2.getObjective(1)).thenReturn(2.0) ;
    archive.add(solution2) ;

    assertEquals(1, archive.size()) ;
    assertSame(solution, archive.get(0)) ;
  }

  @Test
  public void shouldAddADominantSolutionInAnArchiveOfSize1DiscardTheExistingSolution() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.0) ;
    when(solution.getObjective(1)).thenReturn(1.0) ;
    archive.add(solution) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    when(solution2.getObjective(0)).thenReturn(0.0) ;
    when(solution2.getObjective(1)).thenReturn(0.0) ;
    boolean result = archive.add(solution2) ;

    assertEquals(1, archive.size()) ;
    assertTrue(result) ;
    assertSame(solution2, archive.get(0)) ;
  }

  @Test
  public void shouldAddADominantSolutionInAnArchiveOfSize3DiscardTheRestOfSolutions() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.0) ;
    when(solution.getObjective(1)).thenReturn(1.0) ;
    archive.add(solution) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    when(solution2.getObjective(0)).thenReturn(0.0) ;
    when(solution2.getObjective(1)).thenReturn(2.0) ;
    archive.add(solution2) ;

    IntegerSolution solution3 = mock(IntegerSolution.class) ;
    when(solution3.getNumberOfObjectives()).thenReturn(2) ;
    when(solution3.getObjective(0)).thenReturn(0.5) ;
    when(solution3.getObjective(1)).thenReturn(1.5) ;
    archive.add(solution3) ;

    IntegerSolution dominantSolution = mock(IntegerSolution.class) ;
    when(dominantSolution.getNumberOfObjectives()).thenReturn(2) ;
    when(dominantSolution.getObjective(0)).thenReturn(0.0) ;
    when(dominantSolution.getObjective(1)).thenReturn(0.0) ;
    boolean result = archive.add(dominantSolution) ;

    assertEquals(1, archive.size()) ;
    assertTrue(result) ;
    assertSame(dominantSolution, archive.get(0)) ;
  }

  @Test
  public void shouldAddANonDominantSolutionInAnArchiveOfSize1IncorporateTheNewSolution() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.0) ;
    when(solution.getObjective(1)).thenReturn(1.0) ;
    archive.add(solution) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    when(solution2.getObjective(0)).thenReturn(2.0) ;
    when(solution2.getObjective(1)).thenReturn(0.0) ;
    archive.add(solution2) ;

    assertEquals(2, archive.size()) ;
  }

  @Test
  public void shouldAddASolutionEqualsToOneAlreadyInTheArchiveDoNothing() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.0) ;
    when(solution.getObjective(1)).thenReturn(1.0) ;
    archive.add(solution) ;

    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    when(solution2.getObjective(0)).thenReturn(2.0) ;
    when(solution2.getObjective(1)).thenReturn(0.0) ;
    archive.add(solution2) ;

    IntegerSolution equalSolution = mock(IntegerSolution.class) ;
    when(equalSolution.getNumberOfObjectives()).thenReturn(2) ;
    when(equalSolution.getObjective(0)).thenReturn(1.0) ;
    when(equalSolution.getObjective(1)).thenReturn(1.0) ;
    boolean result = archive.add(equalSolution) ;

    assertEquals(2, archive.size()) ;
    assertFalse(result) ;
    assertTrue(archive.getSolutionList().contains(solution) ||
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
    NonDominatedSolutionListArchive<IntegerSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.0) ;
    when(solution.getObjective(1)).thenReturn(1.0) ;
    archive1.add(solution) ;


    NonDominatedSolutionListArchive<IntegerSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    archive1.join(archive2) ;

    assertEquals(1, archive1.getSolutionList().size(), EPSILON);
  }

  @Test
  public void shouldJoinAnEAnEmptyArchiveProduceAnArchiveWithTheSameSolutions() {
    NonDominatedSolutionListArchive<IntegerSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    NonDominatedSolutionListArchive<IntegerSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    when(solution.getNumberOfObjectives()).thenReturn(2) ;
    when(solution.getObjective(0)).thenReturn(1.0) ;
    when(solution.getObjective(1)).thenReturn(2.0) ;
    IntegerSolution solution2 = mock(IntegerSolution.class) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    when(solution2.getObjective(0)).thenReturn(2.0) ;
    when(solution2.getObjective(1)).thenReturn(1.0) ;

    archive2.add(solution) ;
    archive2.add(solution2) ;

    archive1.join(archive2) ;

    assertEquals(2, archive1.getSolutionList().size(), EPSILON);
  }
}