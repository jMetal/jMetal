package org.uma.jmetal.util.archive;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/**
 * @author Antonio J. Nebro <ajnebro@uma.es>.
 */
class NonDominatedSolutionListArchiveTest {
  private static final double  EPSILON = 0.000000000001 ;

  @Test
  void shouldConstructorCreateAnEmptyArchive() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    Assertions.assertEquals(0, archive.solutions().size());
  }

  @Test
  void shouldConstructorAssignThePassedComparator() {
    DominanceWithConstraintsComparator<IntegerSolution> comparator = mock(
        DominanceWithConstraintsComparator.class) ;

    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<IntegerSolution>(comparator) ;

    Assertions.assertSame(comparator, ReflectionTestUtils.getField(archive, "dominanceComparator"));
  }

  @Test
  void shouldAddOnAnEmptyListHaveSizeOne() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    archive.add(mock(IntegerSolution.class)) ;

    Assertions.assertEquals(1, archive.size());
  }

  @Test
  void shouldAddOnAnEmptyListInsertTheElement() {
    NonDominatedSolutionListArchive<IntegerSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    archive.add(solution) ;

    Assertions.assertSame(solution, archive.solutions().get(0));
  }

  @Test
  void shouldAddADominatedSolutionInAnArchiveOfSize1DiscardTheNewSolution() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 2.0 ;

    archive.add(solution2) ;

    Assertions.assertEquals(1, archive.size());
    Assertions.assertSame(solution1, archive.get(0));
  }

  @Test
  void shouldAddADominantSolutionInAnArchiveOfSize1DiscardTheExistingSolution() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 0.0 ;
    solution2.objectives()[1] = 0.0 ;

    boolean result = archive.add(solution2) ;

    Assertions.assertEquals(1, archive.size());
    Assertions.assertTrue(result);
    Assertions.assertSame(solution2, archive.get(0));
  }

  @Test
  void shouldAddADominantSolutionInAnArchiveOfSize3DiscardTheRestOfSolutions() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 0.0 ;
    solution2.objectives()[1] = 2.0 ;

    archive.add(solution2) ;

    DoubleSolution solution3 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution3.objectives()[0] = 0.5 ;
    solution3.objectives()[1] = 1.5 ;

    archive.add(solution3) ;

    DoubleSolution solution4 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution4.objectives()[0] = 0.0 ;
    solution4.objectives()[1] = 0.0 ;

    archive.add(solution3) ;

    boolean result = archive.add(solution4) ;

    Assertions.assertEquals(1, archive.size());
    Assertions.assertTrue(result);
    Assertions.assertSame(solution4, archive.get(0));
  }

  @Test
  void shouldAddANonDominantSolutionInAnArchiveOfSize1IncorporateTheNewSolution() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 0.0 ;

    archive.add(solution2) ;

    Assertions.assertEquals(2, archive.size());
  }

  @Test
  void shouldAddASolutionEqualsToOneAlreadyInTheArchiveDoNothing() {
    NonDominatedSolutionListArchive<DoubleSolution> archive ;
    archive = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    archive.add(solution1) ;

    DoubleSolution solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 0.0 ;

    archive.add(solution2) ;

    DoubleSolution equalSolution = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    equalSolution.objectives()[0] = 1.0 ;
    equalSolution.objectives()[1] = 1.0 ;

    boolean result = archive.add(equalSolution) ;

    Assertions.assertEquals(2, archive.size());
    Assertions.assertFalse(result);
    Assertions.assertTrue(archive.solutions().contains(solution1) ||
            archive.solutions().contains(equalSolution));
  }

  @Test
  void shouldJoinTwoEmptyArchivesReturnAnEmptyArchive() {
    NonDominatedSolutionListArchive<IntegerSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    NonDominatedSolutionListArchive<IntegerSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    archive1.join(archive2) ;

    Assertions.assertEquals(0.0, archive1.solutions().size(), EPSILON);
  }

  @Test
  void shouldJoinWithAnEmptyArchivesRemainTheArchiveWithTheSameNumberOfSolutions() {
    DoubleSolution solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 1.0 ;

    NonDominatedSolutionListArchive<DoubleSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    archive1.add(solution1) ;

    NonDominatedSolutionListArchive<DoubleSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    archive1.join(archive2) ;

    Assertions.assertEquals(1, archive1.solutions().size(), EPSILON);
  }

  @Test
  void shouldJoinAnEAnEmptyArchiveProduceAnArchiveWithTheSameSolutions() {
    NonDominatedSolutionListArchive<DoubleSolution> archive1 ;
    archive1 = new NonDominatedSolutionListArchive<>() ;

    NonDominatedSolutionListArchive<DoubleSolution> archive2 ;
    archive2 = new NonDominatedSolutionListArchive<>() ;

    DoubleSolution solution1 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution1.objectives()[0] = 1.0 ;
    solution1.objectives()[1] = 2.0 ;

    DoubleSolution solution2 = new FakeDoubleProblem(2, 2, 0).createSolution() ;
    solution2.objectives()[0] = 2.0 ;
    solution2.objectives()[1] = 1.0 ;

    archive2.add(solution1) ;
    archive2.add(solution2) ;

    archive1.join(archive2) ;

    Assertions.assertEquals(2, archive1.solutions().size(), EPSILON);
  }
}