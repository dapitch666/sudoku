package org.anne.sudoku.grader;

import java.util.*;

public class Chain<T> {
    private final Node<T> root;

    public Chain(T rootData, Map<T, List<T>> links) {
        this.root = new Node<T>(rootData, new ArrayList<>());
        buildChain(root, new HashSet<>(), links);
    }

    public Node<T> getRoot() {
        return root;
    }

    private void buildChain(Node<T> node, Set<T> visited, Map<T, List<T>> links) {
        visited.add(node.data);
        List<T> children = links.get(node.data);

        if (children != null) {
            for (T child : children) {
                if (!visited.contains(child)) {
                    Node<T> childNode = new Node<>(child, new ArrayList<>());
                    node.addChild(childNode);
                    buildChain(childNode, visited, links);
                }
            }
        }
    }

    public record Node<T>(T data, List<Node<T>> children) {

        public void addChild(Node<T> child) {
            children.add(child);
        }

        public List<Node<T>> getChildren() {
            return children;
        }
    }
}

