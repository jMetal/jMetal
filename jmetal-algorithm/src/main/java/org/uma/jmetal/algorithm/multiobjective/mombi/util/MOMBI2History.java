package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ajnebro on 10/9/15.
 */
@SuppressWarnings("serial")
public class MOMBI2History<T extends Solution<?>> implements Serializable {
  public static final int MAX_LENGHT 			= 5;
  private 	  final int numberOfObjectives;
  private       final List<List<Double>> history;
  private 	  final List<Integer>      marks;

  public MOMBI2History(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
    this.history            = new LinkedList<>();
    this.marks				= new ArrayList<>(this.numberOfObjectives);
    for (int i = 0; i < this.numberOfObjectives;i++) {
      this.marks.add(MAX_LENGHT);
    }
  }

  /**
   * Adds a new vector of maxs values to the history. The method ensures that only the
   * newest MAX_LENGTH vectors will be kept in the history
   * @param maxs
   */
  public void add(List<Double> maxs) {
    List<Double> aux = new ArrayList<>(this.numberOfObjectives);
    aux.addAll(maxs);
    this.history.add(aux);
    if (history.size() > MAX_LENGHT)
      history.remove(0);
  }

  /**
   * Returns the mean of the values contained in the history
   */
  public List<Double> mean() {
    List<Double> result = new ArrayList<>(this.numberOfObjectives);
    for (int i = 0; i < this.numberOfObjectives; i++)
      result.add(0.0);

    for (List<Double> historyMember : this.history)
      for (int i = 0; i < this.numberOfObjectives;i++)
        result.set(i, result.get(i) + historyMember.get(i));


    for (int i = 0; i < this.numberOfObjectives; i++)
      result.set(i, result.get(i)/(double)this.history.size());

    return result;
  }

  /**
   * Returns the variance of the values contained in the history
   */
  public List<Double> variance(List<Double> mean) {
    List<Double> result = new ArrayList<>(this.numberOfObjectives);
    for (int i = 0; i < this.numberOfObjectives; i++)
      result.add(0.0);

    for (List<Double> historyMember : this.history)
      for (int i = 0; i < this.numberOfObjectives; i++)
        result.set(i, result.get(i) + Math.pow(historyMember.get(i)-mean.get(i), 2.0));

    for (int i = 0; i < this.numberOfObjectives; i++)
      result.set(i, result.get(i) / (double)this.history.size());

    return result;
  }

  /**
   * Return the std of  the values contained in the history
   */
  public List<Double> std(List<Double> mean) {
    List<Double> result = new ArrayList<>(mean.size());
    result.addAll(this.variance(mean));
    for (int i = 0; i < result.size(); i++)
      result.set(i,Math.sqrt(result.get(i)));

    return result;
  }

  public void mark(int index) {
    this.marks.set(index, MAX_LENGHT);
  }

  public boolean isUnMarked(int index) {
    return this.marks.get(index) == 0;
  }

  public void decreaseMark(int index) {
    if (this.marks.get(index) > 0)
      this.marks.set(index,this.marks.get(index)-1);
  }

  public Double getMaxObjective(int index) {
    Double result = Double.NEGATIVE_INFINITY;

    for (List<Double> list : this.history)
      result = Math.max(result, list.get(index));

    return result;
  }
}
