package org.uma.jmetal.util.distance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.distance.impl.CosineSimilarityBetweenVectors;

@DisplayName("Edge cases of CosineSimilarityBetweenVectors")
class CosineSimilarityEdgeCasesTest {

  @Test
  @DisplayName("given a vector that is the reference point, when computed, then the similarity is zero and not NaN")
  void givenAVectorAtTheReferencePoint_whenComputed_thenTheSimilarityIsZero() {
    var subject = new CosineSimilarityBetweenVectors(new double[] {0.0, 0.0});

    assertThat(subject.compute(new double[] {0.0, 0.0}, new double[] {1.0, 1.0})).isZero();
    assertThat(subject.compute(new double[] {1.0, 1.0}, new double[] {0.0, 0.0})).isZero();
    assertThat(subject.compute(new double[] {0.0, 0.0}, new double[] {0.0, 0.0})).isZero();
  }

  @Test
  @DisplayName("given vectors seen from a reference point, when computed, then it is the cosine of the angle")
  void givenVectorsAndAReferencePoint_whenComputed_thenItIsTheCosineOfTheAngle() {
    var subject = new CosineSimilarityBetweenVectors(new double[] {1.0, 1.0});

    // (1, 0) and (0, 1) seen from (1, 1) are (0, -1) and (-1, 0): orthogonal
    assertThat(subject.compute(new double[] {1.0, 0.0}, new double[] {0.0, 1.0})).isZero();
    // (3, 3) and (2, 2) seen from (1, 1) are (2, 2) and (1, 1): the same direction
    assertThat(subject.compute(new double[] {3.0, 3.0}, new double[] {2.0, 2.0}))
        .isEqualTo(1.0, org.assertj.core.data.Offset.offset(1e-12));
  }
}
