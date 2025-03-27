package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;

import java.util.*;

public class SimpleColoring implements SolvingTechnique {
    private final int[] counter = new int[1];

    @Override
    public List<Cell> apply(Grid grid, StringBuilder sb) {
        for (int digit = 1; digit <= 9; digit++) {
            List<List<Cell>> chains = grid.findChains(digit);
            for (List<Cell> chain : chains) {
                List<ColoredCell> cells = new ArrayList<>();
                int color = 1;
                for (Cell cell : chain) {
                    cells.add(new ColoredCell(cell, color));
                    if (color == 1) {
                        color = 2;
                    } else {
                        color = 1;
                    }
                }
                if (digit == 7) System.out.println("Chain " + chain);
                var changed = applyRule1(digit, cells, sb);
                if (!changed.isEmpty()) return changed;
                changed = applyRule2(grid, digit, cells, sb);
                if (!changed.isEmpty()) return changed;
            }
        }
        return List.of();
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
