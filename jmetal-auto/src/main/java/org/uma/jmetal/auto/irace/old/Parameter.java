package org.uma.jmetal.auto.irace.old;

import java.util.ArrayList;
import java.util.List;

public class Parameter {
  private final String name ;
  private final String switchName ;
  private final ParameterTypes type ;
  private final String validValues ;
  private final String conditionalParameters ;
  private final List<Parameter> associatedParameters ;

  /**
   * Constructor
   * @param name
   * @param switchName
   * @param type
   * @param validValues
   * @param conditionalParameters
   * @param associatedParameters
   */
  public Parameter(
      String name,
      String switchName,
      ParameterTypes type,
      String validValues,
      String conditionalParameters,
      List<Parameter> associatedParameters) {
    this.name = name ;
    this.switchName = switchName ;
    this.type = type ;
    this.validValues = validValues ;
    this.conditionalParameters = conditionalParameters ;
    this.associatedParameters = associatedParameters ;
  }

  /**
   * Constructor
   * @param name
   * @param switchName
   * @param type
   * @param validValues
   * @param conditionalParameters
   */
  public Parameter(
      String name,
      String switchName,
      ParameterTypes type,
      String validValues,
      String conditionalParameters) {
    this(name, switchName, type, validValues, conditionalParameters, new ArrayList<>()) ;
  }

  public String getName() {
    return name;
  }

  public String getSwitch() {
    return "\"" + switchName + "\"";
  }

  public ParameterTypes getType() {
    return type;
  }

  public String getValidValues() {
    return validValues;
  }

  public String getConditionalParameters() {
    return conditionalParameters;
  }

  public List<Parameter> getAssociatedParameters() {
    return associatedParameters;
  }

  public Parameter addAssociatedParameter(Parameter parameter) {
    associatedParameters.add(parameter) ;

    return this ;
  }

  @Override
  public String toString(){
    return (getName() + "\t" +
        "\"" + getSwitch() + "\"" + "\t" +
        getType() + "\t" +
        getValidValues() + "\t" +
        getConditionalParameters()
    ) ;
  }
}
