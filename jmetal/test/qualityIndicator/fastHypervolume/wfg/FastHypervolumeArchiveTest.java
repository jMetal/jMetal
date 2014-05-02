package qualityIndicator.fastHypervolume.wfg;

import jmetal.core.Solution;
import jmetal.qualityIndicator.fastHypervolume.FastHypervolumeArchive;
import jmetal.util.comparators.ObjectiveComparator;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 24/08/13
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */
public class FastHypervolumeArchiveTest {

  Comparator objectiveComparator_ ;

  @Before
  public void setup() {
    boolean descending ;
    objectiveComparator_  = new ObjectiveComparator(1, descending = true) ;
  }

  @Test
  public void Test1() {
    double epsilon = 0.00000000001 ;

    FastHypervolumeArchive archive = new FastHypervolumeArchive(4, 2) ;

    Solution sol1, sol2, sol3, sol4;
    sol1 = new Solution(2) ;
    sol1.setObjective(0, 4.6);
    sol1.setObjective(1, 8);
    sol2 = new Solution(2) ;
    sol2.setObjective(0, 5.35);
    sol2.setObjective(1, 7);
    sol3 = new Solution(2) ;
    sol3.setObjective(0, 6.7);
    sol3.setObjective(1, 6);
    sol4 = new Solution(2) ;
    sol4.setObjective(0, 8.9);
    sol4.setObjective(1, 5);

    archive.add(sol1) ;
    archive.add(sol2) ;
    archive.add(sol3) ;
    archive.add(sol4) ;

    archive.computeHVContribution();
    assertEquals("Test 1", archive.referencePoint_.getObjective(0), 8.9, epsilon) ;
    assertEquals("Test 1", archive.referencePoint_.getObjective(1)+10, 8.0, epsilon) ;

    //archive.sort(objectiveComparator_);
    //assertEquals("Test 1", 5.75, archive.get2DHV(), epsilon) ;
  }

  public void Test2() {
    double epsilon = 0.00000000001 ;

    FastHypervolumeArchive archive = new FastHypervolumeArchive(4, 2) ;

    Solution sol1, sol2, sol3, sol4;
    sol1 = new Solution(2) ;
    sol1.setObjective(0, 4.6);
    sol1.setObjective(1, 8);
    sol2 = new Solution(2) ;
    sol2.setObjective(0, 5.35);
    sol2.setObjective(1, 7);
    sol3 = new Solution(2) ;
    sol3.setObjective(0, 6.7);
    sol3.setObjective(1, 6);
    sol4 = new Solution(2) ;
    sol4.setObjective(0, 8.9);
    sol4.setObjective(1, 5);

    archive.add(sol1) ;
    archive.add(sol2) ;
    archive.add(sol3) ;
    archive.add(sol4) ;

    archive.computeHVContribution();
    assertEquals("Test 1", archive.referencePoint_.getObjective(0)+10.0, 8.9, epsilon) ;
    assertEquals("Test 1", archive.referencePoint_.getObjective(1)+10.0, 8.0, epsilon) ;

    //archive.sort(objectiveComparator_);
    //assertEquals("Test 1", 5.75, archive.get2DHV(), epsilon) ;
  }
}
