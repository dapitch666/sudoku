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
        Set<T> visited = new HashSet<>();
        for (T vertex : weakLinks.keySet()) {
            if (!visited.contains(vertex)) {
                dfs(vertex, vertex, new ArrayList<>(), visited, false);
            }
        }
        return cycles;
    }

    private void dfs(T start, T current, List<T> path, Set<T> visited, boolean useStrongLink) {
        visited.add(current);
        path.add(current);

        List<T> neighbors = useStrongLink ? strongLinks.getOrDefault(current, new ArrayList<>()) : weakLinks.getOrDefault(current, new ArrayList<>());

        for (T neighbor : neighbors) {
            if (neighbor.equals(start) && path.size() > 2) {
                Cycle<T> cycle = new Cycle<>(path);
                cycles.add(cycle);
            } else if (!visited.contains(neighbor)) {
                dfs(start, neighbor, path, visited, !useStrongLink);
            }
        }

        path.removeLast();
        visited.remove(current);
    }
}
