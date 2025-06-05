package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Cell;

import java.util.*;

public class UniqueRectangles extends SolvingTechnique {
    public UniqueRectangles() {
        super("Unique Rectangles", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Rectangle> rectangles = getRectangles();
        List<Rule> rules = List.of(this::rule1, this::rule2, this::rule3, this::rule4, this::rule5);

        for (Rectangle rectangle : rectangles) {
            for (Rule rule : rules) {
                List<Cell> changed = rule.apply(rectangle);
                if (!changed.isEmpty()) return changed;
            }
        }
        // No changes made
        return List.of();
    }

    // Rule 1: If three corners of a rectangle have the same candidates,
    // the fourth one can't have any of these candidates.
    private List<Cell> rule1(Rectangle rectangle) {
        if (!rectangle.cellC().isBiValue() && !rectangle.cellD().isBiValue()) return List.of();
        Cell cell = rectangle.cellC().isBiValue() ? rectangle.cellD() : rectangle.cellC();
        var changed = removeCandidatesFromCellAndLog(cell, rectangle.commonCandidates());
        if (!changed.isEmpty()) {
            log(0, "Unique Rectangle Type 1 found: %s%n", rectangle);
        }
        return changed;
    }

    // Rule 2: If both roofs are tri-value and have the same candidates,
    // the extra candidate MUST be in one of the roof cells and can be removed from common isPeerOf.
    private List<Cell> rule2(Rectangle rectangle) {
        if (!rectangle.bothRoofAreTriValue() || !rectangle.roofHasSameCandidates()) return List.of();
        int digit = rectangle.cellC().candidates().stream()
                .filter(d -> !rectangle.commonCandidates().get(d))
                .findFirst()
                .orElseThrow();
        var changed = removeCandidateFromCellsAndLog(List.of(grid.getCells(Predicates.isPeerOf(rectangle.cellC(), rectangle.cellD())
                        .and(Predicates.containsCandidate(digit)))),
                digit);
        if (!changed.isEmpty()) {
            log(0, "Unique Rectangle Type 2 found: %s%n", rectangle);
        }
        return changed;
    }

    // Rule 3: If the extra candidates are a naked subset,
    // the extra candidates can be removed from all other common isPeerOf of the roof cells.
    private List<Cell> rule3(Rectangle rectangle) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : rectangle.cellC().getCommonUnitType(rectangle.cellD())) {
            var cells = grid.getCells(Predicates.inUnit(unitType, rectangle.cellC().getUnitIndex(unitType))
                    .and(Predicates.unsolvedCells)
                    .and(cell -> cell != rectangle.cellC() && cell != rectangle.cellD()));
            List<Cell> subset = getNakedSubset(cells, rectangle.extraCandidates());
            if (subset.isEmpty()) continue;
            for (Cell cell : cells) {
                if (cell == rectangle.cellC() || cell == rectangle.cellD() || subset.contains(cell)) continue;
                var removed = cell.removeCandidates(rectangle.extraCandidates());
                if (removed.isEmpty()) continue;
                log("- Removed candidate(s) %s from %s%n", removed, cell);
                changed.add(cell);
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter();
            log(0, "Unique Rectangle Type 3 found: %s%n", rectangle);
        }
        return changed;
    }

    // Rule 4: If the roof cells are conjugate pairs, the other candidate can be removed from the roof cells.
    private List<Cell> rule4(Rectangle rectangle) {
        int digit = -1;
        if (grid.isConjugatePair(rectangle.cellC(), rectangle.cellD(), rectangle.firstCommonCandidate())) {
            digit = rectangle.secondCommonCandidate();
        } else if (grid.isConjugatePair(rectangle.cellC(), rectangle.cellD(), rectangle.secondCommonCandidate())) {
            digit = rectangle.firstCommonCandidate();
        }
        if (digit == -1) return List.of();
        log("Unique Rectangle Type 4 found: %s%n", rectangle);
        return removeCandidateFromCellsAndLog(rectangle.roofCells(), digit);
    }

    // Rule 5: If the roof cells are diagonally opposed and one of the common candidates is a conjugate pair,
    // the other candidate can be removed from the floor cells.
    private List<Cell> rule5(Rectangle rectangle) {
        if (rectangle.cellA.isPeer(rectangle.cellB)) return List.of();
        Map<Cell, Integer> candidatesToRemove = new HashMap<>();
        for (int digit : List.of(rectangle.firstCommonCandidate(), rectangle.secondCommonCandidate())) {
            for (Cell cell : List.of(rectangle.cellA, rectangle.cellB)) {
                if (grid.isConjugatePair(cell, rectangle.cellC, digit) && grid.isConjugatePair(cell, rectangle.cellD, digit)) {
                    int otherDigit = rectangle.commonCandidates().stream().filter(b -> b != digit).findFirst().orElseThrow();
                    candidatesToRemove.put(cell, otherDigit);
                }
            }
        }
        if (candidatesToRemove.isEmpty()) return List.of();
        incrementCounter();
        log("Unique Rectangle Type 5 found: %s%n", rectangle);
        candidatesToRemove.keySet().forEach(cell -> {
            int digit = candidatesToRemove.get(cell);
            cell.removeCandidate(digit);
            log("- Removed candidate %d from %s%n", digit, cell);
        });
        return new ArrayList<>(candidatesToRemove.keySet());
    }

