package test.util.wrapper;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutiontype.RealSolutionType;
import jmetal.problems.Kursawe;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 28/03/14.
 */
public class XRealTest {
  XReal xreal_ ;
  Solution solution_ ;
  int problemVariables_ ;

  @Before
  public void setUp() throws ClassNotFoundException, JMException {
    Problem problem ;
    problemVariables_ = 5 ;

    problem = new Kursawe("Real", problemVariables_) ;
    solution_ = new Solution(problem) ;
    xreal_ = new XReal(solution_) ;
  }

  @After
  public void tearDown() throws Exception {
    xreal_ = null ;
  }

  @Test
  public void testConstructor() {
    assertEquals("XRealTest.testConstructor", RealSolutionType.class, xreal_.getType_().getClass()) ;
  }

  @Test
  public void testCopyConstructor() {
    XReal xreal2 = new XReal(xreal_) ;
    assertEquals("XRealTest.testCopyConstructor", xreal_.getType_().getClass(), xreal2.getType_().getClass()) ;
    assertEquals("XRealTest.testCopyConstructor", xreal_.size(), xreal2.size()) ;
  }

  @Test
  public void testCopyConstructor2() {
    XReal xreal2 = new XReal(solution_) ;
    assertEquals("XRealTest.testCopyConstructor", xreal_.getType_().getClass(), xreal2.getType_().getClass()) ;
    assertEquals("XRealTest.testCopyConstructor", xreal_.size(), xreal2.size()) ;
  }

  @Test
  public void testSize() {
    assertEquals("XRealTest.testConstructor", problemVariables_, xreal_.size()) ;
  }
}
