package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.*;

import java.util.*;

public class SimpleColoring extends SolvingTechnique {
    public SimpleColoring() {
        super("Simple Coloring");
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Rule> rules = List.of(this::rule1, this::rule2);
        for (int digit = 1; digit <= 9; digit++) {
            Map<Cell, List<Cell>> strongLinks = grid.findStrongLinks(digit);
            NetBuilder<Cell> netBuilder = new NetBuilder<>(strongLinks);
            for (Chain<Cell> chain : netBuilder.getChains()) {
                List<ColoredCell> cells = new ArrayList<>();
                colorNet(chain.getRoot(), cells, 1);

                for (Rule rule : rules) {
                    List<Cell> changed = rule.apply(digit, cells);
                    if (!changed.isEmpty()) {
                        incrementCounter();
                        log(0, "Chain of %d: %s%n", digit, chain);
                        return changed;
                    }
                }
            }
        }
        return List.of();
    }

    private void colorNet(Chain.Node<Cell> node, List<ColoredCell> cells, int color) {
        cells.add(new ColoredCell(node.data(), color));
        int nextColor = (color == 1) ? 2 : 1;
        for (Chain.Node<Cell> child : node.getChildren()) {
            colorNet(child, cells, nextColor);
        }
    }

    private List<Cell> rule1(int digit, List<ColoredCell> chain) {
        List<Cell> changed = new ArrayList<>();
        for (ColoredCell cell : chain) {
            for (ColoredCell other : chain) {
                if (cell != other && cell.color == other.color && cell.cell.isPeer(other.cell)) {
                    for (Cell c : chain.stream().filter(coloredCell -> coloredCell.color == cell.color).map(c -> c.cell).toList()) {
                        c.removeCandidate(digit);
                        changed.add(c);
                        log("%d removed from %s due to Simple Coloring Rule 1%n", digit, c);
                    }
                    return changed;
                }
            }
        }
        return List.of();
    }

    private List<Cell> rule2(int digit, List<ColoredCell> chain) {
        List<Cell> changed = new ArrayList<>();
        List<Cell> color1 = chain.stream().filter(c -> c.color == 1).map(ColoredCell::cell).toList();
        List<Cell> color2 = chain.stream().filter(c -> c.color == 2).map(ColoredCell::cell).toList();
        for (Cell cell : grid.getCellsWithCandidate(digit)) {
            if (color1.contains(cell) || color2.contains(cell)) continue;
            if (color1.stream().anyMatch(c -> c.isPeer(cell)) && color2.stream().anyMatch(c -> c.isPeer(cell))) {
                cell.removeCandidate(digit);
                changed.add(cell);
                log("%d removed from %s due to Simple Coloring Rule 2%n", digit, cell);
            }
        }
        return changed;
    }

    record ColoredCell(Cell cell, int color) { // TODO: make this a class extending Cell
        @Override
        public String toString() {
            return cell.toString();
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(int digit, List<ColoredCell> chain);
    }
}
