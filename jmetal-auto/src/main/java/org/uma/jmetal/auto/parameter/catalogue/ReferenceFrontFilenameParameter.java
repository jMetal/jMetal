package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.Parameter;

import java.util.function.Function;

public class ReferenceFrontFilenameParameter extends Parameter<String> {
  public ReferenceFrontFilenameParameter(String args[]) {
    super("referenceFrontFileName", args);
  }

  @Override
  public void check() {
    // TODO
  }

  @Override
  public Parameter<String> parse() {
    setValue(on("--referenceFrontFileName", getArgs(), Function.identity()));

    return this;
  }
}
