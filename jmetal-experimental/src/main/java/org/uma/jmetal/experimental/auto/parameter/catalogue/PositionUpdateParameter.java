package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.util.bounds.Bounds;

public class PositionUpdateParameter extends CategoricalParameter {
    public PositionUpdateParameter(String[] args, List<String> positionUpdateStrategies){
        super("positionUpdate", args, positionUpdateStrategies);
    }

    public PositionUpdate getParameter(){
        PositionUpdate result;
        switch (getValue()){
            case "defaultPositionUpdate":
                var positionBounds = (List<Bounds<Double>>) getNonConfigurableParameter("positionBounds");
                var velocityChangeWhenLowerLimitIsReached = (double) findSpecificParameter("velocityChangeWhenLowerLimitIsReached").getValue();
                var velocityChangeWhenUpperLimitIsReached = (double) findSpecificParameter("velocityChangeWhenUpperLimitIsReached").getValue();

                result = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached, velocityChangeWhenUpperLimitIsReached, positionBounds);
                break;
            default:
                throw new RuntimeException("Position update component unknown: " + getValue());
        }
        return result;
    }
}
