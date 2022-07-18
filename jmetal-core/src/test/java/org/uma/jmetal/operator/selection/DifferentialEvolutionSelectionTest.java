package org.uma.jmetal.operator.selection;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/** Created by ajnebro on 3/5/15. */
public class DifferentialEvolutionSelectionTest {
  private DifferentialEvolutionSelection selection;
  private List<DoubleSolution> population;

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    selection = new DifferentialEvolutionSelection();
    
    Executable executable = () -> selection.execute(null);

    var cause = assertThrows(NullParameterException.class, executable);
    assertThat(cause.getMessage(), containsString("The parameter is null"));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsHasOneSolution() {
    selection = new DifferentialEvolutionSelection();
    selection.setIndex(0);

    population = Arrays.asList(mock(DoubleSolution.class));

    Executable executable = () -> selection.execute(population);

    var cause = assertThrows(InvalidConditionException.class, executable);
    assertThat(cause.getMessage(), containsString("The population has less than 3 solutions: " + 1));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSizeOFTheListOfSolutionsIsLowerThanTheNumberOfRequestedSolutions() {
    selection = new DifferentialEvolutionSelection(3, false);
    selection.setIndex(0);

    population = Arrays.asList(mock(DoubleSolution.class),mock(DoubleSolution.class));

    Executable executable = () -> selection.execute(population);

    var cause = assertThrows(InvalidConditionException.class, executable);
    assertThat(cause.getMessage(), containsString("The population has less than 3 solutions: " + 2));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    selection = new DifferentialEvolutionSelection();
    selection.setIndex(0);

    population = Collections.emptyList();

    Executable executable = () -> selection.execute(population);

    var cause = assertThrows(InvalidConditionException.class, executable);
    assertThat(cause.getMessage(), containsString("The population has less than 3 solutions: " + 0));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsNegative() {
    selection = new DifferentialEvolutionSelection();
    selection.setIndex(-1);

    population =
        Arrays.asList(
            mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));

    Executable executable = () -> selection.execute(population);

    var cause = assertThrows(InvalidConditionException.class, executable);
    assertThat(cause.getMessage(), containsString("Index value invalid: " + -1));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsNotIndicated() {
    selection = new DifferentialEvolutionSelection();

    population =
        Arrays.asList(
            mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));

    Executable executable = () -> selection.execute(population);

    var cause = assertThrows(InvalidConditionException.class, executable);
    assertThat(cause.getMessage(), containsString("Index value invalid: " + Integer.MIN_VALUE));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsHigherThanTheSolutionListLength() {
    selection = new DifferentialEvolutionSelection();
    selection.setIndex(5);

    population =
        Arrays.asList(
            mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));

    Executable executable = () -> selection.execute(population);

    var cause = assertThrows(InvalidConditionException.class, executable);
    assertThat(cause.getMessage(), containsString("Index value invalid: " + 5));
  }

  @Test
  public void shouldExecuteReturnThreeDifferentSolutionsIfTheListHasFourElements() {
    selection = new DifferentialEvolutionSelection();
    selection.setIndex(1);

    population =
        Arrays.asList(
            mock(DoubleSolution.class),
            mock(DoubleSolution.class),
            mock(DoubleSolution.class),
            mock(DoubleSolution.class));

    var parents = selection.execute(population);
    assertEquals(3, parents.size());

    // The index solution must not be in the result
    assertNotSame(population.get(1), parents.get(0));
    assertNotSame(population.get(1), parents.get(1));
    assertNotSame(population.get(1), parents.get(2));

    assertThat(parents, hasItem(population.get(0)));
    assertThat(parents, hasItem(population.get(2)));
    assertThat(parents, hasItem(population.get(3)));
    assertThat(parents, not(hasItem(population.get(1))));
  }

  @Test
  public void shouldExecuteReturnFiveDifferentSolutionsIfTheListHasSixElements() {
    selection = new DifferentialEvolutionSelection(5, false);
    selection.setIndex(2);

    population =
            Arrays.asList(
                    mock(DoubleSolution.class),
                    mock(DoubleSolution.class),
                    mock(DoubleSolution.class),
                    mock(DoubleSolution.class),
                    mock(DoubleSolution.class),
                    mock(DoubleSolution.class));

    var parents = selection.execute(population);
    assertEquals(5, parents.size());

    // The index solution must not be in the result
    assertNotSame(population.get(2), parents.get(0));
    assertNotSame(population.get(2), parents.get(1));
    assertNotSame(population.get(2), parents.get(2));
    assertNotSame(population.get(2), parents.get(3));
    assertNotSame(population.get(2), parents.get(4));

    assertThat(parents, hasItem(population.get(0)));
    assertThat(parents, hasItem(population.get(1)));
    assertThat(parents, hasItem(population.get(3)));
    assertThat(parents, hasItem(population.get(4)));
    assertThat(parents, not(hasItem(population.get(2))));
  }

  @Test
  public void shouldExecuteReturnThreeDifferentSolutionsIncludingTheCurrentOne() {
    selection = new DifferentialEvolutionSelection(3, true);
    selection.setIndex(1);

    population =
            Arrays.asList(
                    mock(DoubleSolution.class),
                    mock(DoubleSolution.class),
                    mock(DoubleSolution.class));

    var parents = selection.execute(population);
    assertEquals(3, parents.size());

    // The index solution must not be in the result
    assertNotSame(population.get(1), parents.get(0));
    assertNotSame(population.get(1), parents.get(1));
    assertSame(population.get(1), parents.get(2));

    assertNotSame(parents.get(0), parents.get(1));

    assertThat(parents, hasItem(population.get(0)));
    assertThat(parents, hasItem(population.get(2)));
    assertThat(parents, hasItem(population.get(1)));
  }

  /*
  @Test
  public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
    // Configuration
    List<DoubleSolution> solutions =
        Arrays.asList(
            mock(DoubleSolution.class),
            mock(DoubleSolution.class),
            mock(DoubleSolution.class),
            mock(DoubleSolution.class));

    // Check configuration leads to use default generator by default
    final int[] defaultUses = {0};
    JMetalRandom defaultGenerator = JMetalRandom.getInstance();
    AuditableRandomGenerator auditor =
        new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
    defaultGenerator.setRandomGenerator(auditor);
    auditor.addListener((a) -> defaultUses[0]++);

    DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection();
    selection.setIndex(1);
    selection.execute(solutions);
    assertTrue("No use of the default generator", defaultUses[0] > 0);

    // Test same configuration uses custom generator instead
    solutions =
        Arrays.asList(
            mock(DoubleSolution.class),
            mock(DoubleSolution.class),
            mock(DoubleSolution.class),
            mock(DoubleSolution.class));
    defaultUses[0] = 0;

    final int[] customUses = {0};
    selection =
        new DifferentialEvolutionSelection(
            (a, b) -> {
              customUses[0]++;
              return new Random().nextInt(b + 1 - a) + a;
            });
    selection.setIndex(1);
    selection.execute(solutions);
    assertTrue("Default random generator used", defaultUses[0] == 0);
    assertTrue("No use of the custom generator", customUses[0] > 0);
  }
  */
}
