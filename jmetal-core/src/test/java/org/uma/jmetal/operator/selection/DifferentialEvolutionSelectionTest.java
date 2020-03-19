package org.uma.jmetal.operator.selection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/** Created by ajnebro on 3/5/15. */
public class DifferentialEvolutionSelectionTest {
  private DifferentialEvolutionSelection selection;
  private List<DoubleSolution> population;

  @Rule public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    exception.expect(NullParameterException.class);
    exception.expectMessage(containsString("The parameter is null"));

    selection = new DifferentialEvolutionSelection();

    selection.execute(null);
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsHasOneSolution() {
    exception.expect(InvalidConditionException.class);
    exception.expectMessage(containsString("The population has less than 3 solutions: " + 1));

    selection = new DifferentialEvolutionSelection();
    selection.setIndex(0);

    population = Arrays.asList(mock(DoubleSolution.class));

    selection.execute(population);
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSizeOFTheListOfSolutionsIsLowerThanTheNumberOfRequestedSolutions() {
    exception.expect(InvalidConditionException.class);
    exception.expectMessage(containsString("The population has less than 3 solutions: " + 2));

    selection = new DifferentialEvolutionSelection(3, false);
    selection.setIndex(0);

    population = Arrays.asList(mock(DoubleSolution.class),mock(DoubleSolution.class));

    selection.execute(population);
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    exception.expect(InvalidConditionException.class);
    exception.expectMessage(containsString("The population has less than 3 solutions: " + 0));

    selection = new DifferentialEvolutionSelection();
    selection.setIndex(0);

    population = Collections.emptyList();

    selection.execute(population);
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsNegative() {
    exception.expect(InvalidConditionException.class);
    exception.expectMessage(containsString("Index value invalid: " + -1));

    selection = new DifferentialEvolutionSelection();
    selection.setIndex(-1);

    population =
        Arrays.asList(
            mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));

    selection.execute(population);
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsNotIndicated() {
    exception.expect(InvalidConditionException.class);
    exception.expectMessage(containsString("Index value invalid: " + Integer.MIN_VALUE));

    selection = new DifferentialEvolutionSelection();

    population =
        Arrays.asList(
            mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));

    selection.execute(population);
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsHigherThanTheSolutionListLength() {
    exception.expect(InvalidConditionException.class);
    exception.expectMessage(containsString("Index value invalid: " + 5));

    selection = new DifferentialEvolutionSelection();
    selection.setIndex(5);

    population =
        Arrays.asList(
            mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));

    selection.execute(population);
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

    List<DoubleSolution> parents = selection.execute(population);
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

    List<DoubleSolution> parents = selection.execute(population);
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

    List<DoubleSolution> parents = selection.execute(population);
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
