package org.uma.jmetal.auto.pruebas.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of the {@Link DoubleSolution} interface
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SpecificDoubleSolution2 implements DoubleSolution2 {
  protected List<Bounds<Double>> bounds;

  protected List<Double> variables ;
  protected double[] objectives ;
  protected double[] constraints ;

  protected Map<Object, Object> attributes ;

  private int id = 54 ;

  public SpecificDoubleSolution2(List<Bounds<Double>> bounds, int numberOfObjectives, int numberOfConstraints) {
    Check.notNull(bounds);
    Check.valueIsNotNegative(numberOfObjectives);
    Check.valueIsNotNegative(numberOfConstraints);

    this.bounds = bounds ;
    objectives = new double[numberOfObjectives] ;
    constraints = new double[numberOfConstraints] ;

    variables = new ArrayList<>(bounds.size()) ;
    for (int i = 0; i < bounds.size(); i++) {
      variables.set(i, JMetalRandom.getInstance().nextDouble(bounds.get(i).getLowerBound(), bounds.get(i).getUpperBound()));
    }

    attributes = new HashMap<>() ;
  }

  public int getID() {
    return id ;
  }

  public SpecificDoubleSolution2(SpecificDoubleSolution2 solution) {
    IntStream.range(0, solution.variables().size()).forEach(i -> variables().set(i, solution.variables().get(i)));
    IntStream.range(0, solution.objectives().length).forEach(i -> objectives()[i] = solution.objectives()[i]);
    IntStream.range(0, solution.constraints().length).forEach(i -> constraints()[i] = solution.constraints()[i]);

    bounds = solution.bounds;
    attributes = new HashMap<>(solution.attributes);
  }

  @Override
  public List<Bounds<Double>> getBounds(){
    return bounds;
  }

  @Override
  public List<Double> variables() {
    return variables;
  }

  @Override
  public double[] objectives() {
    return objectives;
  }

  @Override
  public double[] constraints() {
    return constraints;
  }

  @Override
  public Map<Object, Object> attributes() {
    return attributes;
  }

  @Override
  public Solution2 copy() {
    return new SpecificDoubleSolution2(this);
  }
}
