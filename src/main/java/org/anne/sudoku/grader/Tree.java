package org.anne.sudoku.grader;

import java.util.*;

public class Tree<T> {
    private final TreeNode<T> root;
    private final Map<T, List<T>> links;

    public Tree(T rootData, Map<T, List<T>> links) {
        this.root = new TreeNode<T>(rootData, new ArrayList<>());
        this.links = links;
        buildTree(root, new HashSet<>());
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    private void buildTree(TreeNode<T> node, Set<T> visited) {
        visited.add(node.data);
        List<T> children = links.get(node.data);

        if (children != null) {
            for (T child : children) {
                if (!visited.contains(child)) {
                    TreeNode<T> childNode = new TreeNode<>(child, new ArrayList<>());
                    node.addChild(childNode);
                    buildTree(childNode, visited);
                }
            }
        }
    }

    public record TreeNode<T>(T data, List<TreeNode<T>> children) {

        public void addChild(TreeNode<T> child) {
            children.add(child);
        }

        public List<TreeNode<T>> getChildren() {
            return children;
        }
    }
}

