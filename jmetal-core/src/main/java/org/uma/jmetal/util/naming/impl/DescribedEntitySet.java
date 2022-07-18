package org.uma.jmetal.util.naming.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.util.naming.DescribedEntity;

public class DescribedEntitySet<Entity extends DescribedEntity> implements Set<Entity> {

  private final Map<String, Entity> map = new HashMap<>();

  @Override
  public boolean add(@NotNull Entity e) {
    var stored = map.get(e.getName());
    if (stored == null) {
      map.put(e.getName(), e);
      return true;
    } else if (stored.equals(e)) {
      return false;
    } else {
      throw new IllegalArgumentException("Cannot add " + e + ", conflicting name with " + stored);
    }
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends Entity> c) {
    Boolean acc = false;
    for (Entity entity : c) {
      @NotNull Boolean add = add(entity);
      acc = acc || add;
    }
    boolean isModified = acc;
      return isModified;
  }

  @SuppressWarnings("unchecked")
  public <E extends Entity> E get(String name) {
    return (E) map.get(name);
  }

  @Override
  public boolean remove(Object o) {
    return map.values().remove(o);
  }

  public boolean remove(String name) {
    return map.keySet().remove(name);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return map.values().removeAll(c);
  }

  @Override
  public boolean contains(Object o) {
    return map.values().contains(o);
  }

  public boolean contains(String name) {
    return map.keySet().contains(name);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return map.values().containsAll(c);
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    return map.values().retainAll(c);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public @NotNull Iterator<Entity> iterator() {
    return map.values().iterator();
  }

  @Override
  public Object @NotNull [] toArray() {
    return map.values().toArray();
  }

  @Override
  public <T> T @NotNull [] toArray(T[] a) {
    return map.values().toArray(a);
  }

  @Override
  public String toString() {
    @NotNull TreeSet<String> displaySet =
        new TreeSet<>(
                (s1, s2) -> {
                  var comparison = s1.compareToIgnoreCase(s2);
                  return comparison == 0 ? s1.compareTo(s2) : comparison;
                });
    displaySet.addAll(map.keySet());
    return displaySet.toString();
  }
}
