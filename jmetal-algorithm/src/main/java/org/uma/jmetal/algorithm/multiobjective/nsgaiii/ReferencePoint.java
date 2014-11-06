package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Vector;

/**
 * Created by ajnebro on 5/11/14.
 * This implementation is based on the code of Tsung-Che Chiang
 * http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm
 */
public class ReferencePoint {
  public double[] position ;
  private int memberSize ;
  private Vector<Pair<Integer, Double>> potentialMembers ;

  /** Constructor */
  public ReferencePoint(int size) {
    position = new double[size] ;
    memberSize = 0 ;
  }

  public ReferencePoint(ReferencePoint point) {
    position = Arrays.copyOfRange(point.position, 0, point.position.length) ;
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
      refPoint.position[element] = (double) left / total ;
      referencePoints.add(new ReferencePoint(refPoint)) ;
    } else {
      for (int i = 0 ; i <= left; i +=1) {
        refPoint.position[element] = (double)i/total ;

        generateRecursive(referencePoints, refPoint, numberOfObjectives, left-i, total, element+1);
      }
    }
  }
}
