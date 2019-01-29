package org.uma.jmetal.util.fileinput.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * created at 下午3:54, 2019/1/29
 *
 * @author sunhaoran
 */
public class ReadReferenceVectorsUtilsTest {
    @Test
    public void testReadFile() throws IOException {
        double[][] refvectors = ReadReferenceVectorsUtils.readReferenceVectors("MOEAD_Weights/W3D_300.dat") ;
        Assert.assertNotNull(refvectors);
        Assert.assertEquals(300, refvectors.length);
        Assert.assertEquals(3, refvectors[0].length);
    }
}
