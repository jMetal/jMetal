//  SolutionSet.Java
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

package org.uma.jmetal.core;

import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Class representing a SolutionSet (a set of solutions)
 */
public class SolutionSet implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -8200105946946452804L;

  /**
   * Stores a list of <code>solution</code> objects.
   */
  protected final List<Solution> solutionsList_;

  /**
   * Maximum size of the solution set
   */
  private int capacity_ = 0;

  /**
   * Constructor. Creates an unbounded solution set.
   */
  public SolutionSet() {
    solutionsList_ = new ArrayList<Solution>();
  }

  /**
   * Creates a empty solutionSet with a maximum capacity.
   *
   * @param maximumSize Maximum size.
   */
  public SolutionSet(int maximumSize) {
    solutionsList_ = new ArrayList<Solution>();
    capacity_ = maximumSize;
  } // SolutionSet

  /**
   * Inserts a new solution into the SolutionSet.
   *
   * @param solution The <code>Solution</code> to store
   * @return True If the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean add(Solution solution) throws JMetalException {
    if (solutionsList_.size() == capacity_) {
      Configuration.logger_.severe("The population is full");
      Configuration.logger_.severe("Capacity is : " + capacity_);
      Configuration.logger_.severe("\t Size is: " + this.size());
        throw new JMetalException("The population is full. Capacity is : " + capacity_ + "") ;
    }

    solutionsList_.add(solution);
    return true;
  }

  public boolean add(int index, Solution solution) {
    solutionsList_.add(index, solution);
    return true;
  }

  /**
   * Returns the ith solution in the set.
   *
   * @param i Position of the solution to obtain.
   * @return The <code>Solution</code> at the position i.
   * @throws IndexOutOfBoundsException Exception
   */
  public Solution get(int i) {
    if (i >= solutionsList_.size()) {
      throw new IndexOutOfBoundsException("Index out of Bound " + i);
    }
    return solutionsList_.get(i);
  }

  /**
   * Returns the maximum capacity of the solution set
   *
   * @return The maximum capacity of the solution set
   */
  public int getMaxSize() {
    return capacity_;
  } 

  /**
   * Sorts a SolutionSet using a <code>Comparator</code>.
   *
   * @param comparator <code>Comparator</code> used to sort.
   */

  public void sort(Comparator<Solution> comparator) {
    if (comparator == null) {
      Configuration.logger_.severe("No criterium for comparing exist");
      return;
    }
    Collections.sort(solutionsList_, comparator);
  }

  /**
   * Returns the index of the best Solution using a <code>Comparator</code>. If
   * there are more than one occurrences, only the index of the first one is
   * returned
   *
   * @param comparator <code>Comparator</code> used to compare solutions.
   * @return The index of the best Solution attending to the comparator or
   * <code>-1<code> if the SolutionSet is empty
   */

  int indexBest(Comparator<Solution> comparator) {
    if ((solutionsList_ == null) || (this.solutionsList_.isEmpty())) {
      return -1;
    }

    int index = 0;
    Solution bestKnown = solutionsList_.get(0), candidateSolution;
    int flag;
    for (int i = 1; i < solutionsList_.size(); i++) {
      candidateSolution = solutionsList_.get(i);
      flag = comparator.compare(bestKnown, candidateSolution);
      if (flag == +1) {
        index = i;
        bestKnown = candidateSolution;
      }
    }

    return index;
  }

  /**
   * Returns the best Solution using a <code>Comparator</code>. If there are
   * more than one occurrences, only the first one is returned
   *
   * @param comparator <code>Comparator</code> used to compare solutions.
   * @return The best Solution attending to the comparator or <code>null<code>
   * if the SolutionSet is empty
   */

  public Solution best(Comparator<Solution> comparator) {
    int indexBest = indexBest(comparator);
    if (indexBest < 0) {
      return null;
    } else {
      return solutionsList_.get(indexBest);
    }
  }

  /**
   * Returns the index of the worst Solution using a <code>Comparator</code>. If
   * there are more than one occurrences, only the index of the first one is
   * returned
   *
   * @param comparator <code>Comparator</code> used to compare solutions.
   * @return The index of the worst Solution attending to the comparator or
   * <code>-1<code> if the SolutionSet is empty
   */
  public int indexWorst(Comparator<Solution> comparator) {
    if ((solutionsList_ == null) || (this.solutionsList_.isEmpty())) {
      return -1;
    }

    int index = 0;
    Solution worstKnown = solutionsList_.get(0), candidateSolution;
    int flag;
    for (int i = 1; i < solutionsList_.size(); i++) {
      candidateSolution = solutionsList_.get(i);
      flag = comparator.compare(worstKnown, candidateSolution);
      if (flag == -1) {
        index = i;
        worstKnown = candidateSolution;
      }
    }

    return index;
  }

  /**
   * Returns the worst Solution using a <code>Comparator</code>. If there are
   * more than one occurrences, only the first one is returned
   *
   * @param comparator <code>Comparator</code> used to compare solutions.
   * @return The worst Solution attending to the comparator or <code>null<code>
   * if the SolutionSet is empty
   */
  public Solution worst(Comparator<Solution> comparator) {
    int index = indexWorst(comparator);
    if (index < 0) {
      return null;
    } else {
      return solutionsList_.get(index);
    }
  }

  /**
   * Returns the number of solutions in the SolutionSet.
   *
   * @return The size of the SolutionSet.
   */
  public int size() {
    return solutionsList_.size();
  } 

  /**
   * @return true if the solution set if empty
   */
  public boolean isEmtpy() {
    return (solutionsList_.isEmpty());
  }

  /**
   * Writes the objective function values of the <code>Solution</code> objects
   * into the set in a file.
   *
   * @param path The output file name
   * @Deprecated
   */
  @Deprecated
  public void printObjectivesToFile(String path) throws IOException {
    /* Open the file */
    FileOutputStream fos = new FileOutputStream(path);
    OutputStreamWriter osw = new OutputStreamWriter(fos);
    BufferedWriter bw = new BufferedWriter(osw);

    for (Solution solution : solutionsList_) {
      bw.write(solution.toString());
      bw.newLine();
    }
    bw.close();
  }

  /**
   * Writes the decision encoding.variable values of the <code>Solution</code>
   * solutions objects into the set in a file.
   *
   * @param path The output file name
   * @Deprecated
   */
  @Deprecated
  public void printVariablesToFile(String path) throws IOException {
    FileOutputStream fos = new FileOutputStream(path);
    OutputStreamWriter osw = new OutputStreamWriter(fos);
    BufferedWriter bw = new BufferedWriter(osw);

    if (!isEmtpy()) {
      int numberOfVariables = solutionsList_.get(0).getDecisionVariables().length;
      for (Solution solution : solutionsList_) {
        for (int j = 0; j < numberOfVariables; j++) {
          bw.write(solution.getDecisionVariables()[j].toString() + " ");
        }
        bw.newLine();
      }
    }
    bw.close();
  }

  /**
   * Compares two solution sets
   *
   * @param object Solution set to compare with
   * @return true or false depending on the result of the comparison
   */
  @Override
  public boolean equals(Object object) {
    boolean result;
    if (object == null) {
      result = false;
    } else if (object == this) {
      result = true;
    } else if (!(object instanceof SolutionSet)) {
      result = false;
    } else if (this.size() != ((SolutionSet) object).size()) {
      result = false;
    } else {
      SolutionSet solutionSet = (SolutionSet) object;

      boolean areEquals = true;
      int i = 0;
      while (areEquals && (i < solutionSet.size())) {
        int j = 0;
        boolean found = false;
        while (!found && (j < this.size())) {
          if (solutionSet.get(i).equals(this.get(j))) {
            found = true;
          }
          j++;
        }
        if (!found) {
          areEquals = false;
        }
      }
      result = areEquals;
    }
    return result;
  }

  /**
   * Write the function values of feasible solutions into a file
   *
   * @param path File name
   * @Deprecated
   */
  @Deprecated
  public void printFeasibleFUN(String path) {
    try {
      FileOutputStream fos = new FileOutputStream(path);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      BufferedWriter bw = new BufferedWriter(osw);

      for (Solution aSolutionsList : solutionsList_) {
        if (aSolutionsList.getOverallConstraintViolation() == 0.0) {
          bw.write(aSolutionsList.toString());
          bw.newLine();
        }
      }
      bw.close();
    } catch (IOException e) {
      Configuration.logger_.log(Level.SEVERE, "Error generating the FUN file",
        e);
    }
  }

  /**
   * Write the encoding.variable values of feasible solutions into a file
   *
   * @param path File name
   * @Deprecated
   */
  @Deprecated
  public void printFeasibleVAR(String path) {
    try {
      FileOutputStream fos = new FileOutputStream(path);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      BufferedWriter bw = new BufferedWriter(osw);

      if (!isEmtpy()) {
        int numberOfVariables = solutionsList_.get(0).getDecisionVariables().length;
        for (Solution aSolutionsList : solutionsList_) {
          if (aSolutionsList.getOverallConstraintViolation() == 0.0) {
            for (int j = 0; j < numberOfVariables; j++) {
              bw.write(aSolutionsList.getDecisionVariables()[j].toString()
                + " ");
            }
            bw.newLine();
          }
        }
      }
      bw.close();
    } catch (IOException e) {
      Configuration.logger_.log(Level.SEVERE, "Error generating the VAR file",
        e);
    }
  }

  /**
   * Empties the SolutionSet
   */
  public void clear() {
    solutionsList_.clear();
  }

  /**
   * Deletes the <code>Solution</code> at position i in the set.
   *
   * @param i The position of the solution to remove.
   */
  public void remove(int i) {
    if (i > solutionsList_.size() - 1) {
      Configuration.logger_.log(Level.SEVERE, "Size is: " + this.size());
    }
    solutionsList_.remove(i);
  }

  /**
   * Returns an <code>Iterator</code> to access to the solution set list.
   *
   * @return the <code>Iterator</code>.
   */
  public Iterator<Solution> iterator() {
    return solutionsList_.iterator();
  } 

  /**
   * Returns a new <code>SolutionSet</code> which is the result of the union
   * between the current solution set and the one passed as a parameter.
   *
   * @param solutionSet SolutionSet to join with the current solutionSet.
   * @return The result of the union operation.
   */
  public SolutionSet union(SolutionSet solutionSet) throws JMetalException {
    // Check the correct size. In development
    int newSize = this.size() + solutionSet.size();
    if (newSize < capacity_) {
      newSize = capacity_;
    }

    // Create a new population
    SolutionSet union = new SolutionSet(newSize);
    for (int i = 0; i < this.size(); i++) {
      union.add(this.get(i));
    } 

    for (int i = this.size(); i < (this.size() + solutionSet.size()); i++) {
      union.add(solutionSet.get(i - this.size()));
    } 

    return union;
  }

  /**
   * Replaces a solution by a new one
   *
   * @param position The position of the solution to replace
   * @param solution The new solution
   */
  public void replace(int position, Solution solution) {
    if (position > this.solutionsList_.size()) {
      solutionsList_.add(solution);
    }
    solutionsList_.remove(position);
    solutionsList_.add(position, solution);
  }

  /**
   * Copies the objectives of the solution set to a matrix
   *
   * @return A matrix containing the objectives
   */
  @Deprecated
  public double[][] writeObjectivesToMatrix() {
    if (isEmtpy()) {
      return new double[0][0];
    }
    double[][] objectives;
    objectives = new double[size()][get(0).getNumberOfObjectives()];
    for (int i = 0; i < size(); i++) {
      for (int j = 0; j < get(0).getNumberOfObjectives(); j++) {
        objectives[i][j] = get(i).getObjective(j);
      }
    }
    return objectives;
  }

  public void printObjectives() {
    for (Solution solution : solutionsList_) {
      Configuration.logger_.log(Level.INFO, "" + solution);
    }
  }

  public int getCapacity() {
    return capacity_;
  }

  public void setCapacity(int capacity) {
    capacity_ = capacity;
  }

  public List<Solution> getSolutionsList() {
    return solutionsList_ ;
  }

  public SolutionSet getFeasibleSolutions() {
    SolutionSet feasibleSolutions = new SolutionSet() ;

    for (Solution solution : solutionsList_) {
       if (solution.getOverallConstraintViolation() == 0) {
         feasibleSolutions.add(new Solution((solution))) ;
       }
    }

    return feasibleSolutions ;
  }
}
