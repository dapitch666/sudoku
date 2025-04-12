package org.anne.sudoku.grader.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.grader.Cell;
import org.anne.sudoku.grader.Grid;
import org.anne.sudoku.grader.UnitType;

import java.util.*;
import java.util.stream.Collectors;

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

        for (Rule rule : rules) {
            for (Rectangle rectangle : rectangles) {
                List<Cell> changed = rule.apply(rectangle);
                if (!changed.isEmpty()) return changed;
            }
        }
        // No changes made
        return List.of();
    }

    private List<Cell> rule1(Rectangle rectangle) {
        if (!rectangle.cellC().isBiValue() && !rectangle.cellD().isBiValue()) return List.of();
        Cell cell = rectangle.cellC().isBiValue() ? rectangle.cellD() : rectangle.cellC();
        var removed = cell.removeCandidates(rectangle.commonCandidates());
        if (!removed.isEmpty()) {
            log("Unique Rectangle Type 1 found: %s. Removed candidates %s from %s%n", rectangle, removed, cell);
            incrementCounter();
            return List.of(cell);
        }
        return List.of();
    }

    private List<Cell> rule2(Rectangle rectangle) {
        if (!rectangle.bothRoofAreTriValue() || !rectangle.roofHasSameCandidates()) return List.of();
        int digit = rectangle.cellC().getCandidates().stream().filter(d -> !rectangle.commonCandidates().contains(d)).findFirst().orElseThrow();
        var changed = grid.getCommonPeersWithCandidate(rectangle.cellC(), rectangle.cellD(), digit);
        if (!changed.isEmpty()) {
            log("Unique Rectangle Type 2 found: %s. Removed candidates %d from %s%n", rectangle, digit, changed);
            changed.forEach(cell -> cell.removeCandidate(digit));
            incrementCounter();
            return changed;
        }
        return List.of();
    }

    private List<Cell> rule3(Rectangle rectangle) {
        List<Cell> changed = new ArrayList<>();
        for (UnitType unitType : rectangle.cellC().getCommonUnitType(rectangle.cellD())) {
            List<Cell> cells = Arrays.stream(grid.getCells(unitType, rectangle.cellC().getUnitIndex(unitType)))
                    .filter(Cell::isNotSolved)
                    .filter(cell -> cell != rectangle.cellC() && cell != rectangle.cellD())
                    .toList();
            List<Integer> extraCandidates = rectangle.extraCandidates();
            List<Cell> kCells = getNakedSubset(cells, extraCandidates);
            if (kCells.isEmpty()) continue;
            for (Cell cell : cells) {
                if (cell == rectangle.cellC() || cell == rectangle.cellD()) continue;
                if (kCells.contains(cell)) {
                    continue;
                }
                List<Integer> removed = cell.removeCandidates(extraCandidates);
                if (!removed.isEmpty()) {
                    log("Removed candidates %s from %s%n", removed, cell);
                    changed.add(cell);
                }
            }
        }
        if (!changed.isEmpty()) {
            incrementCounter();
            log(0, "Unique Rectangle Type 3 found: %s.%n", rectangle);
            return changed;
        }
        return List.of();
    }

    private List<Cell> rule4(Rectangle rectangle) {
        int digit = -1;
        if (grid.isConjugatePair(rectangle.cellC(), rectangle.cellD(), rectangle.commonCandidates().getFirst())) {
            digit = rectangle.commonCandidates().getLast();
        } else if (grid.isConjugatePair(rectangle.cellC(), rectangle.cellD(), rectangle.commonCandidates().getLast())) {
            digit = rectangle.commonCandidates().getFirst();
        }
        if (digit == -1)     return List.of();
        log("Unique Rectangle Type 4 found: %s.%nRemoving %d from roof cells [%s, %s]%n", rectangle, digit, rectangle.cellC(), rectangle.cellD());
        rectangle.cellC().removeCandidate(digit);
        rectangle.cellD().removeCandidate(digit);
        incrementCounter();
        return List.of(rectangle.cellC(), rectangle.cellD());
    }

    private List<Cell> rule5(Rectangle rectangle) {
        if (rectangle.cellA.isPeer(rectangle.cellB)) return List.of();
        for (int digit : rectangle.commonCandidates()) {
            if (grid.isConjugatePair(rectangle.cellA, rectangle.cellC, digit) && grid.isConjugatePair(rectangle.cellA, rectangle.cellD, digit)) {
                int digitToRemove = rectangle.commonCandidates().stream().filter(d -> d != digit).findFirst().orElseThrow();
                log("Unique Rectangle Type 5 found: %s.%nRemoving %d from roof cells [%s, %s]%n", rectangle, digitToRemove, rectangle.cellA, rectangle.cellB);
                rectangle.cellA.removeCandidate(digitToRemove);
                rectangle.cellB.removeCandidate(digitToRemove);
                incrementCounter();
                return List.of(rectangle.cellA, rectangle.cellB);
            }
        }
        return List.of();
    }

    private List<Cell> getNakedSubset(List<Cell> cells, List<Integer> extraCandidates) {
        for (int k = 2; k <= cells.size(); k++) {
            int subsetSize = k - 1; // We need k-1 cells
            List<List<Cell>> subsets = getSubsets(cells, subsetSize);
            for (List<Cell> subset : subsets) {
                Set<Integer> unionCandidates = new HashSet<>();
                subset.forEach(cell -> unionCandidates.addAll(cell.getCandidates()));
                if (unionCandidates.size() == k && unionCandidates.containsAll(extraCandidates)) {
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
        Cell[] biValueCells = grid.getBiValueCells();
        for (int i = 0; i < biValueCells.length; i++) {
            Cell cellA = biValueCells[i];
            List<Integer> candidates = cellA.getCandidates();
            for (int j = i + 1; j < biValueCells.length; j++) {
                Cell cellB = biValueCells[j];
                // CellA and cellB must share the same candidates
                if (!candidates.equals(cellB.getCandidates())) continue;
                // Find a cell that has both cellA candidates perpendicular to cellA cellB
                Cell[] cells;
                if (cellA.getRow() == cellB.getRow()) {
                    cells = grid.getCellsInUnitWithCandidates(candidates, UnitType.COL, cellA.getCol());
                } else if (cellA.getCol() == cellB.getCol()) {
                    cells = grid.getCellsInUnitWithCandidates(candidates, UnitType.ROW, cellA.getRow());
                } else if (cellA.getBox() == cellB.getBox()) {
                    continue;
                } else if (cellA.getHorizontalChute() == cellB.getHorizontalChute() || cellA.getVerticalChute() == cellB.getVerticalChute()) {
                    cells = grid.getCellsInUnitWithCandidates(candidates, UnitType.BOX, cellA.getBox());
                } else {
                    continue;
                }
                for (Cell cellD : cells) {
                    if (cellD == cellA || cellD == cellB) continue;
                    // Check if all three cells are in 2 different boxes, 2 different rows and 2 different columns
                    List<Cell> cellsList = List.of(cellA, cellB, cellD);
                    if (cellsList.stream().map(Cell::getRow).distinct().count() != 2 || cellsList.stream().map(Cell::getCol).distinct().count() != 2 || cellsList.stream().map(Cell::getBox).distinct().count() != 2) {
                        continue;
                    }
                    // Find the fourth cell
                    Cell cellC = grid.findFourthCorner(cellA, cellB, cellD);
                    // Check if all cellA candidates are also cellC candidates
                    if (cellC.isCandidate(candidates.getFirst()) && cellC.isCandidate(candidates.getLast())) {
                        rectangles.add(new Rectangle(cellA, cellB, cellC, cellD));
                    }
                }
            }
        }
        return rectangles;
    }

    record Rectangle(Cell cellA, Cell cellB, Cell cellC, Cell cellD) {
        public Cell[] cells() {
            return new Cell[]{cellA, cellB, cellC, cellD};
        }

        public List<Integer> commonCandidates() {
            return cellA.getCandidates();
        }

        public List<Integer> extraCandidates() {
            return Arrays.stream(cells())
                    .flatMap(c -> c.getCandidates().stream())
                    .filter(c -> !commonCandidates().contains(c))
                    .distinct()
                    .collect(Collectors.toList());
        }

        public boolean bothRoofAreTriValue() {
            return cellC.getCandidateCount() == 3 && cellD.getCandidateCount() == 3;
        }

        public boolean roofHasSameCandidates() {
            return cellC.getCandidates().equals(cellD.getCandidates());
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
