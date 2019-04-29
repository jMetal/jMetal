package org.uma.jmetal.util.fileinput;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * created at 下午3:54, 2019/1/29
 *
 * @author sunhaoran
 */
public class VectorFileUtilsTest {
  @Test
  public void shouldReadVectorsWorkwithAWeightVectorFileLocatedInTheResourceFolder() {
    double[][] referenceVectors =
        VectorFileUtils.readVectors("MOEAD_Weights/W3D_300.dat");
    Assert.assertNotNull(referenceVectors);
    Assert.assertEquals(300, referenceVectors.length);
    Assert.assertEquals(3, referenceVectors[0].length);
  }
}
