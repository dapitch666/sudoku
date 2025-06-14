package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExtendedUniqueRectangles extends SolvingTechnique {
    public ExtendedUniqueRectangles() {
        super("Extended Unique Rectangles", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Rule> rules = List.of(this::rule1, this::rule2, this::rule4);
        List<Rectangle> rectangles = getRectangles();
        for (Rectangle rectangle : rectangles) {
            for (Rule rule : rules) {
                List<Cell> changed = rule.apply(rectangle);
                if (!changed.isEmpty()) return changed;
            }
        }
        return List.of();
    }

    // Rule 1: Remove floor candidates from a roof cell
    private List<Cell> rule1(Rectangle rectangle) {
        BitSet floorCandidates = rectangle.floorCandidates();
        List<Cell> roofCell = Arrays.stream(rectangle.roof())
                .filter(cell -> getExtraCandidates(cell, floorCandidates).cardinality() > 0)
                .toList();
        if (roofCell.size() != 1) return List.of();
        Cell cell = roofCell.getFirst();
        var removed = cell.removeCandidates(floorCandidates);
        if (removed.isEmpty()) return List.of();
        incrementCounter();
        log("Extended Unique Rectangles Type 1 found %s%n- Removed %s from %s%n", rectangle, removed, cell);
        return List.of(cell);
    }

    // Rule 2: Remove a candidate from peers of two roof cells if they share the same extra candidate
    private List<Cell> rule2(Rectangle rectangle) {
        BitSet floorCandidates = rectangle.floorCandidates();
        Map<Cell, Integer> roofExtraCandidates = Arrays.stream(rectangle.roof())
                .filter(cell -> getExtraCandidates(cell, floorCandidates).cardinality() == 1)
                .collect(Collectors.toMap(cell -> cell, cell -> getExtraCandidates(cell, floorCandidates).nextSetBit(0)));
        if (roofExtraCandidates.size() != 2 ||
                !roofExtraCandidates.get(rectangle.cell5).equals(roofExtraCandidates.get(rectangle.cell6)))
            return List.of();
        int digit = roofExtraCandidates.get(rectangle.cell5);
        List<Cell> peers = Arrays.stream(grid.getCells(Predicates.isPeerOf(rectangle.cell5, rectangle.cell6)
                        .and(Predicates.containsCandidate(digit))))
                .toList();
        List<Cell> changed = removeCandidateFromCellsAndLog(peers, digit);
        if (!changed.isEmpty()) {
            log(0, "Extended Unique Rectangles Type 2 found %s%n", rectangle);
        }
        return changed;
    }

    // Rule 4: Handle cases where floor cells have exactly two candidates and roof cells share a conjugate pair
    private List<Cell> rule4(Rectangle rectangle) {
        if (!Arrays.stream(rectangle.floor()).allMatch(cell -> cell.getCandidateCount() == 2)
                || !rectangle.cell1.candidates().equals(rectangle.cell2.candidates())
                || !rectangle.cell3.candidates().equals(rectangle.cell4.candidates())) {
            return List.of();
        }
        BitSet floorCandidates = rectangle.floorCandidates();
        List<Integer> distinctFloorCandidates = floorCandidates.stream()
                .filter(digit -> rectangle.cell1.hasCandidate(digit) ^ rectangle.cell3.hasCandidate(digit))
                .boxed()
                .toList();
        for (int digit : distinctFloorCandidates) {
            if ((rectangle.cell5.hasCandidate(digit) || rectangle.cell6.hasCandidate(digit))
                    && grid.isConjugatePair(rectangle.cell5, rectangle.cell6, digit)) continue;
            log("Extended Unique Rectangles Type 4 found %s%n", rectangle);
            return removeCandidateFromCellsAndLog(Arrays.stream(rectangle.roof()).toList(), digit);
        }
        return List.of();
    }

    // Get the extra candidates of a cells that are not part of the given candidates
    private BitSet getExtraCandidates(Cell cell, BitSet candidates) {
        BitSet extraCandidates = (BitSet) cell.candidates().clone();
        extraCandidates.andNot(candidates);
        return extraCandidates;
    }

    // Find 6 cells that form a rectangle (2 rows and 3 columns or vice versa) in 3 boxes (2 in each box)
    // four of them (the floor) must have a total of 3 common candidates
    // and two of them (the roof) can have extra candidates
    private List<Rectangle> getRectangles() {
        Set<Rectangle> rectangles = new HashSet<>();
        var cellsWith2or3Candidates = grid.getCells(Predicates.unsolvedCells.and(c -> c.getCandidateCount() <= 3));
        for (Cell cellA : cellsWith2or3Candidates) {
            for (Cell cellB : Arrays.stream(cellsWith2or3Candidates).filter(Predicates.inUnit(UnitType.BOX, cellA.getBox())).toList()) {
                if (cellA == cellB || (cellA.getRow() != cellB.getRow() && cellA.getCol() != cellB.getCol())
                        || !cellA.candidates().intersects(cellB.candidates())) continue;
                BitSet mergedCandidates = Helper.mergedCandidates(cellA, cellB);
                if (mergedCandidates.cardinality() > 3) continue;

                for (Cell cellC : Arrays.stream(cellsWith2or3Candidates)
                        .filter(Predicates.inUnit(UnitType.BOX, cellA.getBox()).negate()
                                .and(Predicates.isPeerOf(cellA))
                                .and(Predicates.intersectCandidates(mergedCandidates)))
                        .toList()) {
                    mergedCandidates = Helper.mergedCandidates(cellA, cellB, cellC);
                    if (mergedCandidates.cardinality() > 3) continue;

                    Cell cellD = grid.findFourthCorner(cellA, cellB, cellC);
                    if (cellD.isSolved()) continue;
                    mergedCandidates = Helper.mergedCandidates(cellA, cellB, cellC, cellD);
                    if (mergedCandidates.cardinality() != 3) continue;

                    int targetBox = targetBox(cellA.getBox(), cellC.getBox());
                    for (Cell cellE : grid.getCells(Predicates.isPeerOf(cellA)
                            .and(Predicates.inUnit(UnitType.BOX, targetBox))
                            .and(Predicates.intersectCandidates(mergedCandidates)))) {
                        Cell cellF = grid.findFourthCorner(cellA, cellB, cellE);
                        if (cellF.isSolved() || !cellF.candidates().intersects(mergedCandidates)) continue;

                        if (isValidRectangle(cellA, cellB, cellC, cellD, cellE, cellF)) {
                            rectangles.add(new Rectangle(cellA, cellB, cellC, cellD, cellE, cellF));
                        }
                    }
                }
            }
        }
        return new ArrayList<>(rectangles);
    }

    // Check if the given cells form a valid rectangle:
    // the cells are in 3 distinct boxes and are either:
    // - in 2 distinct rows and 3 distinct columns, or
    // - in 3 distinct rows and 2 distinct columns.
    private boolean isValidRectangle(Cell... cells) {
        boolean distinctBoxes = Arrays.stream(cells).map(Cell::getBox).distinct().count() == 3;
        boolean twoRowsThreeCols = Arrays.stream(cells).map(Cell::getRow).distinct().count() == 2 &&
                Arrays.stream(cells).map(Cell::getCol).distinct().count() == 3;
        boolean threeRowsTwoCols = Arrays.stream(cells).map(Cell::getRow).distinct().count() == 3 &&
                Arrays.stream(cells).map(Cell::getCol).distinct().count() == 2;
        return distinctBoxes && (twoRowsThreeCols || threeRowsTwoCols);
    }

    // Determine the third box in a chute based on the first two boxes
    private int targetBox(int boxA, int boxC) {
        if (boxA / 3 == boxC / 3) { // The boxes are aligned horizontally
            return 3 * (boxA / 3) + (3 - (boxA % 3 + boxC % 3));
        } else if (boxA % 3 == boxC % 3) { // The boxes are aligned vertically
            return (boxA % 3) + 3 * (3 - (boxA / 3 + boxC / 3));
        }
        throw new IllegalArgumentException("Boxes are not aligned in the same chute");
    }

    record Rectangle(Cell cell1, Cell cell2, Cell cell3, Cell cell4, Cell cell5, Cell cell6) {
        public Cell[] floor() {
            return new Cell[]{cell1, cell2, cell3, cell4};
        }

        public Cell[] roof() {
            return new Cell[]{cell5, cell6};
        }

        public Cell[] cells() {
            return Stream.concat(Arrays.stream(floor()), Arrays.stream(roof())).toArray(Cell[]::new);
        }

        public BitSet floorCandidates() {
            return Helper.mergedCandidates(floor());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Rectangle rectangle)) return false;
            return Arrays.stream(floor())
                    .allMatch(cell -> Arrays.asList(rectangle.floor()).contains(cell))
                    && Arrays.stream(roof())
                    .allMatch(cell -> Arrays.asList(rectangle.roof()).contains(cell));
        }

        @Override
        public int hashCode() {
            return cell1.hashCode() + cell2.hashCode() + cell3.hashCode() + cell4.hashCode()
                    + cell5.hashCode() + cell6.hashCode();
        }

        @Override
        public String toString() {
            return Arrays.toString(cells());
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(Rectangle rectangle);
    }
}
