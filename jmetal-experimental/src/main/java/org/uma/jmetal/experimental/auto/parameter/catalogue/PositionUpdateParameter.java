package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.util.bounds.Bounds;

import java.util.List;

public class PositionUpdateParameter extends CategoricalParameter {
    public PositionUpdateParameter(String[] args, List<String> positionUpdateStrategies){
        super("positionUpdate", args, positionUpdateStrategies);
    }

    public PositionUpdate getParameter(){
        PositionUpdate result;
        switch (getValue()){
            case "defaultPositionUpdate":
                List<Bounds<Double>> positionBounds = (List<Bounds<Double>>) getNonConfigurableParameter("positionBounds");
                double velocityChangeWhenLowerLimitIsReached = (double) getNonConfigurableParameter("velocityChangeWhenLowerLimitIsReached");
                double velocityChangeWhenUpperLimitIsReached = (double) getNonConfigurableParameter("velocityChangeWhenUpperLimitIsReached");

                result = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached, velocityChangeWhenUpperLimitIsReached, positionBounds);
                break;
            default:
                throw new RuntimeException("Position update component unknown: " + getValue());
        }
        return result;
    }
}
