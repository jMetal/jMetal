package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.component.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class PositionUpdateParameter extends CategoricalParameter {

  public PositionUpdateParameter(String[] args, List<String> positionUpdateStrategies) {
    super("positionUpdate", args, positionUpdateStrategies);
  }

  public @NotNull PositionUpdate getParameter() {
    PositionUpdate result;
    switch (getValue()) {
      case "defaultPositionUpdate":
        List<Bounds<Double>> positionBounds = (List<Bounds<Double>>) getNonConfigurableParameter(
            "positionBounds");
        double velocityChangeWhenLowerLimitIsReached = (double) findSpecificParameter(
            "velocityChangeWhenLowerLimitIsReached").getValue();
        double velocityChangeWhenUpperLimitIsReached = (double) findSpecificParameter(
            "velocityChangeWhenUpperLimitIsReached").getValue();

        result = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached,
            velocityChangeWhenUpperLimitIsReached, positionBounds);
        break;
      default:
        throw new JMetalException("Position update component unknown: " + getValue());
    }
    return result;
  }
}
