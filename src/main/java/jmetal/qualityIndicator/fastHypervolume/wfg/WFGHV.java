//  WFGHV.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro, Juan J. Durillo
//
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
//

//  CREDIT
//  This class is based on the code of the WFG group (http://www.wfg.csse.uwa.edu.au/hypervolume/)
//  Copyright (C) 2010 Lyndon While, Lucas Bradstreet.


package jmetal.qualityIndicator.fastHypervolume.wfg;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 25/07/13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
public class WFGHV {
  Front [] fs_ ;
  Point referencePoint_ ;
  boolean maximizing_  ;
  int currentDeep_  ;
  int currentDimension_ ;
  int maxNumberOfPoints_ ;
  int maxNumberOfObjectives_ ;
  final int OPT = 2 ;
  Comparator pointComparator_;

  public WFGHV(int dimension, int maxNumberOfPoints) {
    referencePoint_ = null ;
    maximizing_ = false ;
    currentDeep_ = 0 ;
    currentDimension_ = dimension ;
    maxNumberOfPoints_ = maxNumberOfPoints ;
    maxNumberOfObjectives_ = dimension ;
    pointComparator_ = new PointComparator(true) ;

    int maxd = maxNumberOfPoints_ - (OPT /2 + 1) ;
    fs_ = new Front[maxd] ;
    for (int i = 0; i < maxd; i++) {
      fs_[i] = new Front(maxNumberOfPoints, dimension) ;
    }
  }

  public WFGHV(int dimension, int maxNumberOfPoints, Solution referencePoint) {
    referencePoint_ = new Point(referencePoint) ;
    maximizing_ = false ;
    currentDeep_ = 0 ;
    currentDimension_ = dimension ;
    maxNumberOfPoints_ = maxNumberOfPoints ;
    maxNumberOfObjectives_ = dimension ;
    pointComparator_ = new PointComparator(true) ;

    int maxd = maxNumberOfPoints_ - (OPT /2 + 1) ;
    fs_ = new Front[maxd] ;
    for (int i = 0; i < maxd; i++) {
      fs_[i] = new Front(maxNumberOfPoints, dimension) ;
    }
  }

  public WFGHV(int dimension, int maxNumberOfPoints, Point referencePoint) {
    referencePoint_ = referencePoint ;
    maximizing_ = false ;
    currentDeep_ = 0 ;
    currentDimension_ = dimension ;
    maxNumberOfPoints_ = maxNumberOfPoints ;
    maxNumberOfObjectives_ = dimension ;
    pointComparator_ = new PointComparator(true) ;

    int maxd = maxNumberOfPoints_ - (OPT /2 + 1) ;
    fs_ = new Front[maxd] ;
    for (int i = 0; i < maxd; i++) {
      fs_[i] = new Front(maxNumberOfPoints, dimension) ;
    }
  }

  public int getLessContributorHV(SolutionSet set) {

    Front wholeFront   = new Front();

    wholeFront.loadFront(set, -1);

    int index= 0;
    double contribution = Double.POSITIVE_INFINITY;

    for (int i = 0; i < set.size(); i++) {
      double [] v = new double[set.get(i).getNumberOfObjectives()];
      for (int j = 0; j < v.length; j++){
        v[j] = set.get(i).getObjective(j);
      }
      Point p = new Point(v);
      double aux = this.getExclusiveHV(wholeFront, i);
      if ((aux) < contribution) {
        index = i;
        contribution = aux;
      }
      set.get(i).setCrowdingDistance(aux);
    }

    return index;
  }

  public double getHV(Front front, Solution referencePoint) {
    referencePoint_ = new Point(referencePoint) ;
    double volume = 0.0 ;
    sort(front) ;

    if (currentDimension_ == 2)
      volume = get2DHV(front) ;
    else {
      volume = 0.0 ;

      currentDimension_ -- ;
      for (int i = front.nPoints_-1; i >= 0; i--) {
        volume += Math.abs(front.getPoint(i).objectives_[currentDimension_] -
                referencePoint_.objectives_[currentDimension_])*
                this.getExclusiveHV(front, i) ;
      }
      currentDimension_ ++ ;
    }

    return volume ;
  }

  public double getHV(Front front) {
    double volume = 0.0 ;
    sort(front) ;

    if (currentDimension_ == 2)
      volume = get2DHV(front) ;
    else {
      volume = 0.0 ;

      currentDimension_ -- ;
      for (int i = front.nPoints_-1; i >= 0; i--) {
        volume += Math.abs(front.getPoint(i).objectives_[currentDimension_] -
                referencePoint_.objectives_[currentDimension_])*
                this.getExclusiveHV(front, i) ;
      }
      currentDimension_ ++ ;
    }

    return volume ;
  }

