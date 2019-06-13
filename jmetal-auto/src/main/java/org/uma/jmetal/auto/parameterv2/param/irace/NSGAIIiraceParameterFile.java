package org.uma.jmetal.auto.parameterv2.param.irace;

import org.uma.jmetal.auto.parameterv2.param.*;

import java.util.List;

public class NSGAIIiraceParameterFile {
  private static String formatString = "%-40s %-40s %-7s %-30s %-20s\n";

  public void generateConfigurationFile(List<Parameter<?>> parameterList) {
    StringBuilder stringBuilder = new StringBuilder();

    for (Parameter<?> parameter : parameterList) {
      this.decodeParameter(parameter, stringBuilder) ;
      stringBuilder.append("#\n") ;
    }

    System.out.println(stringBuilder.toString());
  }

  private void decodeParameter(Parameter<?> parameter, StringBuilder stringBuilder) {
    stringBuilder.append(
        String.format(
            formatString,
            parameter.getName(),
            "--" + parameter.getName(),
            decodeType(parameter),
            decodeValidValues(parameter),
            ""));

    for (Parameter<?> globalParameter : parameter.getGlobalParameters()) {
      decodeParameter(globalParameter, stringBuilder);
    }

    parameter
        .getSpecificParameters()
        .forEach(
            (key, value) ->
                stringBuilder.append(
                    String.format(
                        formatString,
                        value.getName(),
                        "--" + value.getName(),
                        decodeType(value),
                        decodeValidValues(value),
                        "| " + parameter.getName() + " %in% c(\"" + key + "\")")));

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
      result = ((CategoricalParameter<?>) parameter).getValidValues().toString();
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
