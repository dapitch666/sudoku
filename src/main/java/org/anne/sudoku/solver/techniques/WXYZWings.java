package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Predicates;
import org.anne.sudoku.model.UnitType;

import java.util.*;
import java.util.stream.Collectors;

public class WXYZWings extends SolvingTechnique {
    public WXYZWings() {
        super("WXYZ-Wings", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Rule> rules = List.of(this::rule2, this::rule1);
        List<Formation> formations = findWXYZWings();
        for (Rule rule : rules) {
            for (Formation formation : formations) {
                List<Cell> changed = rule.apply(formation);
                if (!changed.isEmpty()) return changed;
            }
        }
        return List.of();
    }

    private List<Cell> rule1(Formation formation) {
        if (formation.z == -1) return List.of();
        // Remove the non-restricted candidate from the cells that can see all the cells in the formation with the same candidate
        List<Cell> changed = Arrays.stream(grid.getCells(Predicates.containsCandidate(formation.z).and(Predicates.in(formation.cells).negate())))
                .filter(cell -> formation.getCellsWithZ().stream().allMatch(cell::isPeer))
                .toList();
        if (!changed.isEmpty()) {
            log("WXYZ-Wing type 1 found in %s%n", formation);
            removeCandidateFromCellsAndLog(changed, formation.z);
        }
        return changed;
    }

    private List<Cell> rule2(Formation formation) {
        if (formation.z != -1) return List.of();
        // Group cells by box
        int hingeBox = Arrays.stream(formation.cells)
                .collect(Collectors.groupingBy(Cell::getBox))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() == 3)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
        if (hingeBox == -1) return List.of();

        // Group cells by row and column
        Map<Integer, List<Cell>> rows = Arrays.stream(formation.cells).collect(Collectors.groupingBy(Cell::getRow));
        Map<Integer, List<Cell>> cols = Arrays.stream(formation.cells).collect(Collectors.groupingBy(Cell::getCol));

        // Identify the hinge (two cells in the same box and row/column)
        List<Cell> hinge = Arrays.stream(formation.cells)
                .filter(cell -> cell.getBox() == hingeBox &&
                        (rows.values().stream().anyMatch(r -> r.contains(cell) && r.size() == 3) ||
                        cols.values().stream().anyMatch(c -> c.contains(cell) && c.size() == 3)))
                .toList();
        if (hinge.size() != 2) return List.of();

        // Check if the hinge collectively contains exactly four candidates
        if (!formation.combinedCandidates.equals(combinedCandidates(hinge.get(0), hinge.get(1)))) return List.of();

        // Identify the other two cells
        List<Cell> otherCells = Arrays.stream(formation.cells)
                .filter(cell -> !hinge.contains(cell))
                .toList();

        // Validate the other two cells
        Cell boxCell = otherCells.stream().filter(cell -> cell.getBox() == hingeBox).findFirst().orElse(null);
        Cell rowOrColCell = otherCells.stream().filter(cell -> cell.getBox() != hingeBox).findFirst().orElse(null);

        if (boxCell == null || rowOrColCell == null || boxCell.isPeer(rowOrColCell)
                || boxCell.candidates().intersects(rowOrColCell.candidates())
                || !formation.combinedCandidates.equals(combinedCandidates(boxCell, rowOrColCell)))
            return List.of();

        // Candidates elimination
        List<Cell> changes = new ArrayList<>();
        for (Cell cell : grid.getCells(Predicates.inUnit(UnitType.BOX, boxCell.getBox()).and(Predicates.in(formation.cells).negate())
                .and(Predicates.intersectCandidates(boxCell.candidates())))) {
            changes.add(cell);
            BitSet removed = cell.removeCandidates(boxCell.candidates());
            log("- Removed %s from %s%n", removed, cell);
        }
        for (Cell cell : grid.getCells(Predicates.isPeerOf(hinge.getFirst(), rowOrColCell)
                .and(Predicates.in(formation.cells).negate())
                .and(Predicates.intersectCandidates(rowOrColCell.candidates())))) {
            changes.add(cell);
            BitSet removed = cell.removeCandidates(rowOrColCell.candidates());
            log("- Removed %s from %s%n", removed, cell);
        }
        if (!changes.isEmpty()) {
            incrementCounter();
            log(0, "WXYZ-Wing type 2 found in %s%n", formation);
        }
        return changes;
    }

    private List<Formation> findWXYZWings() {
        Set<Formation> formations = new HashSet<>();
        Cell[] cells = grid.getCells(Predicates.unsolvedCells.and(cell -> cell.getCandidateCount() <= 4));
        for (Cell cellA : cells) {
            for (Cell cellB : grid.getCells(Predicates.in(cells).and(Predicates.isPeerOf(cellA)))) {
                if (combinedCandidates(cellA, cellB).cardinality() > 4) continue;
                for (Cell cellC : grid.getCells(Predicates.in(cells).and(Predicates.isPeerOf(cellA).or(Predicates.isPeerOf(cellB))))) {
                    if (List.of(cellA, cellB).contains(cellC) || combinedCandidates(cellA, cellB, cellC).cardinality() > 4)
                        continue;
                    for (Cell cellD : grid.getCells(Predicates.in(cells).and(Predicates.isPeerOf(cellA).or(Predicates.isPeerOf(cellB).or(Predicates.isPeerOf(cellC)))))) {
                        BitSet combinedCandidates = combinedCandidates(cellA, cellB, cellC, cellD);
                        if (List.of(cellA, cellB, cellC).contains(cellD) || combinedCandidates.cardinality() > 4)
                            continue;
                        // Check if the cells form a WXYZ-Wing pattern
                        Formation formation = new Formation(combinedCandidates, cellA, cellB, cellC, cellD);
                        if (formation.isValidWXYZWing()) formations.add(formation);
                    }
                }
            }
        }
        return new ArrayList<>(formations);
    }

    record Formation(BitSet combinedCandidates, int z, Cell... cells) {
        Formation(BitSet combinedCandidates, Cell... cells) {
            this(combinedCandidates, getZ(combinedCandidates, cells), cells);
        }

        private static int getZ(BitSet combinedCandidates, Cell[] cells) {
            BitSet nonRestrictedCandidates = new BitSet(9);
            for (int digit : combinedCandidates.stream().toArray()) {
                for (Cell cell1 : cells) {
                    if (!cell1.hasCandidate(digit)) continue;

                    for (Cell cell2 : cells) {
                        if (cell1 == cell2 || cell1.isPeer(cell2)) continue;
                        if (cell2.hasCandidate(digit)) {
                            nonRestrictedCandidates.set(digit);
                            break;
                        }
                    }
                }
            }
            if (nonRestrictedCandidates.cardinality() != 1) return -1;
            return nonRestrictedCandidates.nextSetBit(0);
        }

        public List<Cell> getCellsWithZ() {
            return Arrays.stream(cells).filter(Predicates.containsCandidate(z)).toList();
        }

        public boolean isValidWXYZWing() {
            if (cells.length != 4) return false;
            if (combinedCandidates.cardinality() != 4) return false;
            if (Arrays.stream(cells).mapToInt(Cell::getBox).distinct().count() !=2) return false;

            return true;
        }

        @Override
        public String toString() {
            return Arrays.toString(cells);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Formation formation)) return false;
            return z == formation.z && Arrays.stream(cells())
                    .allMatch(cell -> Arrays.asList(cells()).contains(cell));
        }

        @Override
        public int hashCode() {
            return Arrays.stream(cells).mapToInt(Cell::hashCode).sum() + z;
        }
    }

    @FunctionalInterface
    private interface Rule {
        List<Cell> apply(Formation formation);
    }
}
