package org.uma.jmetal.qualityindicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.GeneralizedSpread;

public class GeneralizedSpreadTest {
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
    double[][] copyOfTheReferenceFront = referenceFront.clone();
    double[][] front = shuffledFront(50, 0.05, 2L);

    new GeneralizedSpread(referenceFront).compute(front);

    for (int i = 0; i < referenceFront.length; i++) {
      Assertions.assertSame(copyOfTheReferenceFront[i], referenceFront[i], "row " + i + " moved");
    }
  }

  @Test
  public void shouldComputeNotReorderTheFront() {
    double[][] referenceFront = shuffledFront(100, 0.0, 1L);
    double[][] front = shuffledFront(50, 0.05, 2L);
    double[][] copyOfTheFront = front.clone();

    new GeneralizedSpread(referenceFront).compute(front);

    for (int i = 0; i < front.length; i++) {
      Assertions.assertSame(copyOfTheFront[i], front[i], "row " + i + " moved");
    }
  }

  @Test
  public void shouldComputeGiveTheSameValueWhenAnInstanceIsSharedByParallelComputations() {
    double[][] referenceFront = shuffledFront(2000, 0.0, 3L);
    GeneralizedSpread shared = new GeneralizedSpread(referenceFront);
    double expected = new GeneralizedSpread(referenceFront.clone()).compute(shuffledFront(300, 0.05, 4L));

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