    private List<Cell> getNakedSubset(Cell[] cells, BitSet extraCandidates) {
        for (int k = 2; k <= cells.length; k++) {
            List<List<Cell>> subsets = getSubsets(List.of(cells), k - 1); // We need k-1 cells
            for (List<Cell> subset : subsets) {
                BitSet unionCandidates = new BitSet(9);
                subset.forEach(cell -> unionCandidates.or(cell.candidates()));
                if (unionCandidates.cardinality() == k && extraCandidates.stream().allMatch(unionCandidates::get)) {
                    return subset; // Return as soon as a valid subset is found
                }
            }
        }
        return List.of(); // Return an empty list if no subset is found
    }

    private List<List<Cell>> getSubsets(List<Cell> cells, int size) {
        List<List<Cell>> subsets = new ArrayList<>();
        generateSubsets(cells, size, 0, new ArrayList<>(), subsets);
        return subsets;
    }

    private void generateSubsets(List<Cell> cells, int size, int index, List<Cell> current, List<List<Cell>> subsets) {
        if (current.size() == size) {
            subsets.add(new ArrayList<>(current));
            return;
        }
        for (int i = index; i < cells.size(); i++) {
            current.add(cells.get(i));
            generateSubsets(cells, size, i + 1, current, subsets);
            current.removeLast();
        }
    }

    private List<Rectangle> getRectangles() {
        List<Rectangle> rectangles = new ArrayList<>();
        for (Cell cell1 : grid.getCells(Predicates.biValueCells)) {
            BitSet candidates = cell1.candidates();
            for (Cell cell2 : grid.getCells(Predicates.isPeerOf(cell1)
                    .and(Predicates.inUnit(UnitType.BOX, cell1.getBox()))
                    .and((Predicates.inUnit(UnitType.ROW, cell1.getRow())).or(Predicates.inUnit(UnitType.COL, cell1.getCol())))
                    .and(Predicates.containsAllCandidates(candidates)))) {
                for (Cell cell3 : grid.getCells(Predicates.isPeerOf(cell1)
                        .and(Predicates.isPeerOf(cell2).negate())
                        .and(Predicates.inUnit(UnitType.BOX, cell1.getBox()).negate())
                        .and(Predicates.containsAllCandidates(candidates)))) {
                    Cell cell4 = grid.findFourthCorner(cell1, cell2, cell3);
                    if (!cell4.isSolved() && cell4.hasCandidates(candidates) && isValidRectangle(cell1, cell2, cell3, cell4)) {
                        if (cell2.isBiValue()) {
                            rectangles.add(new Rectangle(cell1, cell2, cell3, cell4));
                        } else if (cell3.isBiValue()) {
                            rectangles.add(new Rectangle(cell1, cell3, cell2, cell4));
                        } else if (cell4.isBiValue()) {
                            rectangles.add(new Rectangle(cell1, cell4, cell3, cell2));
                        }
                    }
                }
            }
        }
        return rectangles;
    }

    private boolean isValidRectangle(Cell... cells) {
        return Arrays.stream(cells).map(Cell::getRow).distinct().count() == 2 &&
                Arrays.stream(cells).map(Cell::getCol).distinct().count() == 2 &&
                Arrays.stream(cells).map(Cell::getBox).distinct().count() == 2;
    }

    record Rectangle(Cell cellA, Cell cellB, Cell cellC, Cell cellD) {
        public Cell[] cells() {
            return new Cell[]{cellA, cellB, cellC, cellD};
        }

        public List<Cell> roofCells() {
            return List.of(cellC, cellD);
        }

        public BitSet commonCandidates() {
            return cellA.candidates();
        }

        public int firstCommonCandidate() {
            return cellA.getFirstCandidate();
        }

        public int secondCommonCandidate() {
            return commonCandidates().nextSetBit(firstCommonCandidate() + 1);
        }

        public BitSet extraCandidates() {
            BitSet extraCandidates = (BitSet) cellC.candidates().clone();
            extraCandidates.or(cellD.candidates());
            extraCandidates.andNot(cellA.candidates());
            return extraCandidates;
        }

        public boolean bothRoofAreTriValue() {
            return cellC.getCandidateCount() == 3 && cellD.getCandidateCount() == 3;
        }

        public boolean roofHasSameCandidates() {
            return cellC.candidates().equals(cellD.candidates());
        }

        @Override
        public String toString() {
            return String.format("[%s, %s, %s, %s]", cellA, cellB, cellC, cellD);
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(Rectangle rectangle);
    }
}