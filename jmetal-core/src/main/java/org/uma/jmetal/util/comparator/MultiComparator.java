package org.uma.jmetal.util.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Juanjo
 * @version 1.0
 * This class aims to implement an interface for MultiComparators. Multicomparators are
 * comparators that underneath make use of a list of comparator to possibly break the ties between
 * solutions
 */
public class MultiComparator<T> implements Comparator<T> {

  protected List<Comparator<T>> comparatorList;

  /**
   * Constructor
   * @param comparatorList
   */
  public MultiComparator(List<Comparator<T>> comparatorList) {
    this.comparatorList = comparatorList ;
  }

  public MultiComparator() {
    comparatorList = new ArrayList<>() ;
  }

  public MultiComparator<T> add(Comparator<T> comparator) {
    comparatorList.add(comparator) ;
    return this ;
  }

  /**
   * Compare two objects based on a list of comparators. It performs a lexicographical comparison as
   * follows:
   *
   * o1 is smaller than o2 if exist an index i, within the interval [0,comparatorList.size()) s.t.
   * comparatorList.get(i).compare(o1,o2)==-1 and for all j, s.t. j<i, then
   * comparatorList.get(j).compare(o1,o2)==0.
   *
   * Conversely, o1 is bigger than o2 if exist an index i, within the interval
   * [0,comparatorList.size()) s.t. comparatorList.get(i).compare(o1,o2)==1 and for all j, s.t. j<i,
   * then comparatorList.get(j).compare(o1,o2)==0.
   *
   * Finally o1 cannot be said smaller or bigger than o2 if none of the previous two cases are
   * true.
   *
   * @param o1 the first element to compare
   * @param o2 the second element to compare
   * @return -1 if o1 is smaller than o2, 1 if o2 is bigger than o2, and 0 otherwise
   */
  public int compare(T o1, T o2) {
    for (Comparator<T> comparator : comparatorList) {
      int flag = comparator.compare(o1, o2);
      if (flag == 0) {
        continue;
      }
      return flag;
    }
    return 0;
  }

  /**
   * @return the list of comparators
   */
  public List<? extends Comparator<T>> getComparatorList() {
    return comparatorList ;
  }
}
