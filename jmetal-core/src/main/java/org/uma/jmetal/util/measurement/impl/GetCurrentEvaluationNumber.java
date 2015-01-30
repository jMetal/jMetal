package org.uma.jmetal.util.measurement.impl;

import fr.vergne.data.access.impl.Puller;
import org.uma.jmetal.util.measurement.PullMeasure;

/**
 * Created by ajnebro on 30/1/15.
 */
public class GetCurrentEvaluationNumber extends Puller<Double> implements PullMeasure<Double>{
  public GetCurrentEvaluationNumber(ValueGenerator<Double> generator) {
    super(generator);
  }

  @Override public String getName() {
    return "numberOfEvaluations";
  }

  @Override public String getDescription() {
    return "Value of the current number of evaluated solutions ";
  }
}
