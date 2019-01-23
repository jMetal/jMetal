package org.uma.jmetal.util.naming.impl;

import org.uma.jmetal.util.naming.DescribedEntity;

import java.util.*;

public class DescribedEntitySet<Entity extends DescribedEntity> implements
		Set<Entity> {

	private final Map<String, Entity> map = new HashMap<>();

	@Override
	public boolean add(Entity e) {
		Entity stored = map.get(e.getName());
		if (stored == null) {
			map.put(e.getName(), e);
			return true;
		} else if (stored.equals(e)) {
			return false;
		} else {
			throw new IllegalArgumentException("Cannot add " + e
					+ ", conflicting name with " + stored);
		}
	}

	@Override
	public boolean addAll(Collection<? extends Entity> c) {
		boolean isModified = false;
		for (Entity entity : c) {
			isModified |= add(entity);
		}
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
	public boolean retainAll(Collection<?> c) {
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
	public Iterator<Entity> iterator() {
		return map.values().iterator();
	}

	@Override
	public Object[] toArray() {
		return map.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return map.values().toArray(a);
	}

	@Override
	public String toString() {
		TreeSet<String> displaySet = new TreeSet<>(new Comparator<String>() {

			@Override
			public int compare(String s1, String s2) {
				int comparison = s1.compareToIgnoreCase(s2);
				return comparison == 0 ? s1.compareTo(s2) : comparison;
			}
		});
		displaySet.addAll(map.keySet());
		return displaySet.toString();
	}
}
