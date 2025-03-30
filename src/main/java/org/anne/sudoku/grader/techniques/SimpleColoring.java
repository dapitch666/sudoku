package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.*;

import java.util.*;

public class SimpleColoring implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        for (int digit = 1; digit <= 9; digit++) {
            Map<Cell, List<Cell>> strongLinks = grid.findStrongLinks(digit);
            ForestBuilder<Cell> forestBuilder = new ForestBuilder<>(strongLinks);
            for (Tree<Cell> tree : forestBuilder.getTrees()) {
                List<ColoredCell> cells = new ArrayList<>();
                colorTree(tree.getRoot(), cells, 1);

                var changed = applyRule1(digit, cells, sb);
                if (!changed.isEmpty()) return changed;
                changed = applyRule2(grid, digit, cells, sb);
                if (!changed.isEmpty()) return changed;
            }
        }
        return List.of();
    }

    private void colorTree(Tree.TreeNode<Cell> node, List<ColoredCell> cells, int color) {
        cells.add(new ColoredCell(node.data(), color));
        int nextColor = (color == 1) ? 2 : 1;
        for (Tree.TreeNode<Cell> child : node.getChildren()) {
            colorTree(child, cells, nextColor);
        }
    }

    private List<Cell> applyRule1(int digit, List<ColoredCell> chain, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        for (ColoredCell cell : chain) {
            for (ColoredCell other : chain) {
                if (cell != other && cell.color == other.color && cell.cell.isPeer(other.cell)) {
                    for (Cell c : chain.stream().filter(coloredCell -> coloredCell.color == cell.color).map(c -> c.cell).toList()) {
                        c.removeCandidate(digit);
                        changed.add(c);
                        sb.append(String.format("%d removed from %s due to Simple Coloring Rule 1%n", digit, c.getPosition()));
                    }
                    incrementCounter(counter);
                    sb.insert(0, String.format("Chain of %d: %s%n", digit, chain));
                    return changed;
                }
            }
        }
        return List.of();
    }

    private List<Cell> applyRule2(Grid grid, int digit, List<ColoredCell> chain, StringBuilder sb) {
        List<Cell> changed = new ArrayList<>();
        List<Cell> color1 = chain.stream().filter(c -> c.color == 1).map(ColoredCell::cell).toList();
        List<Cell> color2 = chain.stream().filter(c -> c.color == 2).map(ColoredCell::cell).toList();
        for (Cell cell : grid.getCellsWithCandidate(digit)) {
            if (color1.contains(cell) || color2.contains(cell)) continue;
            if (color1.stream().anyMatch(c -> c.isPeer(cell)) && color2.stream().anyMatch(c -> c.isPeer(cell))) {
                cell.removeCandidate(digit);
                changed.add(cell);
                sb.append(String.format("%d removed from %s due to Simple Coloring Rule 2%n", digit, cell.getPosition()));
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter(counter);
            sb.insert(0, String.format("Chain of %d: %s%n", digit, chain));
        }
        return changed;
    }

    record ColoredCell(Cell cell, int color) {
        @Override
        public String toString() {
            return cell().getPosition();
        }
    }

    @Override
    public int getCounter() {
        return counter[0];
    }
}
