package org.uma.jmetal.qualityindicator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.Solution;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NormalizableQualityIndicatorTest {

  private MockIndicator indicator ;
  private String name = "shortID" ;
  private String description = "description" ;

  @Before public void setUp() throws Exception {
    indicator = new MockIndicator(name, description) ;
  }

  @After public void tearDown() throws Exception {
    indicator = null ;
  }

  @Test
  public void shouldObjectHaveTheCorrectIdAndDescription() {
    assertEquals(name, indicator.getName());
    assertEquals(description, indicator.getDescription());
  }

  @Test
  public void shouldSetNormalizeSetTheCorrectValue() {
    indicator.setNormalize(true) ;
    assertTrue((Boolean) ReflectionTestUtils.getField(indicator, "normalize")) ;

    indicator.setNormalize(false) ;
    assertFalse((Boolean) ReflectionTestUtils.getField(indicator, "normalize")) ;
  }

  @Test
  public void shouldGetNormalizedReturnTheCorrectValue() {
    ReflectionTestUtils.setField(indicator, "normalize", true) ;
    assertTrue(indicator.frontsNormalized()) ;

    ReflectionTestUtils.setField(indicator, "normalize", false) ;
    assertFalse(indicator.frontsNormalized()) ;
  }


  /**
   * Mock class
   */
  private class MockIndicator extends NormalizableQualityIndicator<List<? extends Solution<?>>,Double> {

    public MockIndicator(String shortName, String description) {
      super(shortName, description) ;
    }

    @Override public Double evaluate(List<? extends Solution<?>> solutions) {
      return null;
    }
  }
}
