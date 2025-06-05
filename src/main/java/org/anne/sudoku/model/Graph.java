package org.anne.sudoku.model;

import java.util.*;

public class Graph<T> {
    private final Map<T, List<T>> strongLinks;
    private final Map<T, List<T>> weakLinks;
    private final Set<Cycle<T>> cycles;

    public Graph() {
        strongLinks = new HashMap<>();
        weakLinks = new HashMap<>();
        cycles = new HashSet<>();
    }

    public Graph(Map<T, List<T>> strongLinks, Map<T, List<T>> weakLinks) {
        this.strongLinks = strongLinks;
        this.weakLinks = weakLinks;
        this.cycles = new HashSet<>();
    }

    public void addNode(T node, List<T> strongNeighbors, List<T> weakNeighbors) {
        strongLinks.putIfAbsent(node, strongNeighbors);
        weakLinks.putIfAbsent(node, weakNeighbors);
    }

    public Set<Cycle<T>> findAllCycles() {
        return findAllCycles(12); // default max path length is 12
    }

    public Set<Cycle<T>> findAllCycles(int maxPathLength) {
        for (T vertex : weakLinks.keySet()) {
            dfs(vertex, vertex, new ArrayDeque<>(), new HashSet<>(), false, maxPathLength);
        }
        return cycles;
    }

    private void dfs(T start, T current, Deque<T> path, Set<T> visited, boolean useStrongLink, int maxPathLength) {
        if (path.size() >= maxPathLength) {
            return; // Stop if path exceeds max length
        }
        visited.add(current);
        path.addLast(current);

        List<T> neighbors = useStrongLink ? strongLinks.getOrDefault(current, Collections.emptyList()) : weakLinks.getOrDefault(current, Collections.emptyList());

        for (T neighbor : neighbors) {
            if (neighbor.equals(start) && path.size() > 2) {
                cycles.add(new Cycle<>(new ArrayList<>(path), strongLinks));
            } else if (!visited.contains(neighbor)) {
                dfs(start, neighbor, path, visited, !useStrongLink, maxPathLength);
            }
        }

        path.removeLast();
        visited.remove(current);
    }
}