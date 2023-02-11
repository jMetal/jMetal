package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.component.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class PositionUpdateParameter extends CategoricalParameter {

  public PositionUpdateParameter(List<String> positionUpdateStrategies) {
    super("positionUpdate", positionUpdateStrategies);
  }

  public PositionUpdate getParameter() {
    PositionUpdate result;
    switch (value()) {
      case "defaultPositionUpdate":
        List<Bounds<Double>> positionBounds = (List<Bounds<Double>>) getNonConfigurableParameter(
            "positionBounds");
        double velocityChangeWhenLowerLimitIsReached = (double) findSpecificParameter(
            "velocityChangeWhenLowerLimitIsReached").value();
        double velocityChangeWhenUpperLimitIsReached = (double) findSpecificParameter(
            "velocityChangeWhenUpperLimitIsReached").value();

        result = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached,
            velocityChangeWhenUpperLimitIsReached, positionBounds);
        break;
      default:
        throw new JMetalException("Position update component unknown: " + value());
    }
    return result;
  }
}
