package org.uma.jmetal.component.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class EvolutionaryAlgorithmTest {

  @Test
  void TheConstructorInitializesTheComponents() throws Exception {
    var termination = mock(Termination.class) ;
    SolutionsCreation<DoubleSolution> solutionsCreation = mock(SolutionsCreation.class) ;
    Evaluation<DoubleSolution> evaluation = mock(Evaluation.class) ;
    Selection<DoubleSolution> selection = mock(Selection.class) ;
    Variation<DoubleSolution> variation = mock(Variation.class) ;
    Replacement<DoubleSolution> replacement = mock(Replacement.class) ;

    var evolutionaryAlgorithm = new EvolutionaryAlgorithm<DoubleSolution>(
        "EA", solutionsCreation, evaluation, termination, selection, variation, replacement) ;

    assertThat(evolutionaryAlgorithm.getResult()).isNullOrEmpty();
    assertThat(evolutionaryAlgorithm.getName()).isEqualTo("EA") ;
    assertThat(evolutionaryAlgorithm.getAttributes()).isEmpty();

    assertThat(solutionsCreation).isSameAs(ReflectionTestUtils.getField(evolutionaryAlgorithm, "createInitialPopulation")) ;
    assertThat(evaluation).isSameAs(ReflectionTestUtils.getField(evolutionaryAlgorithm, "evaluation")) ;
    assertThat(termination).isSameAs(ReflectionTestUtils.getField(evolutionaryAlgorithm, "termination")) ;
    assertThat(variation).isSameAs(ReflectionTestUtils.getField(evolutionaryAlgorithm, "variation")) ;
    assertThat(selection).isSameAs(ReflectionTestUtils.getField(evolutionaryAlgorithm, "selection")) ;
    assertThat(replacement).isSameAs(ReflectionTestUtils.getField(evolutionaryAlgorithm, "replacement")) ;
  }

}