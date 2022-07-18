package org.uma.jmetal.experimental.auto.irace;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.auto.algorithm.nsgaii.AutoNSGAII;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.auto.parameter.IntegerParameter;
import org.uma.jmetal.experimental.auto.parameter.OrdinalParameter;
import org.uma.jmetal.experimental.auto.parameter.Parameter;
import org.uma.jmetal.experimental.auto.parameter.RealParameter;

public class AutoNSGAIIIraceParameterFileGenerator {
  private static String formatString = "%-40s %-40s %-7s %-30s %-20s\n";

  public void generateConfigurationFile() {
    String[] parameters =
            ("--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                    + "--referenceFrontFileName ZDT1.csv "
                    + "--maximumNumberOfEvaluations 25000 "
                    + "--algorithmResult population "
                    + "--populationSize 100 "
                    + "--offspringPopulationSize 100 "
                    + "--createInitialSolutions random "
                    + "--variation crossoverAndMutationVariation "
                    + "--selection tournament "
                    + "--selectionTournamentSize 2 "
                    + "--crossover SBX "
                    + "--crossoverProbability 0.9 "
                    + "--crossoverRepairStrategy bounds "
                    + "--sbxDistributionIndex 20.0 "
                    + "--mutation polynomial "
                    + "--mutationProbabilityFactor 1.0 "
                    + "--mutationRepairStrategy bounds "
                    + "--polynomialMutationDistributionIndex 20.0 ")
                    .split("\\s+");

    AutoNSGAII nsgaiiWithParameters = new AutoNSGAII();
    nsgaiiWithParameters.parseAndCheckParameters(parameters);

    AutoNSGAIIIraceParameterFileGenerator nsgaiiiraceParameterFile = new AutoNSGAIIIraceParameterFileGenerator();
    nsgaiiiraceParameterFile.generateConfigurationFile(
            nsgaiiWithParameters.autoConfigurableParameterList);
  }

  public void generateConfigurationFile(List<Parameter<?>> parameterList) {
    @NotNull StringBuilder stringBuilder = new StringBuilder();

    for (Parameter<?> parameter : parameterList) {
      this.decodeParameter(parameter, stringBuilder);
      stringBuilder.append("#\n");
    }

    System.out.println(stringBuilder.toString());
  }

  private void decodeParameter(Parameter<?> parameter, @NotNull StringBuilder stringBuilder) {
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

    for (@NotNull Pair<String, Parameter<?>> specificParameter : parameter.getSpecificParameters()) {
      decodeParameterSpecific(specificParameter, stringBuilder, parameter);
    }
  }

  private void decodeParameterGlobal(@NotNull Parameter<?> parameter, @NotNull StringBuilder stringBuilder, Parameter<?> parentParameter) {
    @NotNull StringBuilder dependenceString = new StringBuilder("\"" + parameter.getName() + "\"");
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

    for (@NotNull Parameter<?> globalParameter : parameter.getGlobalParameters()) {
      decodeParameterGlobal(globalParameter, stringBuilder, parameter);
    }

    for (@NotNull Pair<String, Parameter<?>> specificParameter : parameter.getSpecificParameters()) {
      decodeParameterSpecific(specificParameter, stringBuilder, parameter);
    }
  }


  private void decodeParameterSpecific(
          Pair<String, Parameter<?>> pair, StringBuilder stringBuilder, @NotNull Parameter<?> parentParameter) {
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
    new AutoNSGAIIIraceParameterFileGenerator().generateConfigurationFile();
  }
}
