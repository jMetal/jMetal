package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.auto.component.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.bounds.Bounds;

public class PositionUpdateParameter extends CategoricalParameter {
    public PositionUpdateParameter(String[] args, List<String> positionUpdateStrategies){
        super("positionUpdate", args, positionUpdateStrategies);
    }

    public PositionUpdate getParameter(){
        PositionUpdate result;
        switch (getValue()){
            case "defaultPositionUpdate":
                List<Bounds<Double>> positionBounds = (List<Bounds<Double>>) getNonConfigurableParameter("positionBounds");
                double velocityChangeWhenLowerLimitIsReached = (double) findSpecificParameter("velocityChangeWhenLowerLimitIsReached").getValue();
                double velocityChangeWhenUpperLimitIsReached = (double) findSpecificParameter("velocityChangeWhenUpperLimitIsReached").getValue();

                result = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached, velocityChangeWhenUpperLimitIsReached, positionBounds);
                break;
            default:
                throw new RuntimeException("Position update component unknown: " + getValue());
        }
        return result;
    }
}
