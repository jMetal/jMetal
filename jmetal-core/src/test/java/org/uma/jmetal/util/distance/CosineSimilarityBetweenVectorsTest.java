package org.uma.jmetal.util.distance;

import static org.assertj.core.api.Assertions.withPrecision;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.distance.impl.CosineSimilarityBetweenVectors;

class CosineSimilarityBetweenVectorsTest {
  private static final double EPSILON = 0.00000000001 ;

  @Test
  void shouldIdenticalPointsHaveADistanceOfOne() {
    CosineSimilarityBetweenVectors distance = new CosineSimilarityBetweenVectors(new double[]{0.0, 0.0}) ;

    double receivedValue = distance.compute(new double[]{1.0, 1.0}, new double[]{1.0, 1.0}) ;
    assertThat(receivedValue).isEqualTo(1.0, withPrecision(EPSILON));
  }

  @Test
  void shouldPointsInTheSameDirectionHaveADistanceOfOne() {
    CosineSimilarityBetweenVectors distance = new CosineSimilarityBetweenVectors(new double[]{0.0, 0.0}) ;

    double receivedValue = distance.compute(new double[]{1.0, 1.0}, new double[]{2.0, 2.0}) ;
    assertThat(receivedValue).isEqualTo(1.0, withPrecision(EPSILON));
  }

  @Test
  void shouldTwoPerpendicularPointsHaveADistanceOfZero() {
    CosineSimilarityBetweenVectors distance = new CosineSimilarityBetweenVectors(new double[]{0.0, 0.0}) ;

    double receivedValue = distance.compute(new double[]{0.0, 1.0}, new double[]{1.0, 0.0}) ;
    assertThat(receivedValue).isEqualTo(0.0, withPrecision(EPSILON));
  }
}