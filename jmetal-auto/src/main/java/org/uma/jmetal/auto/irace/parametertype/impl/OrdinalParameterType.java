package org.uma.jmetal.auto.irace.parametertype.impl;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;

import java.util.HashSet;
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

  public List<ParameterType> getAssociatedParameters(){
    return associatedParameters ;
  }

  public void addAssociatedParameter(ParameterType parameter) {
    parameter.setParent(this);
    associatedParameters.add(parameter) ;
    addValue(parameter.getParentTag());
  }
}
