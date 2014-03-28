package test.util.wrapper;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.wrapper.XInt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
