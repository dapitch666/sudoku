package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Chain;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.NetBuilder;

import java.util.*;

public class SimpleColoring extends SolvingTechnique {
    public SimpleColoring() {
        super("Simple Coloring", Grade.HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Rule> rules = List.of(this::rule1, this::rule2);
        List<Cell> changed = new ArrayList<>();
        for (int digit = 1; digit <= 9; digit++) {
            Map<Cell, List<Cell>> strongLinks = grid.findLinks(digit, true);
            NetBuilder<Cell> netBuilder = new NetBuilder<>(strongLinks);
            for (Chain<Cell> chain : netBuilder.getChains()) {
                List<ColoredCell> cells = new ArrayList<>();
                colorNet(chain.getRoot(), cells, 1);

                for (Rule rule : rules) {
                    if (rule.apply(digit, cells, changed)) {
                        incrementCounter();
                        for (Cell cell : changed) {
                            cell.removeCandidate(digit);
                        }
                        log("- Removed candidate %d from %s%n", digit, changed);
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

    private boolean rule1(int digit, List<ColoredCell> chain, List<Cell> changed) {
        for (ColoredCell cell : chain) {
            for (ColoredCell other : chain) {
                if (cell == other || cell.color != other.color || !cell.cell.isPeer(other.cell)) continue;
                changed.addAll(chain.stream()
                        .filter(coloredCell -> coloredCell.color == cell.color)
                        .map(c -> c.cell)
                        .toList());
                log("Rule 1 on chain of %d%n", digit);
                return true;
            }
        }
        return false;
    }

    private boolean rule2(int digit, List<ColoredCell> chain, List<Cell> changed) {
        List<Cell> color1 = chain.stream().filter(c -> c.color == 1).map(ColoredCell::cell).toList();
        List<Cell> color2 = chain.stream().filter(c -> c.color == 2).map(ColoredCell::cell).toList();
        for (Cell cell : grid.getCells(cell -> cell.hasCandidate(digit))) {
            if (color1.contains(cell) || color2.contains(cell)) continue;
            if (color1.stream().anyMatch(c -> c.isPeer(cell)) && color2.stream().anyMatch(c -> c.isPeer(cell))) {
                changed.add(cell);
            }
        }
        if (changed.isEmpty()) return false;
        log("Rule 2 on chain of %d%n", digit);
        return true;
    }

    record ColoredCell(Cell cell, int color) {
        @Override
        public String toString() {
            return cell.toString();
        }
    }

    @FunctionalInterface
    private interface Rule {
        boolean apply(int digit, List<ColoredCell> chain, List<Cell> changed);
    }
}
