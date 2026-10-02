package org.uma.jmetal.qualityindicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.Spread;

public class SpreadTest {
  private static final double EPSILON = 1.0e-12;

  /** A bi-objective front f2 = 1 - sqrt(f1), with its points in random order. */
  private static double[][] shuffledFront(int numberOfPoints, double shift, long seed) {
    List<double[]> points = new ArrayList<>();
    for (int i = 0; i < numberOfPoints; i++) {
      double f1 = (double) i / (numberOfPoints - 1);
      points.add(new double[] {f1, 1.0 - Math.sqrt(f1) + shift});
    }
    Collections.shuffle(points, new Random(seed));
    return points.toArray(new double[0][]);
  }

  @Test
  public void shouldComputeNotReorderTheReferenceFront() {
    double[][] referenceFront = shuffledFront(100, 0.0, 1L);
    double[][] copyOfTheReferenceFront = new double[referenceFront.length][];
    for (int i = 0; i < referenceFront.length; i++) {
      copyOfTheReferenceFront[i] = referenceFront[i];
    }
    double[][] front = shuffledFront(50, 0.05, 2L);

    new Spread(referenceFront).compute(front);

    for (int i = 0; i < referenceFront.length; i++) {
      Assertions.assertSame(copyOfTheReferenceFront[i], referenceFront[i], "row " + i + " moved");
    }
  }

  @Test
  public void shouldComputeGiveTheSameValueWhenAnInstanceIsSharedByParallelComputations() {
    double[][] referenceFront = shuffledFront(2000, 0.0, 3L);
    Spread shared = new Spread(referenceFront);
    double expected = new Spread(referenceFront.clone()).compute(shuffledFront(300, 0.05, 4L));

    double[] values =
        IntStream.range(0, 200)
            .parallel()
            .mapToDouble(i -> shared.compute(shuffledFront(300, 0.05, 4L)))
            .toArray();

    for (double value : values) {
      Assertions.assertEquals(expected, value, EPSILON);
    }
  }
}