  public double get2DHV(Front front) {
    double hv = 0.0 ;

    hv = Math.abs((front.getPoint(0).getObjectives()[0] - referencePoint_.objectives_[0]) *
            (front.getPoint(0).getObjectives()[1] - referencePoint_.objectives_[1])) ;

    for (int i = 1; i < front.nPoints_; i++) {
      hv += Math.abs((front.getPoint(i).getObjectives()[0] - referencePoint_.objectives_[0]) *
              (front.getPoint(i).getObjectives()[1] - front.getPoint(i-1).getObjectives()[1])) ;

    }

    return hv ;

  }

  public double getInclusiveHV(Point p) {
    double volume = 1 ;
    for (int i = 0; i < currentDimension_; i++) {
      volume *= Math.abs(p.objectives_[i] - referencePoint_.objectives_[i]) ;
    }

    return volume ;
  }

  public double getExclusiveHV(Front front, int point) {
    double volume ;

    volume = getInclusiveHV(front.getPoint(point)) ;
    if (front.nPoints_ > point + 1) {
      makeDominatedBit(front, point);
      double v = getHV(fs_[currentDeep_-1]) ;
      volume -= v ;
      currentDeep_ -- ;
    }

    return volume ;
  }

  public void makeDominatedBit(Front front, int p) {
    int z = front.nPoints_ - 1 - p ;

    for (int i = 0 ; i < z ; i++)
      for (int j = 0 ; j < currentDimension_; j++) {
        fs_[currentDeep_].getPoint(i).objectives_[j] = worse(front.points_[p].objectives_[j], front.points_[p+1+i].objectives_[j], false) ;
      }


    Point t ;
    fs_[currentDeep_].nPoints_ = 1 ;

    for (int i = 1; i < z; i++) {
      int j = 0 ;
      boolean keep = true ;
      while (j < fs_[currentDeep_].nPoints_ && keep) {
        switch (dominates2way(fs_[currentDeep_].points_[i], fs_[currentDeep_].points_[j])) {
          case -1:
            t = fs_[currentDeep_].points_[j] ;
            fs_[currentDeep_].nPoints_--;
            fs_[currentDeep_].points_[j] = fs_[currentDeep_].points_[fs_[currentDeep_].nPoints_];
            fs_[currentDeep_].points_[fs_[currentDeep_].nPoints_] = t;
            break;
          case  0: j++; break;
          // case  2: printf("Identical points!\n");
          default: keep = false;
        }
      }
      if (keep) {t = fs_[currentDeep_].points_[fs_[currentDeep_].nPoints_];
        fs_[currentDeep_].points_[fs_[currentDeep_].nPoints_] = fs_[currentDeep_].points_[i];
        fs_[currentDeep_].points_[i] = t;
        fs_[currentDeep_].nPoints_++;
      }
    }

    currentDeep_++ ;
  }

  private double worse (double x, double y, boolean maximizing) {
    double result ;
    if (maximizing) {
      if (x > y)
        result = y ;
      else
        result = x ;
    }
    else {
      if (x > y)
        result = x ;
      else
        result = y ;
    }
    return result ;
  }

  int dominates2way(Point p, Point q)
// returns -1 if p dominates q, 1 if q dominates p, 2 if p == q, 0 otherwise
  // ASSUMING MINIMIZATION
  {
    // domination could be checked in either order

    for (int i = currentDimension_ - 1; i >= 0; i--)
      if (p.objectives_[i] < q.objectives_[i]){
        for (int j = i - 1; j >= 0; j--)
          if (q.objectives_[j] < p.objectives_[j]) return 0;
        return -1;
      }
      else
      if (q.objectives_[i] < p.objectives_[i]){
        for (int j = i - 1; j >= 0; j--)
          if (p.objectives_[j] < q.objectives_[j]) return 0;
        return  1;
      }
    return 2;
  }

  public void sort(Front front) {
    Arrays.sort(front.points_, 0, front.nPoints_, pointComparator_);
  }

  public static void main(String args[]) throws IOException {
    Front front = new Front() ;

    if (args.length == 0) {
      System.out.println("Usage: WFGHV front [reference point]") ;
      System.exit(-1) ;
    }

    if (args.length > 0) {
      front.readFront(args[0]);
    }

    int dimensions = front.getNumberOfObjectives() ;
    Point referencePoint ;
    double [] points = new double[dimensions] ;

    if (args.length == (dimensions + 1)) {
       for (int i = 1; i <= dimensions; i++)
         points[i-1] = Double.parseDouble(args[i]) ;
    }
    else {
      for (int i = 1; i <= dimensions; i++)
        points[i-1] = 0.0 ;
    }

    referencePoint = new Point(points) ;
    System.out.println("Using reference point: " + referencePoint) ;

    WFGHV wfghv = new WFGHV(referencePoint.getNumberOfObjectives(), front.getNumberOfPoints(), referencePoint) ;

    System.out.println("hv = " + wfghv.getHV(front)) ;
  }

}
