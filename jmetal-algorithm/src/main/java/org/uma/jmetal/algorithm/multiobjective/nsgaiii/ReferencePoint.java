package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ajnebro on 5/11/14.
 * Modified by Juanjo Durillo on 13/11/14
 * This implementation is based on the code of Tsung-Che Chiang
 * http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm
 */
public class ReferencePoint {
  public List<Double> position ;
  private int memberSize ;
  private List<Pair<Solution, Double>> potentialMembers ;

  /** Constructor */
  public ReferencePoint(int size) {
	position = new ArrayList<>();
	for (int i =0; i < size; i++)
		position.add(0.0);
    memberSize = 0 ;
    potentialMembers = new ArrayList<>();
  }

  public ReferencePoint(ReferencePoint point) {
    position = new ArrayList<>(point.position.size());
    for (Double d : point.position) {
    	position.add(new Double(d));
    }
    memberSize = 0 ;
    potentialMembers = new ArrayList<>();    
  }
  
  
  public static void generateReferencePoints(
          List<ReferencePoint> referencePoints,
          int numberOfObjectives,
          List<Integer> numberOfDivisions) {

    ReferencePoint refPoint = new ReferencePoint(numberOfObjectives) ;
    generateRecursive(referencePoints, refPoint, numberOfObjectives, numberOfDivisions.get(0), numberOfDivisions.get(0), 0);
  }

  private static void generateRecursive(
          List<ReferencePoint> referencePoints,
          ReferencePoint refPoint,
          int numberOfObjectives,
          int left,
          int total,
          int element) {
    if (element == (numberOfObjectives - 1)) {
      refPoint.position.set(element, (double) left / total) ;
      referencePoints.add(new ReferencePoint(refPoint)) ;
    } else {
      for (int i = 0 ; i <= left; i +=1) {
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
  public void AddPotentialMember(Solution member_ind, double distance){
	  this.potentialMembers.add(new ImmutablePair<Solution,Double>(member_ind,distance) );
  }
  public Solution FindClosestMember() {
		double min_dist = Double.MAX_VALUE;
		Solution min_indv = null;
		for (Pair<Solution,Double> p : this.potentialMembers)
		{
			if ( p.getRight() < min_dist)
			{
				min_dist = p.getRight();
				min_indv = p.getLeft();
			}
		}

		return min_indv;
  }
  
  public Solution RandomMember() {
	int index = this.potentialMembers.size()>1 ? JMetalRandom.getInstance().nextInt(0, this.potentialMembers.size()-1):0;
	return this.potentialMembers.get(index).getLeft();
  }
  
  public void RemovePotentialMember(Solution solution) {
	  Iterator<Pair<Solution, Double>> it = this.potentialMembers.iterator();
	  while (it.hasNext()) 
		  if (it.next().getLeft().equals(solution)) 
			  it.remove();
	  
  }

 
  
}
