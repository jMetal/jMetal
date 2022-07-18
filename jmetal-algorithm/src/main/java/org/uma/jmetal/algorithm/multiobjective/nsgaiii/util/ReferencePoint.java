package org.uma.jmetal.algorithm.multiobjective.nsgaiii.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Created by ajnebro on 5/11/14.
 * Modified by Juanjo on 13/11/14
 * This implementation is based on the code of Tsung-Che Chiang
 * http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm
 */
public class ReferencePoint<S extends Solution<?>> {
  public List<Double> position ;
  private int memberSize ;
  private List<Pair<S, Double>> potentialMembers ;

  public ReferencePoint() {
  }

  /** Constructor */
  public ReferencePoint(int size) {
    position = new ArrayList<>();
    for (var i = 0; i < size; i++)
      position.add(0.0);
    memberSize = 0 ;
    potentialMembers = new ArrayList<>();
  }

  public ReferencePoint(ReferencePoint<S> point) {
    position = new ArrayList<>(point.position.size());
    for (var d : point.position) {
      position.add(d);
    }
    memberSize = 0 ;
    potentialMembers = new ArrayList<>();
  }

  public void generateReferencePoints(
          @NotNull List<ReferencePoint<S>> referencePoints,
          int numberOfObjectives,
          int numberOfDivisions) {

    @NotNull ReferencePoint<S> refPoint = new ReferencePoint<>(numberOfObjectives) ;
    generateRecursive(referencePoints, refPoint, numberOfObjectives, numberOfDivisions, numberOfDivisions, 0);
  }

  private void generateRecursive(
          List<ReferencePoint<S>> referencePoints,
          @NotNull ReferencePoint<S> refPoint,
          int numberOfObjectives,
          int left,
          int total,
          int element) {
    if (element == (numberOfObjectives - 1)) {
      refPoint.position.set(element, (double) left / total) ;
      referencePoints.add(new ReferencePoint<>(refPoint)) ;
    } else {
      for (var i = 0; i <= left; i +=1) {
        refPoint.position.set(element, (double)i/total) ;

        generateRecursive(referencePoints, refPoint, numberOfObjectives, left-i, total, element+1);
      }
    }
  }
  
  public List<Double> pos()  { return this.position; }
  public int  MemberSize(){ return memberSize; }
  public boolean HasPotentialMember() { return potentialMembers.size()>0; }
  public void clear(){ memberSize=0; this.potentialMembers.clear();}
  public void AddMember(){this.memberSize++;}
  public void AddPotentialMember(S member_ind, double distance){
    this.potentialMembers.add(new ImmutablePair<S,Double>(member_ind,distance) );
  }

  public void sort() {
    this.potentialMembers.sort(Comparator.comparing(Pair<S, Double>::getRight).reversed());
  }

  public S FindClosestMember() {
    return this.potentialMembers.remove(this.potentialMembers.size() - 1)
            .getLeft();
  }
  
  public S RandomMember() {
    var index = this.potentialMembers.size()>1 ? JMetalRandom.getInstance().nextInt(0, this.potentialMembers.size()-1):0;
    return this.potentialMembers.remove(index).getLeft();
  }
}
