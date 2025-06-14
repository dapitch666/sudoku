package org.anne.sudoku.model;

import java.util.*;
import java.util.function.UnaryOperator;

public class Cycle<T> implements List<T> {
    private final List<T> list;
    private final CycleType cycleType;

    public Cycle(List<T> path) {
        this(path, new HashMap<>());
    }

    public Cycle(List<T> path, Map<T, List<T>> strongLinks) {
        this.list = new ArrayList<>(path);
        if (path.size() % 2 == 0) {
            this.cycleType = CycleType.CONTINUOUS;
        } else if (!strongLinks.getOrDefault(path.getFirst(), List.of()).contains(path.getLast())) {
            this.cycleType = CycleType.DISCONTINUOUS_WEAK;
        } else {
            this.cycleType = CycleType.DISCONTINUOUS_STRONG;
        }
    }

    public CycleType getCycleType() {
        return cycleType;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return list.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        list.sort(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        list.add(index, element);
    }

    @Override
    public T remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public int hashCode() {
        return list.stream().map(Objects::hashCode).sorted().reduce(0, Integer::sum);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cycle<?> cycle = (Cycle<?>) obj;
        if (list.size() != cycle.list.size()) return false;
        return list.containsAll(cycle.list) && cycle.list.containsAll(list);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public enum CycleType {
        CONTINUOUS,
        DISCONTINUOUS_STRONG,
        DISCONTINUOUS_WEAK;

        @Override
        public String toString() {
            return switch (this) {
                case CONTINUOUS -> "Continuous Alternating Nice Loop";
                case DISCONTINUOUS_STRONG -> "Discontinuous Alternating Nice Loop (Strong)";
                case DISCONTINUOUS_WEAK -> "Discontinuous Alternating Nice Loop (Weak)";
            };
        }
    }
}

