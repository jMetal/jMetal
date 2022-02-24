package org.uma.jmetal.experimental.auto.irace;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.experimental.auto.algorithm.omopso.AutoOMOPSO;
import org.uma.jmetal.experimental.auto.parameter.*;

import java.util.List;

/**
 * @author Daniel Doblas
 */
public class AutoMOPSOIraceFileGenerator {
    private static String formatString = "%-40s %-40s %-7s %-30s %-20s\n";

    public void generateConfigurationFile() {
        String[] parameters =
                ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                        + "--referenceFrontFileName ZDT1.csv "
                        + "--maximumNumberOfEvaluations 25000 "
                        + "--swarmSize 100 "
                        + "--archiveSize 100 "
                        + "--swarmInitialization random "
                        + "--velocityInitialization defaultVelocityInitialization "
                        + "--externalArchive crowdingDistanceArchive "
                        + "--localBestInitialization defaultLocalBestInitialiation "
                        + "--globalBestInitialization defaultGlobalBestInitialization "
                        + "--selection tournament "
                        + "--selectionTournamentSize 2 "
                        + "--perturbation mutationBasedPerturbation "
                        + "--mutation polynomial "
                        + "--mutationProbability 0.01 "
                        + "--mutationRepairStrategy bounds "
                        + "--polynomialMutationDistributionIndex 20.0 "
                        + "--positionUpdate defaultPositionUpdate "
                        + "--globalBestUpdate defaultGlobalBestUpdate "
                        + "--localBestUpdate defaultLocalBestUpdate "
                        + "--velocityUpdate defaultVelocityUpdate "
                        + "--c1Min 1.0 "
                        + "--c1Max 2.0 "
                        + "--c2Min 1.0 "
                        + "--c2Max 2.0 "
                        + "--wMin 0.1 "
                        + "--wMax 0.5 "
                )
                        .split("\\s+");

        AutoOMOPSO autoOMOPSOwithParameters = new AutoOMOPSO();
        autoOMOPSOwithParameters.parseAndCheckParameters(parameters);

        AutoMOPSOIraceFileGenerator ompsoiraceParameterFile = new AutoMOPSOIraceFileGenerator();
        ompsoiraceParameterFile.generateConfigurationFile(
                autoOMOPSOwithParameters.autoConfigurableParameterList);
    }

    public void generateConfigurationFile(List<Parameter<?>> parameterList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Parameter<?> parameter : parameterList) {
            this.decodeParameter(parameter, stringBuilder);
            stringBuilder.append("#\n");
        }

        System.out.println(stringBuilder.toString());
    }

    private void decodeParameter(Parameter<?> parameter, StringBuilder stringBuilder) {
        stringBuilder.append(
                String.format(
                        formatString,
                        parameter.getName(),
                        "\"" + "--" + parameter.getName() + " \"",
                        decodeType(parameter),
                        decodeValidValues(parameter),
                        ""));

        for (Parameter<?> globalParameter : parameter.getGlobalParameters()) {
            decodeParameterGlobal(globalParameter, stringBuilder, parameter);
        }

        for (Pair<String, Parameter<?>> specificParameter : parameter.getSpecificParameters()) {
            decodeParameterSpecific(specificParameter, stringBuilder, parameter);
        }
    }

    private void decodeParameterGlobal(Parameter<?> parameter, StringBuilder stringBuilder, Parameter<?> parentParameter) {
        StringBuilder dependenceString = new StringBuilder("\"" + parameter.getName() + "\"");
        if (parentParameter instanceof CategoricalParameter) {
            var validValues = ((CategoricalParameter) parentParameter).getValidValues();
            dependenceString = new StringBuilder();
            for (String value : validValues) {
                dependenceString.append("\"").append(value).append("\"").append(",");
            }
            dependenceString = new StringBuilder(dependenceString.substring(0, dependenceString.length() - 1));
        }

        stringBuilder.append(
                String.format(
                        formatString,
                        parameter.getName(),
                        "\"" + "--" + parameter.getName() + " \"",
                        decodeType(parameter),
                        decodeValidValues(parameter),
                        "| " + parentParameter.getName() + " %in% c(" + dependenceString + ")"));

        for (Parameter<?> globalParameter : parameter.getGlobalParameters()) {
            decodeParameterGlobal(globalParameter, stringBuilder, parameter);
        }

        for (Pair<String, Parameter<?>> specificParameter : parameter.getSpecificParameters()) {
            decodeParameterSpecific(specificParameter, stringBuilder, parameter);
        }
    }


    private void decodeParameterSpecific(
            Pair<String, Parameter<?>> pair, StringBuilder stringBuilder, Parameter<?> parentParameter) {
        stringBuilder.append(
                String.format(
                        formatString,
                        pair.getRight().getName(),
                        "\"" + "--" + pair.getRight().getName() + " \"",
                        decodeType(pair.getRight()),
                        decodeValidValues(pair.getRight()),
                        "| " + parentParameter.getName() + " %in% c(\"" + pair.getLeft() + "\")"));

        for (Parameter<?> globalParameter : pair.getValue().getGlobalParameters()) {
            decodeParameterGlobal(globalParameter, stringBuilder, pair.getValue());
        }

        for (Pair<String, Parameter<?>> specificParameter : pair.getValue().getSpecificParameters()) {
            decodeParameterSpecific(specificParameter, stringBuilder, pair.getValue());
        }
    }

    private String decodeType(Parameter<?> parameter) {
        String result = " ";
        if (parameter instanceof CategoricalParameter) {
            result = "c";
        } else if (parameter instanceof OrdinalParameter) {
            result = "o";
        } else if (parameter instanceof IntegerParameter) {
            result = "i";
        } else if (parameter instanceof RealParameter) {
            result = "r";
        } else if (parameter instanceof Parameter) {
            result = "o";
        }

        return result;
    }

    private String decodeValidValues(Parameter<?> parameter) {
        String result = " ";

        if (parameter instanceof CategoricalParameter) {
            result = ((CategoricalParameter) parameter).getValidValues().toString();
            result = result.replace("[", "(");
            result = result.replace("]", ")");
        } else if (parameter instanceof OrdinalParameter) {
            result = ((OrdinalParameter<?>) parameter).getValidValues().toString();
            result = result.replace("[", "(");
            result = result.replace("]", ")");
        } else if (parameter instanceof IntegerParameter) {
            result = ((IntegerParameter) parameter).getValidValues().toString();
            result = result.replace("[", "(");
            result = result.replace("]", ")");
        } else if (parameter instanceof RealParameter) {
            result = ((RealParameter) parameter).getValidValues().toString();
            result = result.replace("[", "(");
            result = result.replace("]", ")");
        } else if (parameter instanceof Parameter) {
            result = "(" + parameter.getValue() + ")";
        }

        return result;
    }

    public static void main(String[] args) {
        new AutoMOPSOIraceFileGenerator().generateConfigurationFile();
    }
}
