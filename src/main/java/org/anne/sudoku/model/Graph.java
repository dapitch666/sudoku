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

    public static void main(String[] args) {
        Graph<Character> graph = new Graph<>();

        graph.addNode('A', List.of('B'), List.of('B', 'D'));
        graph.addNode('B', List.of('A', 'C'), List.of('A', 'C', 'E'));
        graph.addNode('C', List.of('F'), List.of('B', 'D', 'F'));
        graph.addNode('D', List.of('C'), List.of('A', 'C', 'G'));
        graph.addNode('E', List.of('F'), List.of('B', 'F'));
        graph.addNode('F', List.of('E'), List.of('C', 'E'));
        graph.addNode('G', List.of('D'), List.of('D'));

        Set<Cycle<Character>> cycles = graph.findAllCycles();
        System.out.println("Found cycles: " + cycles);

        graph = new Graph<>();
        graph.addNode('A', List.of('B', 'G'), List.of('B', 'G'));
        graph.addNode('B', List.of('A'), List.of('A', 'C'));
        graph.addNode('C', List.of('D'), List.of('B', 'D'));
        graph.addNode('D', List.of('C'), List.of('C', 'E'));
        graph.addNode('E', List.of('F'), List.of('D', 'F'));
        graph.addNode('F', List.of('E'), List.of('E', 'G'));
        graph.addNode('G', List.of('A'), List.of('A', 'F'));

        var cycles2 = graph.findAllCycles();
        System.out.println("Found cycles: " + cycles2);
    }
}
