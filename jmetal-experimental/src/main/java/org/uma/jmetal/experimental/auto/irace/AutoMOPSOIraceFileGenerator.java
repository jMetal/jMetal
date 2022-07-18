package org.uma.jmetal.experimental.auto.irace;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.auto.algorithm.mopso.AutoMOPSO;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.auto.parameter.IntegerParameter;
import org.uma.jmetal.experimental.auto.parameter.OrdinalParameter;
import org.uma.jmetal.experimental.auto.parameter.Parameter;
import org.uma.jmetal.experimental.auto.parameter.RealParameter;

/**
 * @author Daniel Doblas
 */
public class AutoMOPSOIraceFileGenerator {
    private static String formatString = "%-40s %-40s %-7s %-30s %-20s\n";

    public void generateConfigurationFile() {
        var parameters =
                ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                        + "--referenceFrontFileName ZDT1.csv "
                        + "--maximumNumberOfEvaluations 25000 "
                        + "--swarmSize 100 "
                        + "--archiveSize 100 "
                        + "--swarmInitialization random "
                        + "--velocityInitialization defaultVelocityInitialization "
                        + "--externalArchive crowdingDistanceArchive "
                        + "--localBestInitialization defaultLocalBestInitialization "
                        + "--globalBestInitialization defaultGlobalBestInitialization "
                        + "--globalBestSelection binaryTournament "
                        + "--perturbation frequencySelectionMutationBasedPerturbation "
                        + "--frequencyOfApplicationOfMutationOperator 6 "
                        + "--mutation polynomial "
                        + "--mutationProbabilityFactor 1.0 "
                        + "--mutationRepairStrategy bounds "
                        + "--polynomialMutationDistributionIndex 20.0 "
                        + "--positionUpdate defaultPositionUpdate "
                        + "--velocityChangeWhenLowerLimitIsReached -1.0 "
                        + "--velocityChangeWhenUpperLimitIsReached -1.0 "
                        + "--globalBestUpdate defaultGlobalBestUpdate "
                        + "--localBestUpdate defaultLocalBestUpdate "
                        + "--velocityUpdate defaultVelocityUpdate "
                        + "--inertiaWeightComputingStrategy randomSelectedValue "
                        + "--c1Min 1.0 "
                        + "--c1Max 2.0 "
                        + "--c2Min 1.0 "
                        + "--c2Max 2.0 "
                        + "--weightMin 0.1 "
                        + "--weightMax 0.5 ")
                        .split("\\s+");

        var autoMOPSOwithParameters = new AutoMOPSO();
        autoMOPSOwithParameters.parseAndCheckParameters(parameters);

        var ompsoiraceParameterFile = new AutoMOPSOIraceFileGenerator();
        ompsoiraceParameterFile.generateConfigurationFile(
            autoMOPSOwithParameters.autoConfigurableParameterList);
    }

    public void generateConfigurationFile(List<Parameter<?>> parameterList) {
        @NotNull StringBuilder stringBuilder = new StringBuilder();

        for (var parameter : parameterList) {
            this.decodeParameter(parameter, stringBuilder);
            stringBuilder.append("#\n");
        }

        System.out.println(stringBuilder.toString());
    }

    private void decodeParameter(@NotNull Parameter<?> parameter, StringBuilder stringBuilder) {
        stringBuilder.append(
                String.format(
                        formatString,
                        parameter.getName(),
                        "\"" + "--" + parameter.getName() + " \"",
                        decodeType(parameter),
                        decodeValidValues(parameter),
                        ""));

        for (@NotNull Parameter<?> globalParameter : parameter.getGlobalParameters()) {
            decodeParameterGlobal(globalParameter, stringBuilder, parameter);
        }

        for (var specificParameter : parameter.getSpecificParameters()) {
            decodeParameterSpecific(specificParameter, stringBuilder, parameter);
        }
    }

    private void decodeParameterGlobal(Parameter<?> parameter, StringBuilder stringBuilder, Parameter<?> parentParameter) {
        @NotNull StringBuilder dependenceString = new StringBuilder("\"" + parameter.getName() + "\"");
        if (parentParameter instanceof CategoricalParameter) {
            var validValues = ((CategoricalParameter) parentParameter).getValidValues();
            dependenceString = new StringBuilder();
            for (var value : validValues) {
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

        for (var globalParameter : parameter.getGlobalParameters()) {
            decodeParameterGlobal(globalParameter, stringBuilder, parameter);
        }

        for (var specificParameter : parameter.getSpecificParameters()) {
            decodeParameterSpecific(specificParameter, stringBuilder, parameter);
        }
    }


    private void decodeParameterSpecific(
            @NotNull Pair<String, Parameter<?>> pair, StringBuilder stringBuilder, @NotNull Parameter<?> parentParameter) {
        stringBuilder.append(
                String.format(
                        formatString,
                        pair.getRight().getName(),
                        "\"" + "--" + pair.getRight().getName() + " \"",
                        decodeType(pair.getRight()),
                        decodeValidValues(pair.getRight()),
                        "| " + parentParameter.getName() + " %in% c(\"" + pair.getLeft() + "\")"));

        for (@NotNull Parameter<?> globalParameter : pair.getValue().getGlobalParameters()) {
            decodeParameterGlobal(globalParameter, stringBuilder, pair.getValue());
        }

        for (var specificParameter : pair.getValue().getSpecificParameters()) {
            decodeParameterSpecific(specificParameter, stringBuilder, pair.getValue());
        }
    }

    private String decodeType(Parameter<?> parameter) {
        @NotNull String result = " ";
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

    private @NotNull String decodeValidValues(Parameter<?> parameter) {
        var result = " ";

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
