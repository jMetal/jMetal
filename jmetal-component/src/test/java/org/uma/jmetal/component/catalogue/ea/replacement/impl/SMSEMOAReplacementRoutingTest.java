package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

class SMSEMOAReplacementRoutingTest {
  @Test
  void shouldUseSpecializedExactPathForThreeObjectives() {
    SMSEMOAReplacement<DoubleSolution> replacement = createReplacement();
    assertTrue(replacement.usesSpecializedExact3DPath(3));
  }

  @Test
  void shouldNotUseSpecializedExactPathOutsideThreeObjectives() {
    SMSEMOAReplacement<DoubleSolution> replacement = createReplacement();
    assertFalse(replacement.usesSpecializedExact3DPath(2));
    assertFalse(replacement.usesSpecializedExact3DPath(4));
  }

  private SMSEMOAReplacement<DoubleSolution> createReplacement() {
    return new SMSEMOAReplacement<>(
        new FastNonDominatedSortRanking<>(),
        new WFGHypervolume(new double[] {1.0, 1.0, 1.0}));
  }
}
