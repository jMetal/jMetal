package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Vector;

/**
 * Created by ajnebro on 5/11/14.
 */
public class ReferencePoint {
  public Vector<Double> position ;
  private int memberSize ;
  private Vector<Pair<Integer, Double>> potentialMembers ;

  /** Constructor */
  public ReferencePoint(int size) {
    position = new Vector<>(size) ;
    memberSize = 0 ;
  }

  public static void generateReferencePoints(
          Vector<ReferencePoint> referencePoints,
          int numberOfObjectives,
          Vector<Integer> numberOfDivisions) {

    ReferencePoint refPoint = new ReferencePoint(numberOfObjectives) ;
    generateRecursive(referencePoints, refPoint, numberOfObjectives, numberOfDivisions.get(0), numberOfDivisions.get(0), 0);
  }

  private static void generateRecursive(
          Vector<ReferencePoint> referencePoints,
          ReferencePoint refPoint,
          int numberOfObjectives,
          int left,
          int total,
          int element) {
    if (element == (numberOfObjectives - 1)) {
      refPoint.position.add(element, 0.0) ;
      refPoint.position.add(element, (double) left / total) ;
      referencePoints.add(refPoint) ;
    } else {
      for (int i = 0 ; i <= left; i +=1) {
        refPoint.position.add(element, 0.0) ;
        refPoint.position.set(element, (double)i/total) ;

        generateRecursive(referencePoints, refPoint, numberOfObjectives, left-i, total, element+1);
      }
    }
  }
}
