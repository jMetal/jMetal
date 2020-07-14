package org.uma.jmetal.lab.experiment.component;

import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentComponent;
import org.uma.jmetal.lab.visualization.StudyVisualizer;
import org.uma.jmetal.solution.Solution;

import java.io.IOException;
import java.util.List;

public class GenerateHtmlPages<Result extends List<? extends Solution<?>>> implements ExperimentComponent {

    private final Experiment<?, Result> experiment;

    public GenerateHtmlPages(Experiment<?, Result> experimentConfiguration) {
        this.experiment = experimentConfiguration;
    }

    @Override
    public void run() throws IOException {

        String directory = experiment.getExperimentBaseDirectory();

        StudyVisualizer visualizer = new StudyVisualizer(directory, StudyVisualizer.SHOW_BEST_FRONTS);

        visualizer.createHTMLPageForEachIndicator();

    }
}
