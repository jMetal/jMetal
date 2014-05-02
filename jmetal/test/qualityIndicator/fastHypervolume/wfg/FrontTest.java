package qualityIndicator.fastHypervolume.wfg;

import jmetal.qualityIndicator.fastHypervolume.wfg.Front;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 26/07/13
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public class FrontTest {
  Front front_ ;

  @Before
  public void setUp() {
    List<double[]> list = new ArrayList<double[]>();
    list.add(new double[]{1, 2, 3}) ;
    list.add(new double[]{2, 2, 6}) ;
    list.add(new double[]{3, 1, 6}) ;

    front_ = new Front(list.size(), 2, list) ;
  }

  @Test
  public void sortMaximizingTest() {
    double epsilon = 0.0000000000001 ;
    front_.printFront();
    front_.sort();
    System.out.println("--------") ;
    front_.printFront();
    assertEquals(6, front_.getPoint(0).getObjectives()[2], epsilon) ;
    assertEquals(2, front_.getPoint(0).getObjectives()[1], epsilon) ;
    assertEquals(2, front_.getPoint(0).getObjectives()[0], epsilon) ;
  }


  @Test
  public void sortMinimizingTest() {
    front_.setToMinimize();
    double epsilon = 0.0000000000001 ;
    front_.printFront();
    front_.sort();
    System.out.println("--------") ;
    front_.printFront();
    assertEquals(3, front_.getPoint(0).getObjectives()[2], epsilon) ;
    assertEquals(2, front_.getPoint(0).getObjectives()[1], epsilon) ;
    assertEquals(1, front_.getPoint(0).getObjectives()[0], epsilon) ;
  }

}
