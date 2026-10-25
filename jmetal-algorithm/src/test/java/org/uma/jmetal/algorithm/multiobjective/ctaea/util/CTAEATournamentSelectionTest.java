package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * The random indexes of the tournament are given by a mock of the random generator of {@link
 * JMetalRandom}, which is restored after each test.
 */
@DisplayName("Unit tests for class CTAEATournamentSelection")
class CTAEATournamentSelectionTest {
  private static final int LAST_INDEX = 4;

  private CTAEATournamentSelection<DoubleSolution> selection;
  private PseudoRandomGenerator originalGenerator;
  private PseudoRandomGenerator generator;
  private DoubleSolution feasible;
  private DoubleSolution dominatingFeasible;
  private DoubleSolution otherFeasible;
  private DoubleSolution infeasible;
  private DoubleSolution otherInfeasible;
  private List<DoubleSolution> archive;

  /** Creates a solution; a negative constraint value is a violation. */
  private static DoubleSolution solution(double constraint, double... objectives) {
    DoubleSolution solution = new DefaultDoubleSolution(new ArrayList<>(), objectives.length, 1);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    solution.constraints()[0] = constraint;
    return solution;
  }

  @BeforeEach
  void setUp() {
    originalGenerator = JMetalRandom.getInstance().getRandomGenerator();
    generator = mock(PseudoRandomGenerator.class);
    JMetalRandom.getInstance().setRandomGenerator(generator);

    feasible = solution(0.0, 1.0, 1.0);
    dominatingFeasible = solution(0.0, 0.5, 0.5);
    otherFeasible = solution(1.0, 2.0, 0.0);
    infeasible = solution(-1.0, 0.0, 0.0);
    otherInfeasible = solution(-2.0, 0.0, 0.0);
    archive = List.of(feasible, dominatingFeasible, otherFeasible, infeasible, otherInfeasible);
    selection = new CTAEATournamentSelection<>();
  }

  @AfterEach
  void tearDown() {
    JMetalRandom.getInstance().setRandomGenerator(originalGenerator);
  }

  @Nested
  @DisplayName("When only one candidate is feasible")
  class WhenOnlyOneCandidateIsFeasible {

    @Test
    @DisplayName("given a feasible first candidate, when selecting, then it wins")
    void givenAFeasibleFirstCandidate_whenSelecting_thenItWins() {
      // Arrange
      when(generator.nextInt(0, LAST_INDEX)).thenReturn(0, 3);

      // Act
      DoubleSolution winner = selection.execute(archive);

      // Assert
      assertSame(feasible, winner);
    }

    @Test
    @DisplayName("given a feasible second candidate, when selecting, then it wins")
    void givenAFeasibleSecondCandidate_whenSelecting_thenItWins() {
      // Arrange
      when(generator.nextInt(0, LAST_INDEX)).thenReturn(3, 0);

      // Act
      DoubleSolution winner = selection.execute(archive);

      // Assert
      assertSame(feasible, winner);
    }
  }

  @Nested
  @DisplayName("When both candidates are feasible")
  class WhenBothCandidatesAreFeasible {

    @Test
    @DisplayName("given a dominating second candidate, when selecting, then it wins")
    void givenADominatingSecondCandidate_whenSelecting_thenItWins() {
      // Arrange
      when(generator.nextInt(0, LAST_INDEX)).thenReturn(0, 1);

      // Act
      DoubleSolution winner = selection.execute(archive);

      // Assert
      assertSame(dominatingFeasible, winner);
    }

    @Test
    @DisplayName("given a dominating first candidate, when selecting, then it wins")
    void givenADominatingFirstCandidate_whenSelecting_thenItWins() {
      // Arrange
      when(generator.nextInt(0, LAST_INDEX)).thenReturn(1, 0);

      // Act
      DoubleSolution winner = selection.execute(archive);

      // Assert
      assertSame(dominatingFeasible, winner);
    }

    @Test
    @DisplayName("given non-dominated candidates, when selecting, then the first one wins")
    void givenNonDominatedCandidates_whenSelecting_thenTheFirstOneWins() {
      // Arrange
      when(generator.nextInt(0, LAST_INDEX)).thenReturn(2, 0);

      // Act
      DoubleSolution winner = selection.execute(archive);

      // Assert
      assertSame(otherFeasible, winner);
    }
  }

  @Nested
  @DisplayName("When both candidates are infeasible")
  class WhenBothCandidatesAreInfeasible {

    @Test
    @DisplayName("given two infeasible candidates, when selecting, then a third random one wins")
    void givenTwoInfeasibleCandidates_whenSelecting_thenAThirdRandomSolutionWins() {
      // Arrange
      when(generator.nextInt(0, LAST_INDEX)).thenReturn(3, 4, 2);

      // Act
      DoubleSolution winner = selection.execute(archive);

      // Assert
      assertSame(otherFeasible, winner);
      verify(generator, times(3)).nextInt(0, LAST_INDEX);
    }
  }

  @Nested
  @DisplayName("When the archive is empty")
  class WhenTheArchiveIsEmpty {

    @Test
    @DisplayName("given an empty archive, when selecting, then an exception is thrown")
    void givenAnEmptyArchive_whenSelecting_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(EmptyCollectionException.class, () -> selection.execute(List.of()));
    }
  }
}
