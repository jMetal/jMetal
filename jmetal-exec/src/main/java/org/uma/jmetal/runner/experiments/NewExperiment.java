package org.uma.jmetal.runner.experiments;

import java.io.IOException;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.util.JMetalException;

public class NewExperiment {
	public static void main(String[] args) throws JMetalException, IOException {
		ExperimentData experimentData = new ExperimentData.Builder("prueba")
		.algorithmNameList(new String[]{"NSGAII, SMPSO"})
		.problemList(new String[]{"ZDT1", "ZDT2", "ZDT3"})
		.experimentBaseDirectory("directory")
		.outputParetoFrontFileName("FUN")
		.outputParetoSetFileName("VAR")
		.independentRuns(30)
		.build() ;
	}
}

/*
private final String experimentName ;
private String[] algorithmNameList;
private String[] problemList;
private String[] paretoFrontFileList;
private String[] indicatorList;
private String experimentBaseDirectory;

private String latexDirectory;
private String paretoFrontDirectory;
private String outputParetoFrontFileName;
private String outputParetoSetFileName;
private int independentRuns;*/