package org.uma.jmetal.util.fileinput;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/***
 * @author sunhaoran
 */
class VectorFileUtilsTest {
  @Test
  void shouldReadVectorsWorkwithAWeightVectorFileLocatedInTheResourceFolder() {
    double[][] referenceVectors =
        VectorFileUtils.readVectors("../resources/weightVectorFiles/moead/W3D_300.dat");
    Assertions.assertNotNull(referenceVectors);
    Assertions.assertEquals(300, referenceVectors.length);
    Assertions.assertEquals(3, referenceVectors[0].length);
  }
}
