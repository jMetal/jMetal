package org.uma.jmetal.util.fileinput;

import org.junit.Assert;
import org.junit.Test;

/**
 * created at 下午3:54, 2019/1/29
 *
 * @author sunhaoran
 */
public class VectorFileUtilsTest {
  @Test
  public void shouldReadVectorsWorkwithAWeightVectorFileLocatedInTheResourceFolder() {
    var referenceVectors =
        VectorFileUtils.readVectors("../resources/weightVectorFiles/moead/W3D_300.dat");
    Assert.assertNotNull(referenceVectors);
    Assert.assertEquals(300, referenceVectors.length);
    Assert.assertEquals(3, referenceVectors[0].length);
  }
}
