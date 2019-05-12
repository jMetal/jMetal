package org.uma.jmetal.algorithm.multiobjective.automoea ;

import static org.junit.Assert.* ;

import java.util.Arrays ;
import java.util.Collection ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.function.BiFunction ;
import java.util.function.Consumer ;
import java.util.function.Function ;
import java.util.function.Supplier ;

import org.junit.Test ;
import org.mockito.Mockito ;
import org.uma.jmetal.algorithm.multiobjective.automoea.AutoMOEA.ExtArchiveType ;
import org.uma.jmetal.solution.Solution ;

public class AutoMOEATest {

  // TODO generalize test
  @Test public void testGetNameCallsNameSupplier() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    String expected = "test" ;
    parameters.name = () -> expected ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    assertEquals(expected, template.getName()) ;
  }

  // TODO generalize test
  @Test public void testGetDescriptionCallsDescriptionSupplier() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    String expected = "test" ;
    parameters.description = () -> expected ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    assertEquals(expected, template.getDescription()) ;
  }

  @Test public void testGetResultReturnsPopulationWhenNoExternalArchive() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    Collection<Solution<?>> expected = Arrays.asList(Mockito.mock(Solution.class)) ;
    parameters.extArchiveType = () -> ExtArchiveType.NONE ;
    parameters.terminationStatus = () -> true ;
    parameters.replacer = (x, y) -> expected ; // population source
    parameters.extReplacer = (x, y) -> null ; // bounded archive source
    parameters.union = (x, y) -> null ; // unbounded archive source
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(expected, template.getResult()) ;
  }

  @Test public void testGetResultReturnsBoundedArchiveWhenBoundedExternalArchive() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    Collection<Solution<?>> expected = Arrays.asList(Mockito.mock(Solution.class)) ;
    parameters.extArchiveType = () -> ExtArchiveType.BOUNDED ;
    parameters.terminationStatus = () -> true ;
    parameters.replacer = (x, y) -> null ; // population source
    parameters.extReplacer = (x, y) -> expected ; // bounded archive source
    parameters.union = (x, y) -> null ; // unbounded archive source
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(expected, template.getResult()) ;
  }

  @Test public void testGetResultReturnsUnboundedArchiveWhenUnboundedExternalArchive() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    Collection<Solution<?>> expected = Arrays.asList(Mockito.mock(Solution.class)) ;
    parameters.extArchiveType = () -> ExtArchiveType.UNBOUNDED ;
    parameters.terminationStatus = () -> true ;
    parameters.replacer = (x, y) -> null ; // population source
    parameters.extReplacer = (x, y) -> null ; // bounded archive source
    parameters.union = (x, y) -> expected ; // unbounded archive source
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(expected, template.getResult()) ;
  }

  @Test public void testTemplateHasCorrectSequenceWhenNoExternalArchive() {
    List<Object> sequence = new LinkedList<>() ;
    Consumer<Object> decorator = calledItem -> sequence.add(calledItem) ;
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    parameters.extArchiveType = () -> ExtArchiveType.NONE ;
    parameters.terminationStatus = () -> true ;
    notifyCall(parameters, decorator) ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(Arrays.asList(parameters.extArchiveType, parameters.initialPopulation,
        parameters.matingPoolBuilder, parameters.variator, parameters.evaluator,
        parameters.replacer, parameters.terminationStatus), sequence) ;
  }

  @Test public void testTemplateHasCorrectSequenceWhenBoundedExternalArchive() {
    List<Object> sequence = new LinkedList<>() ;
    Consumer<Object> decorator = calledItem -> sequence.add(calledItem) ;
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    parameters.extArchiveType = () -> ExtArchiveType.BOUNDED ;
    parameters.terminationStatus = () -> true ;
    notifyCall(parameters, decorator) ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(Arrays.asList(parameters.extArchiveType, parameters.initialPopulation,
        parameters.matingPoolBuilder, parameters.variator, parameters.evaluator,
        parameters.replacer, parameters.extReplacer, parameters.terminationStatus), sequence) ;
  }

  @Test public void testTemplateHasCorrectSequenceWhenUnboundedExternalArchive() {
    List<Object> sequence = new LinkedList<>() ;
    Consumer<Object> decorator = calledItem -> sequence.add(calledItem) ;
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    parameters.extArchiveType = () -> ExtArchiveType.UNBOUNDED ;
    parameters.terminationStatus = () -> true ;
    notifyCall(parameters, decorator) ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(Arrays.asList(parameters.extArchiveType, parameters.initialPopulation,
        parameters.matingPoolBuilder, parameters.variator, parameters.evaluator,
        parameters.replacer, parameters.union, parameters.terminationStatus), sequence) ;
  }

  @Test public void testMatingPoolBuilderIsCalledUntilTermination() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] calls = {0} ;
    parameters.matingPoolBuilder = pop -> {
      calls[0]++ ;
      return pop ;
    } ;
    int maxCalls = 10 ;
    parameters.terminationStatus = () -> calls[0] == maxCalls ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(maxCalls, calls[0]) ;
  }

  @Test public void testVariatorIsCalledUntilTermination() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] calls = {0} ;
    parameters.variator = pop -> {
      calls[0]++ ;
      return pop ;
    } ;
    int maxCalls = 10 ;
    parameters.terminationStatus = () -> calls[0] == maxCalls ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(maxCalls, calls[0]) ;
  }

  @Test public void testEvaluatorIsCalledUntilTermination() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] calls = {0} ;
    parameters.evaluator = pop -> {
      calls[0]++ ;
      return pop ;
    } ;
    int maxCalls = 10 ;
    parameters.terminationStatus = () -> calls[0] == maxCalls ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(maxCalls, calls[0]) ;
  }

  @Test public void testReplacerIsCalledUntilTermination() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] calls = {0} ;
    parameters.replacer = (pop1, pop2) -> {
      calls[0]++ ;
      return pop1 ;
    } ;
    int maxCalls = 10 ;
    parameters.terminationStatus = () -> calls[0] == maxCalls ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(maxCalls, calls[0]) ;
  }

  @Test public void testExternalReplacerIsCalledUntilTerminationWhenBoundedExternalArchive() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] calls = {0} ;
    parameters.extArchiveType = () -> ExtArchiveType.BOUNDED ;
    parameters.extReplacer = (pop1, pop2) -> {
      calls[0]++ ;
      return pop1 ;
    } ;
    int maxCalls = 10 ;
    parameters.terminationStatus = () -> calls[0] == maxCalls ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(maxCalls, calls[0]) ;
  }

  @Test public void testUnionIsCalledUntilTerminationWhenUnboundedExternalArchive() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] calls = {0} ;
    parameters.extArchiveType = () -> ExtArchiveType.UNBOUNDED ;
    parameters.union = (pop1, pop2) -> {
      calls[0]++ ;
      return pop1 ;
    } ;
    int maxCalls = 10 ;
    parameters.terminationStatus = () -> calls[0] == maxCalls ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(maxCalls, calls[0]) ;
  }

  @Test public void testInitialPopulationIsCalledOnce() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] initCalls = {0} ;
    parameters.initialPopulation = () -> {
      initCalls[0]++ ;
      return null ;
    } ;
    int[] terminationCalls = {0} ;
    parameters.terminationStatus = () -> ++terminationCalls[0] == 10 ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(1, initCalls[0]) ;
  }

  @Test public void testExternalArchiveTypeIsRetrievedOnce() {
    Parameters<Collection<Solution<?>>> parameters = new Parameters<>() ;
    int[] typeCalls = {0} ;
    parameters.extArchiveType = () -> {
      typeCalls[0]++ ;
      return null ;
    } ;
    int[] terminationCalls = {0} ;
    parameters.terminationStatus = () -> ++terminationCalls[0] == 10 ;
    AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> template =
        instantiate(parameters) ;
    template.run() ;
    assertEquals(1, typeCalls[0]) ;
  }

  private AutoMOEA<Solution<?>, Collection<Solution<?>>, Collection<Solution<?>>> instantiate(
      Parameters<Collection<Solution<?>>> p) {
    return new AutoMOEA<>(p.name, p.description, p.initialPopulation, p.matingPoolBuilder,
        p.variator, p.evaluator, p.replacer, p.extArchiveType, p.extReplacer, p.union,
        p.terminationStatus) ;
  }

  class Parameters<Pop> {
    Supplier<String> name = () -> null ;
    Supplier<String> description = () -> null ;
    Supplier<Pop> initialPopulation = () -> null ;
    Function<Pop, Pop> matingPoolBuilder = Function.identity() ;
    Function<Pop, Pop> variator = Function.identity() ;
    Function<Pop, Pop> evaluator = Function.identity() ;
    BiFunction<Pop, Pop, Pop> replacer = (x, y) -> x ;
    BiFunction<Pop, Pop, Pop> extReplacer = (x, y) -> x ;
    Supplier<ExtArchiveType> extArchiveType = () -> null ;
    BiFunction<Pop, Pop, Pop> union = (x, y) -> x ;
    Supplier<Boolean> terminationStatus = () -> null ;
  }

  private void notifyCall(Parameters<Collection<Solution<?>>> parameters,
      Consumer<Object> decorator) {
    parameters.name = notifyCall(parameters.name, decorator, "name") ;
    parameters.description = notifyCall(parameters.description, decorator, "description") ;
    parameters.initialPopulation =
        notifyCall(parameters.initialPopulation, decorator, "initialPopulation") ;
    parameters.matingPoolBuilder =
        notifyCall(parameters.matingPoolBuilder, decorator, "matingPoolBuilder") ;
    parameters.variator = notifyCall(parameters.variator, decorator, "variator") ;
    parameters.evaluator = notifyCall(parameters.evaluator, decorator, "evaluator") ;
    parameters.replacer = notifyCall(parameters.replacer, decorator, "replacer") ;
    parameters.extReplacer = notifyCall(parameters.extReplacer, decorator, "extReplacer") ;
    parameters.extArchiveType = notifyCall(parameters.extArchiveType, decorator, "extArchiveType") ;
    parameters.union = notifyCall(parameters.union, decorator, "union") ;
    parameters.terminationStatus =
        notifyCall(parameters.terminationStatus, decorator, "terminationStatus") ;
  }

  private <T> Supplier<T> notifyCall(Supplier<T> supplier, Consumer<Object> notifier, String id) {
    return new Supplier<T>() {
      @Override public T get() {
        notifier.accept(this) ;
        return supplier.get() ;
      }

      @Override public String toString() {
        return id ;
      }
    } ;
  }

  private <T, U> Function<T, U> notifyCall(Function<T, U> function, Consumer<Object> notifier,
      String id) {
    return new Function<T, U>() {
      @Override public U apply(T t) {
        notifier.accept(this) ;
        return function.apply(t) ;
      }

      @Override public String toString() {
        return id ;
      }
    } ;
  }

  private <T, U, V> BiFunction<T, U, V> notifyCall(BiFunction<T, U, V> function,
      Consumer<Object> notifier, String id) {
    return new BiFunction<T, U, V>() {
      @Override public V apply(T t, U u) {
        notifier.accept(this) ;
        return function.apply(t, u) ;
      }

      @Override public String toString() {
        return id ;
      }
    } ;
  }
}
