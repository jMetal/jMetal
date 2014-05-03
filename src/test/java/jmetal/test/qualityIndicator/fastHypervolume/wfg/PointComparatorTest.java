package jmetal.test.qualityIndicator.fastHypervolume.wfg;

import jmetal.qualityIndicator.fastHypervolume.wfg.Point;
import jmetal.qualityIndicator.fastHypervolume.wfg.PointComparator;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;


/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 26/07/13
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public class PointComparatorTest {
  Point point1_ ;
  Point point2_ ;
  Comparator pointComparator_ ;

  @Before
  public void setUp() {
    point1_ = new Point(new double[]{1.0, 2.0}) ;
    point2_ = new Point(new double[]{1.5, 1.4}) ;
  }

  @Test
  public void compareTwoPointObjectsMaximizing() {
    boolean maximizing = true ;
    pointComparator_ = new PointComparator(maximizing) ;
    assertEquals("compareTwoPointObjects", -1, pointComparator_.compare(point1_, point2_));
    assertEquals("compareTwoPointObjects", 1, pointComparator_.compare(point2_, point1_));

    point1_ = new Point(new double[]{1.5, 1.4}) ;
    assertEquals("compareTwoPointObjects", 0, pointComparator_.compare(point1_, point2_));

  }

  @Test
  public void compareTwoPointObjectsMinimizing() {
    boolean maximizing = false ;
    pointComparator_ = new PointComparator(maximizing) ;
    assertEquals("compareTwoPointObjects", -1, pointComparator_.compare(point2_, point1_));
    assertEquals("compareTwoPointObjects", 1, pointComparator_.compare(point1_, point2_));

    point1_ = new Point(new double[]{1.5, 1.4}) ;
    assertEquals("compareTwoPointObjects", 0, pointComparator_.compare(point1_, point2_));
  }
}
