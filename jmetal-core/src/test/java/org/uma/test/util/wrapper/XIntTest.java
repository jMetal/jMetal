package org.uma.test.util.wrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XInt;

/**
 * Created by Antonio J. Nebro on 28/03/14.
 */
public class XIntTest {
  XInt xint_ ;

  @Before
  public void setUp() throws ClassNotFoundException {
    Solution solution ;
    Problem problem ;
  }

  @After
  public void tearDown() throws Exception {
    xint_ = null ;
  }

  @Test
  public void testConstructor() {
    //assertEquals("XIntTest.testConstructor", IntRealSolutionType.class, xint_.type_.getClass()) ;
  }
}
