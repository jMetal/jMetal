package jmetal.metaheuristics.executors;

import com.google.inject.AbstractModule;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class ExecutorModule extends AbstractModule {

  @Override
  public void configure() {
    bind(Executor.class).to(SequentialExecutor.class) ;
  }
}
