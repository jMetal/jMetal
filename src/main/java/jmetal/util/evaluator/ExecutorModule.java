package jmetal.util.evaluator;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Created by Antonio J. Nebro on 30/05/14.
 */
public class ExecutorModule implements Module {

  //@Override
  //public void configure() {
  //  bind(Executor.class).to(SequentialExecutor.class) ;
  //}

  @Override public void configure(Binder binder) {
    binder.bind(Executor.class).to(SequentialExecutor.class) ;
  }
}
