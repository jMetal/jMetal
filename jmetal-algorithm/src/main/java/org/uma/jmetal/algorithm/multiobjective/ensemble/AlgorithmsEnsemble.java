package org.uma.jmetal.algorithm.multiobjective.ensemble;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

public class AlgorithmsEnsemble implements Algorithm<List<DoubleSolution>> {
    @Override
    public void run() {

    }

    @Override
    public List<DoubleSolution> getResult() {
        return null;
    }

    @Override
    public String getName() {
        return "Ensemble";
    }

    @Override
    public String getDescription() {
        return "Ensemble of multiobjective algorithms using an external archive";
    }
}
