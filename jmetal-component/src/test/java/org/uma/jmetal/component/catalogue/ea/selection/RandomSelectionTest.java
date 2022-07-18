package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class RandomSelectionTest {

  @Test
  void theConstructorInitializesAValueWithTheProperStateValues() {
    int numberOfElementsToSelect = 5;
    boolean withReplacement = true;
    RandomSelection<DoubleSolution> selection = new RandomSelection<>(numberOfElementsToSelect,
        withReplacement);

    assertThat(selection.getNumberOfElementsToSelect()).isEqualTo(numberOfElementsToSelect);
    assertThat(selection.withReplacement()).isTrue();
  }

  @Test
  void selectWithoutReplacementWorksProperlyWhenTheListHasASolution() {
    List<DoubleSolution> solutions = List.of(mock(DoubleSolution.class));
    var selection = new RandomSelection<DoubleSolution>(1, false) ;

    List<DoubleSolution> selectedSolutions = selection.select(solutions) ;
    assertThat(selectedSolutions)
        .hasSize(1)
        .first().isSameAs(solutions.get(0)) ;
  }

  @Test
  void selectWithReplacementWorksProperlyWhenTheListHasASolution() {
    List<DoubleSolution> solutions = List.of(mock(DoubleSolution.class));
    var selection = new RandomSelection<DoubleSolution>(1, true) ;

    List<DoubleSolution> selectedSolutions = selection.select(solutions) ;
    assertThat(selectedSolutions)
        .hasSize(1)
        .first().isSameAs(solutions.get(0)) ;
  }

  @Test
  void selectWithoutReplacementWorksProperlyWhenTheListHasThreeSolutionsAndThreeSolutionsAreRequested() {
    List<DoubleSolution> solutions = List.
        of(mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class));
    var selection = new RandomSelection<DoubleSolution>(solutions.size(), false) ;

    List<DoubleSolution> selectedSolutions = selection.select(solutions) ;
    assertThat(selectedSolutions)
        .hasSize(3)
        .containsExactlyInAnyOrderElementsOf(solutions) ;
  }

  @Test
  void selectWithoutReplacementWorksProperlyWhenTheListHasFiveSolutionsAndThreeSolutionsAreRequested() {
    List<DoubleSolution> solutions = new ArrayList<>() ;
    int numberOfSolutions = 5 ;
    IntStream.range(0, numberOfSolutions).forEach(i -> solutions.add(mock(DoubleSolution.class)));
    var selection = new RandomSelection<DoubleSolution>(solutions.size(), false) ;

    List<DoubleSolution> selectedSolutions = selection.select(solutions) ;
    assertThat(selectedSolutions)
        .hasSize(numberOfSolutions)
        .containsExactlyInAnyOrderElementsOf(solutions) ;
  }

  @Test
  void selectWithReplacementWorksProperlyWhenTheListHasFiveSolutionsAndThreeSolutionsAreRequested() {
    List<DoubleSolution> solutions = new ArrayList<>() ;
    int numberOfSolutions = 5 ;
    IntStream.range(0, numberOfSolutions).forEach(i -> solutions.add(mock(DoubleSolution.class)));
    var selection = new RandomSelection<DoubleSolution>(solutions.size(), true) ;

    List<DoubleSolution> selectedSolutions = selection.select(solutions) ;
    assertThat(selectedSolutions)
        .hasSize(numberOfSolutions)
        .containsAnyElementsOf(solutions) ;
  }
}