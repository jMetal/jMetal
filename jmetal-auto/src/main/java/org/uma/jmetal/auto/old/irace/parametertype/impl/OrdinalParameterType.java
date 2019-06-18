package org.uma.jmetal.auto.old.irace.parametertype.impl;

import org.uma.jmetal.auto.old.irace.parametertype.ParameterType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Irace parameter types
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class OrdinalParameterType extends ParameterType {
  private Set<String> values ;

  public OrdinalParameterType(String name) {
    this(name, "--" + name) ;
  }

  public OrdinalParameterType(String name, String label) {
    super(name, label) ;
    values = new LinkedHashSet<>() ;
  }

  public void addValue(String value) {
    values.add(value) ;
  }

  @Override
  public String getParameterType() {
    return "o";
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
