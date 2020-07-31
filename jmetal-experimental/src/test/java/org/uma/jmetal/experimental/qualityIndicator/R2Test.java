package org.uma.jmetal.experimental.qualityIndicator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.qualityIndicator.impl.R2;

import java.io.IOException;

public class R2Test {

  String name = "R2";
  String description = "R2 quality indicator";

  @Test
  public void testR2HasProperNameAndDescriptionWithEmptyConstructor() {
    R2 r2 = new R2();
    Assertions.assertEquals(name, r2.getName());
    Assertions.assertEquals(description, r2.getDescription());
  }

  @Test
  public void testR2HasProperNameAndDescriptionWithVectorConstructor() {
    int nVectors = 10;
    R2 r2 = new R2(nVectors);
    Assertions.assertEquals(name, r2.getName());
    Assertions.assertEquals(description, r2.getDescription());
  }

  @Test
  public void testR2HasProperNameAndDescriptionWithFrontConstructor() {
    R2 r2 = new R2(new double[][] {});
    Assertions.assertEquals(name, r2.getName());
    Assertions.assertEquals(description, r2.getDescription());
  }

  @Test
  public void testR2HasProperNameAndDescriptionWithVectorFrontConstructor() {
    int nVectors = 10;
    R2 r2 = new R2(nVectors, new double[][] {});
    Assertions.assertEquals(name, r2.getName());
    Assertions.assertEquals(description, r2.getDescription());
  }

  @Test
  public void testR2HasProperNameAndDescriptionWithFileConstructor() throws IOException {
    String file = "src/test/resources/lambda/fake10x2Lambda.txt";
    R2 r2 = new R2(file);
    Assertions.assertEquals(name, r2.getName());
    Assertions.assertEquals(description, r2.getDescription());
  }

  @Test
  public void testR2HasProperNameAndDescriptionWithFileFrontConstructor() throws IOException {
    String file = "src/test/resources/lambda/fake10x2Lambda.txt";
    R2 r2 = new R2(file, new double[][] {});
    Assertions.assertEquals(name, r2.getName());
    Assertions.assertEquals(description, r2.getDescription());
  }
}
