//  Spea2Fitness.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package jmetal.util;

import jmetal.core.SolutionSet;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.FitnessComparator;

import java.util.*;

/**
 * This class implements some facilities for calculating the Spea2 fitness
 */
public class Spea2Fitness {
    
  /**
   * Stores the distance between solutions
   */
  private double [][] distance = null;
  
  /**
   * Stores the solutionSet to assign the fitness
   */
  private SolutionSet solutionSet_ = null;
  
  /** 
   * stores a <code>Distance</code> object
   */
  private static final Distance distance_ = new Distance();
  
  /**
   * stores a <code>Comparator</code> for distance between nodes checking
   */
  private static final Comparator distanceNodeComparator = new DistanceNodeComparator();      
  
  /**
   * stores a <code>Comparator</code> for dominance checking
   */
  private static final Comparator dominance_ = new DominanceComparator();
  
  /** 
   * Constructor.
   * Creates a new instance of Spea2Fitness for a given <code>SolutionSet</code>.
   * @param solutionSet The <code>SolutionSet</code>
   */
  public Spea2Fitness(SolutionSet solutionSet) {
    distance     = distance_.distanceMatrix(solutionSet);        
    solutionSet_ = solutionSet;
    for (int i = 0; i < solutionSet_.size(); i++) {
      solutionSet_.get(i).setLocation(i);
    } // for
  } // Spea2Fitness
    
    
  /** 
   * Assigns fitness for all the solutions.
   */
  public void fitnessAssign() {
    double [] strength    = new double[solutionSet_.size()];
    double [] rawFitness  = new double[solutionSet_.size()];
    double kDistance                                          ;
  
    
    //Calculate the strength value
    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (int i = 0; i < solutionSet_.size(); i++) {
      for (int j = 0; j < solutionSet_.size();j++) {
        if (dominance_.compare(solutionSet_.get(i),solutionSet_.get(j))==-1) {
          strength[i] += 1.0;
        } // if        
      } // for
    } // for
       
        
    //Calculate the raw fitness
    // rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
    for (int i = 0;i < solutionSet_.size(); i++) {
      for (int j = 0; j < solutionSet_.size();j++) {
        if (dominance_.compare(solutionSet_.get(i),solutionSet_.get(j))==1) {
          rawFitness[i] += strength[j];
        } // if
      } // for
    } // for
       
        
    // Add the distance to the k-th individual. In the reference paper of SPEA2, 
    // k = sqrt(population.size()), but a value of k = 1 recommended. See
    // http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
    int k = 1 ;
    for (int i = 0; i < distance.length; i++) {
      Arrays.sort(distance[i]);
      kDistance = 1.0 / (distance[i][k] + 2.0); // Calcule de D(i) distance
      //population.get(i).setFitness(rawFitness[i]);
      solutionSet_.get(i).setFitness(rawFitness[i] + kDistance);           
    } // for                  
  } // fitnessAsign
    
    
  /** 
  *  Gets 'size' elements from a population of more than 'size' elements
  *  using for this de enviromentalSelection truncation
  *  @param size The number of elements to get.
  */
  public SolutionSet environmentalSelection(int size){        
    
    if (solutionSet_.size() < size) {
      size = solutionSet_.size();
    }
        
    // Create a new auxiliar population for no alter the original population
    SolutionSet aux = new SolutionSet(solutionSet_.size());
                
    int i = 0;
    while (i < solutionSet_.size()){
      if (solutionSet_.get(i).getFitness()<1.0){
        aux.add(solutionSet_.get(i));
        solutionSet_.remove(i);
      } else {
        i++;
      } // if
    } // while
                
    if (aux.size() < size){
      Comparator comparator = new FitnessComparator();
      solutionSet_.sort(comparator);
      int remain = size - aux.size();
      for (i = 0; i < remain; i++){
        aux.add(solutionSet_.get(i));
      }
      return aux;
    } else if (aux.size() == size) {
      return aux;            
    }        
                                                
    double [][] distance = distance_.distanceMatrix(aux);   
    List< List<DistanceNode> > distanceList = new LinkedList< List<DistanceNode> >();
    for (int pos = 0; pos < aux.size(); pos++) {
      aux.get(pos).setLocation(pos);
      List<DistanceNode> distanceNodeList = new ArrayList<DistanceNode>();
      for (int ref = 0; ref < aux.size(); ref++) {
        if (pos != ref) {
          distanceNodeList.add(new DistanceNode(distance[pos][ref],ref));
        } // if
      } // for
      distanceList.add(distanceNodeList);
    } // for                        


    for (List<DistanceNode> aDistanceList : distanceList) {
      Collections.sort(aDistanceList, distanceNodeComparator);
    } // for
                              
    while (aux.size() > size) {
      double minDistance = Double.MAX_VALUE;            
      int toRemove = 0;
      i = 0;
      for (List<DistanceNode> dn : distanceList) {
        if (dn.get(0).getDistance() < minDistance) {
          toRemove = i;
          minDistance = dn.get(0).getDistance();
          //i y toRemove have the same distance to the first solution
        } else if (dn.get(0).getDistance() == minDistance) {
          int k = 0;
          while ((dn.get(k).getDistance() ==
                  distanceList.get(toRemove).get(k).getDistance()) &&
                  k < (distanceList.get(i).size() - 1)) {
            k++;
          }

          if (dn.get(k).getDistance() <
                  distanceList.get(toRemove).get(k).getDistance()) {
            toRemove = i;
          } // if
        } // if
        i++;
      } // while
      
      int tmp = aux.get(toRemove).getLocation();
      aux.remove(toRemove);            
      distanceList.remove(toRemove);

      for (List<DistanceNode> aDistanceList : distanceList) {
        Iterator<DistanceNode> interIterator = aDistanceList.iterator();
        while (interIterator.hasNext()) {
          if (interIterator.next().getReference() == tmp) {
            interIterator.remove();
          } // if
        } // while
      } // while      
    } // while   
    return aux;
  } // environmentalSelection   
} // Spea2Fitness
