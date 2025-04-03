package org.anne.sudoku.grader;

import java.util.*;

public class NetBuilder<T> {
    private final Map<T, List<T>> links;
    private final Set<T> visited;
    private final List<Chain<T>> chains;

    public NetBuilder(Map<T, List<T>> links) {
        this.links = links;
        this.visited = new HashSet<>();
        this.chains = new ArrayList<>();
        buildNet();
    }

    private void buildNet() {
        // Find roots and build trees
        for (T node : links.keySet()) {
            if (!visited.contains(node)) {
                // Find root for this component
                T root = findRoot(node);
                // Build tree starting from root
                Chain<T> chain = new Chain<>(root, links);
                chains.add(chain);
                markVisited(chain.getRoot());
            }
        }
    }

    private void markVisited(Chain.Node<T> node) {
        if (node == null) return;
        visited.add(node.data());
        for (Chain.Node<T> child : node.getChildren()) {
            markVisited(child);
        }
    }

    private T findRoot(T start) {
        Set<T> component = new HashSet<>();
        Queue<T> queue = new LinkedList<>();
        queue.add(start);

        // Find all nodes in this component
        while (!queue.isEmpty()) {
            T current = queue.poll();
            if (!component.contains(current)) {
                component.add(current);
                List<T> neighbors = links.get(current);
                if (neighbors != null) {
                    queue.addAll(neighbors);
                }
            }
        }

        // Find node with minimum incoming edges from within component
        T root = start;
        int minIncoming = countIncomingEdges(start, component);

        for (T node : component) {
            int incoming = countIncomingEdges(node, component);
            if (incoming < minIncoming) {
                minIncoming = incoming;
                root = node;
            }
        }

        return root;
    }

    private int countIncomingEdges(T node, Set<T> component) {
        int count = 0;
        for (T other : component) {
            List<T> neighbors = links.get(other);
            if (neighbors != null && neighbors.contains(node)) {
                count++;
            }
        }
        return count;
    }

    public List<Chain<T>> getChains() {
        return chains;
    }
}
