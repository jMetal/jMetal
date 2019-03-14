package org.uma.jmetal.auto.irace.parametertype;

import java.util.ArrayList;
import java.util.List;

/**
 * Irace parameter types
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public abstract class ParameterType {
  private String name;
  private String label;
  private String parentTag;
  private ParameterType parent;
  protected Boolean isGlobalParameter ;

  protected List<ParameterType> globalParameters = new ArrayList<>();
  protected List<ParameterType> associatedParameters = new ArrayList<>();

  public ParameterType(String name, String label) {
    this.name = name;
    this.label = label;
    this.parent = null;
    this.isGlobalParameter = false ;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    return "\"" + label + " \"";
  }

  public abstract String getParameterType();

  public abstract String getRange();

  public String getConditions() {
    String condition = "";
    if (null != getParent()) {
      condition = "| " ;
      if (isGlobalParameter) {
        condition += getParent().getName() + " %in% c(" + getParentTags(getParent()) + ")";
      } else {
        condition += getParent().getName() + " %in% c(\"" + getParentTag() +"\")";
      }
    }

    return condition;
  }

  private String getParentTags(ParameterType parameter) {
    String result = "" ;

    for (ParameterType param : parameter.getAssociatedParameters()) {
      result += "\"" + param.getParentTag() + "\"" + "," ;
    }

    if (result != "") {
      result = result.substring(0, result.length()-1) ;
    }
    return result ;
  }
  public void setParentTag(String parentTag) {
    this.parentTag = parentTag;
  }

  public String getParentTag() {
    return parentTag;
  }

  public ParameterType getParent() {
    return parent;
  }

  public void setParent(ParameterType parent) {
    this.parent = parent ;
  }

  public List<ParameterType> getGlobalParameters() {
    return globalParameters;
  }

  public void addGlobalParameter(ParameterType parameter) {
    parameter.setParent(this);
    parameter.isGlobalParameter = true ;
    globalParameters.add(parameter);
  }

  public List<ParameterType> getAssociatedParameters() {
    return associatedParameters;
  }

  public abstract void addAssociatedParameter(ParameterType parameter);
}
