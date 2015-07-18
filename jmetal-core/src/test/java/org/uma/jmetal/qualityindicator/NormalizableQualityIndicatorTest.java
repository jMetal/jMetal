//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
    indicator.setNormalize() ;
    assertTrue((Boolean) ReflectionTestUtils.getField(indicator, "normalize")) ;

    indicator.unsetNormalize() ;
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
