package org.uma.jmetal.auto.irace.parametertype.impl;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Irace parameter types
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CategoricalParameterType extends ParameterType {
  private Set<String> values ;

  public CategoricalParameterType(String name) {
    this(name, "--" + name) ;
  }

  public CategoricalParameterType(String name, String label) {
    super(name, label) ;
    values = new HashSet<>() ;
  }

  public void addValue(String value) {
    values.add(value) ;
  }

  @Override
  public String getParameterType() {
    return "c";
  }

  @Override
  public String getRange() {
    StringBuilder range = new StringBuilder("(") ;
    for (String value : values) {
      range.append(value) ;
      range.append(",") ;
    }

    if (range.length() > 1) {
      range.deleteCharAt(range.length() - 1);
    }
    range.append(")") ;

    return range.toString();
  }

  public List<ParameterType> getSpecificParameters(){
    return specificParameters;
  }

  public void addSpecificParameter(ParameterType parameter) {
    parameter.setParent(this);
    specificParameters.add(parameter) ;
    addValue(parameter.getParentTag());
  }
}
