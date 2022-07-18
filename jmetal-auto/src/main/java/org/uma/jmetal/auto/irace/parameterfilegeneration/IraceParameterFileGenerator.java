package org.uma.jmetal.auto.irace.parameterfilegeneration;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoConfigurableAlgorithm;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.IntegerParameter;
import org.uma.jmetal.auto.parameter.OrdinalParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.auto.parameter.RealParameter;

public class IraceParameterFileGenerator {
  private static String formatString = "%-40s %-40s %-7s %-30s %-20s\n";

  public void generateConfigurationFile(AutoConfigurableAlgorithm autoConfigurableAlgorithm, String[] parameters) {
    autoConfigurableAlgorithm.parseAndCheckParameters(parameters) ;

    var parameterList = autoConfigurableAlgorithm.getAutoConfigurableParameterList() ;

    var stringBuilder = new StringBuilder();

    for (var parameter : parameterList) {
      decodeParameter(parameter, stringBuilder);
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

  private void decodeParameterGlobal(@NotNull Parameter<?> parameter, StringBuilder stringBuilder, Parameter<?> parentParameter) {
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

    for (@NotNull Pair<String, Parameter<?>> specificParameter : parameter.getSpecificParameters()) {
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

    for (@NotNull Parameter<?> globalParameter : pair.getValue().getGlobalParameters()) {
      decodeParameterGlobal(globalParameter, stringBuilder, pair.getValue());
    }

    for (var specificParameter : pair.getValue().getSpecificParameters()) {
      decodeParameterSpecific(specificParameter, stringBuilder, pair.getValue());
    }
  }

  private String decodeType(Parameter<?> parameter) {
    var result = " ";
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
}
