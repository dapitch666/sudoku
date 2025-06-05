package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;

import java.util.*;

public class HiddenUniqueRectangles extends SolvingTechnique {
    public HiddenUniqueRectangles() {
        super("Hidden Unique Rectangles", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Rule> rules = List.of(this::rule1, this::rule2, this::rule2b);
        List<Rectangle> rectangles = getRectangles();
        for (Rectangle rectangle : rectangles) {
            for (Rule rule : rules) {
                List<Cell> changed = rule.apply(rectangle);
                if (!changed.isEmpty()) return changed;
            }
        }
        return List.of();
    }

    private List<Cell> rule1(Rectangle rectangle) {
        if (rectangle.cell2.isBiValue() || rectangle.cell3.isBiValue() || rectangle.cell4.isBiValue()) return List.of();
        BitSet strongDigits4and2 = strongDigits(rectangle.cell2, rectangle.cell4);
        BitSet strongDigits4and3 = strongDigits(rectangle.cell3, rectangle.cell4);
        if (strongDigits4and2.cardinality() != 1 || !strongDigits4and2.equals(strongDigits4and3)) return List.of();
        int digit = rectangle.cell1.candidates().stream()
                .filter(i -> i != strongDigits4and2.nextSetBit(0))
                .findFirst()
                .orElse(-1);
        if (digit == -1) return List.of();
        rectangle.cell4.removeCandidate(digit);
        incrementCounter();
        log("Hidden Unique Rectangles Type 1 found %s%n- Removed %d from %s%n", rectangle, digit, rectangle.cell4);
        return List.of(rectangle.cell4);
    }

    private List<Cell> rule2(Rectangle rectangle) {
        Map<Cell, Integer> changed = new HashMap<>();
        if (strongDigits(rectangle.cell1, rectangle.cell2).cardinality() != 2) return List.of();
        // cell1 and cell2 are the floor cells
        for (int digit : rectangle.cell1.getCandidates()) {
            int otherDigit = rectangle.cell1.getOtherCandidate(digit);
            if (grid.isConjugatePair(rectangle.cell1, rectangle.cell3, digit)) {
                changed.put(rectangle.cell4, otherDigit);
            }
            if (grid.isConjugatePair(rectangle.cell2, rectangle.cell4, digit)) {
                changed.put(rectangle.cell3, otherDigit);
            }
        }
        changed.keySet().forEach(cell -> {
            int digitToRemove = changed.get(cell);
            cell.removeCandidate(digitToRemove);
            log("- Removed %d from %s%n", digitToRemove, cell);
        });
        if (changed.isEmpty()) return List.of();
        incrementCounter();
        log(0, "Hidden Unique Rectangles Type 2 found %s%n", rectangle);
        return new ArrayList<>(changed.keySet());
    }

    private List<Cell> rule2b(Rectangle rectangle) {
        if (!rectangle.cell3.isBiValue() || !strongDigits(rectangle.cell3, rectangle.cell4).isEmpty()) return List.of();
        BitSet notStronglyLinked = rectangle.cell1.candidates().stream()
                .filter(d -> !grid.isConjugatePair(rectangle.cell1, rectangle.cell2, d))
                .collect(BitSet::new, BitSet::set, BitSet::or);
        if (notStronglyLinked.cardinality() != 1) return List.of();
        int digit = notStronglyLinked.nextSetBit(0);
        incrementCounter();
        rectangle.cell4.removeCandidate(digit);
        log("Hidden Unique Rectangles Type 2b found %s%n- Removed %d from %s%n", rectangle, digit, rectangle.cell4);
        return List.of(rectangle.cell4);
    }

    private List<Rectangle> getRectangles() {
        List<Rectangle> rectangles = new ArrayList<>();
        for (Cell cell1 : grid.getCells(Predicates.biValueCells)) {
            for (Cell cell2 : grid.getCells(Predicates.isPeerOf(cell1)
                    .and(Predicates.inUnit(UnitType.BOX, cell1.getBox()))
                    .and(Predicates.containsAllCandidates(cell1.candidates())))) {
                for (Cell cell3 : grid.getCells(Predicates.isPeerOf(cell1)
                        .and(Predicates.inUnit(UnitType.BOX, cell1.getBox()).negate())
                        .and(Predicates.containsAllCandidates(cell1.candidates())))) {
                    Cell cell4 = grid.findFourthCorner(cell1, cell2, cell3);
                    if (!cell4.isSolved() && cell4.getBox() == cell3.getBox() && cell4.hasCandidates(cell1.candidates())) {
                        rectangles.add(new Rectangle(cell1, cell2, cell3, cell4));
                    }
                }
            }
        }
        return rectangles;
    }

    public BitSet strongDigits(Cell cellA, Cell cellB) {
        return cellA.candidates().stream()
                .filter(digit -> grid.isConjugatePair(cellA, cellB, digit))
                .collect(BitSet::new, BitSet::set, BitSet::or);
    }

    private record Rectangle(Cell cell1, Cell cell2, Cell cell3, Cell cell4) {
        @Override
        public String toString() {
            return List.of(cell1, cell2, cell3, cell4).toString();
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(Rectangle rectangle);
    }
}
