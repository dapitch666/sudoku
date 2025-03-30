package org.anne.sudoku.grader;

import java.util.*;

public class ForestBuilder<T> {
    private final Map<T, List<T>> links;
    private final Set<T> visited;
    private final List<Tree<T>> trees;

    public ForestBuilder(Map<T, List<T>> links) {
        this.links = links;
        this.visited = new HashSet<>();
        this.trees = new ArrayList<>();
        buildForest();
    }

    private void buildForest() {
        // Get all nodes from the links map
        Set<T> allNodes = new HashSet<>(links.keySet());
        for (List<T> values : links.values()) {
            allNodes.addAll(values);
        }

        // Find roots and build trees
        for (T node : allNodes) {
            if (!visited.contains(node)) {
                // Find root for this component
                T root = findRoot(node);
                // Build tree starting from root
                Tree<T> tree = new Tree<>(root, links);
                trees.add(tree);
                markVisited(tree.getRoot());
            }
        }
    }

    private void markVisited(Tree.TreeNode<T> node) {
        if (node == null) return;
        visited.add(node.data());
        for (Tree.TreeNode<T> child : node.getChildren()) {
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

    public Set<T> getVisited() {
        return visited;
    }

    public List<Tree<T>> getTrees() {
        return trees;
    }

    public static void main(String[] args) {
        Map<String, List<String>> forestLinks = new HashMap<>();
        forestLinks.put("A", Arrays.asList("B"));
        forestLinks.put("B", Arrays.asList("A", "C", "I"));
        forestLinks.put("C", Arrays.asList("B", "D"));
        forestLinks.put("D", Arrays.asList("C", "E"));
        forestLinks.put("E", Arrays.asList("D", "F", "G"));
        forestLinks.put("F", Arrays.asList("E"));
        forestLinks.put("G", Arrays.asList("E", "H", "I"));
        forestLinks.put("H", Arrays.asList("G"));
        forestLinks.put("I", Arrays.asList("G", "B", "J"));
        forestLinks.put("J", Arrays.asList("K", "I"));
        forestLinks.put("K", Arrays.asList("J"));
        forestLinks.put("L", Arrays.asList("M"));
        forestLinks.put("M", Arrays.asList("L"));
        forestLinks.put("N", Arrays.asList("O"));
        forestLinks.put("O", Arrays.asList("N"));

        ForestBuilder<String> forestBuilder = new ForestBuilder<>(forestLinks);
        List<Tree<String>> trees = forestBuilder.getTrees();
        System.out.println(trees);
    }
}
