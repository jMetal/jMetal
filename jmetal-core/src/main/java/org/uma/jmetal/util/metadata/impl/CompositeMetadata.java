package org.uma.jmetal.util.metadata.impl;

import static java.util.function.Function.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.uma.jmetal.util.metadata.Metadata;

public class CompositeMetadata<S, V> implements Metadata<S, V> {

  private final Map<Predicate<S>, Metadata<S, V>> components = new LinkedHashMap<>();
  private final Metadata<S, V> defautMetadata;

  public CompositeMetadata(Metadata<S, V> defautMetadata) {
    this.defautMetadata = defautMetadata;
  }

  public CompositeMetadata() {
    this(source -> {
      throw new IllegalArgumentException("No metadata for " + source);
    });
  }

  public void put(Predicate<S> predicate, Metadata<S, V> metadata) {
    components.put(predicate, metadata);
  }

  public <T, U> void put(Predicate<S> predicate, Function<S, T> transformSource, Metadata<T, U> metadata, Function<U, V> transformValue) {
    put(predicate, transformSource.andThen(metadata::read).andThen(transformValue)::apply);
  }

  public <T> void put(Predicate<S> predicate, Function<S, T> transformSource, Metadata<T, V> metadata) {
    put(predicate, transformSource, metadata, identity());
  }

  public <T> void put(Predicate<S> predicate, Metadata<S, T> metadata, Function<T, V> transformValue) {
    put(predicate, identity(), metadata, transformValue);
  }

  @Override
  public V read(S source) {
    return components.entrySet().stream()//
        .filter(entry -> entry.getKey().test(source))//
        .map(entry -> entry.getValue())//
        .findFirst()//
        .orElse(defautMetadata)//
        .read(source);
  }

}
