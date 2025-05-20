package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.*;

import java.util.*;

import static org.anne.sudoku.model.UnitType.*;


/**
 * When 2 of the 3 cells in a box-line intersection together contain 3 or 4 candidates,
 * then in each of the two boxes in the same chute but in different lines,
 * if there are cells with the same 3 or 4 candidates, any others can be removed.
 */

/*
     1  2  3   4  5  6    7  8  9
   +---------+----------+----------+
 A | B  B  * | *  .  .  | *  .  .  |
 B | .  .  * | T1 M2 M2 | C2 .  .  |
 C | .  .  * | C1 .  .  | T2 M1 M1 |
   +---------+----------+----------+
 D | .  .  S | S  .  .  | S  .  .  |
 E | .  .  S | S  .  .  | S  .  .  |
 F | .  .  S | S  .  .  | S  .  .  |
   +---------+----------+----------+
 G | .  .  S | S  .  .  | S  .  .  |
 H | .  .  S | S  .  .  | S  .  .  |
 J | .  .  S | S  .  .  | S  .  .  |
   +---------+----------+----------+
          CLb CL1        CL2
 */
public class Exocets extends SolvingTechnique {
    public Exocets() {
        super("Exocets", Grade.INSANE);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Cell> changed = new ArrayList<>();
        Set<Exocet> exocets = findExocets();

        if (exocets.size() > 1) {
            for (Exocet exocet1 : exocets) {
                for (Exocet exocet2 : exocets) {
                    if (exocet1.equals(exocet2)) continue;
                    // Check if the two exocets have the same candidates and sCells
                    Cell[] sCells1 = getSCells(exocet1.base1, exocet1.base2, exocet1.mirror1, exocet1.mirror2, exocet1.unitType());
                    Cell[] sCells2 = getSCells(exocet2.base1, exocet2.base2, exocet2.mirror1, exocet2.mirror2, exocet1.unitType());
                    if (exocet1.candidates.equals(exocet2.candidates) && Arrays.equals(sCells1, sCells2)) {
                        changed.addAll(doubleExocet(exocet1, exocet2, sCells1));
                    }
                }
            }
        }
        for (Exocet exocet : exocets) {
            changed.addAll(applyEliminationRules(exocet));
        }
        if (!changed.isEmpty()) {
            incrementCounter();
        }
        return changed;
    }

    private List<Cell> applyEliminationRules(Exocet exocet) {
        List<Cell> changed = new ArrayList<>();
        StringBuilder logBuilder = new StringBuilder();

        changed.addAll(eliminationRule1(exocet, logBuilder));
        changed.addAll(eliminationRule8(exocet, logBuilder));

        if (!changed.isEmpty()) {
            log("Exocet pattern found: %s%n%s", exocet, logBuilder);
        }
        return changed;
    }

    private List<Cell> doubleExocet(Exocet exocet1, Exocet exocet2, Cell[] sCells) {
        List<Cell> changed = new ArrayList<>();
        BitSet candidates = exocet1.candidates;
        Cell[] bases = {exocet1.base1, exocet1.base2, exocet2.base1, exocet2.base2};
        Cell[] targets = {exocet1.target1, exocet1.target2, exocet2.target1, exocet2.target2};
        // Rule 1: If a cell with a base candidate can see all four target cells or all four base cells, this digit can be removed from the cell
        for (Cell cell : grid.getCells(cell -> cell.candidates().intersects(candidates))) {
            if (Arrays.stream(bases).allMatch(cell::isPeer) || Arrays.stream(targets).allMatch(cell::isPeer)) {
                BitSet removed = cell.removeCandidates(candidates);
                if (!removed.isEmpty()) {
                    changed.add(cell);
                    log("- Removed %s from %s (double exocet rule 1)%n", removed, cell);
                }
            }
        }
        // Rule 2: base digits in non-SCells in their cover houses can be removed.
        for (int unitIndex : Arrays.stream(sCells).mapToInt(cell -> cell.getUnitIndex(exocet1.unitType())).distinct().toArray()) {
            BitSet toRemove = Arrays.stream(sCells)
                    .filter(Predicates.inUnit(exocet1.unitType(), unitIndex))
                    .map(Cell::candidates)
                    .reduce(new BitSet(), (a, b) -> {
                        a.or(b);
                        return a;
                    });
            toRemove.and(candidates);
            for (Cell cell : grid.getCells(Predicates.inUnit(exocet1.unitType(), unitIndex).and(Predicates.in(sCells).negate()).and(Predicates.intersectCandidates(toRemove)))) {
                BitSet removed = cell.removeCandidates(toRemove);
                if (!removed.isEmpty()) {
                    changed.add(cell);
                    log("- Removed %s from %s (double exocet rule 2)%n", removed, cell);
                }
            }
        }
        if (!changed.isEmpty()) {
            log(0, "Double Exocet pattern found: %s and %s%n", exocet1, exocet2);
        }
        return changed;
    }

    private List<Cell> eliminationRule8(Exocet exocet, StringBuilder logBuilder) {
        // Elimination Rule 8: any Base Candidate that can not be true in the Mirror Node associated with a Target Cell is false in that Target Cell
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : List.of(exocet.target1, exocet.target2)) {
            BitSet removed = (BitSet) cell.candidates().clone();
            removed.and(exocet.candidates);
            removed.andNot(cell == exocet.target1 ? exocet.mirror1Candidates() : exocet.mirror2Candidates());
            if (!removed.isEmpty()) {
                cell.removeCandidates(removed);
                changed.add(cell);
                logBuilder.append(String.format("- Removed %s from %s (rule 8)%n", removed, cell));
            }
        }
        return changed;
    }

    private List<Cell> eliminationRule1(Exocet exocet, StringBuilder logBuilder) {
        // Elimination Rule 1: Any candidate in a Target cell that is not one of the Base candidates can be removed
        List<Cell> changed = new ArrayList<>();
        for (Cell cell : List.of(exocet.target1, exocet.target2)) {
            BitSet removed = cell.removeAllBut(exocet.candidates);
            if (!removed.isEmpty()) {
                changed.add(cell);
                logBuilder.append(String.format("- Removed %s from %s (rule 1)%n", removed, cell));
            }
        }
        return changed;
    }

    private Set<Exocet> findExocets() {
        Set<Exocet> exocets = new HashSet<>();
        // Two Base cells exist in alignment in one box and contain three or four candidates *in total*.
        for (Cell base1 : grid.getCells(Predicates.cellsWithNCandidates(2, 4))) {
            for (Cell base2 : grid.getCells(Predicates.isPeerOf(base1)
                    .and(Predicates.cellsWithNCandidates(2, 4))
                    .and(Predicates.inUnit(BOX, base1.getBox()))
                    .and(Predicates.inUnit(ROW, base1.getRow()).or(Predicates.inUnit(COL, base1.getCol()))))) {

                BitSet combinedCandidates = combinedCandidates(base1, base2);
                if (combinedCandidates.cardinality() < 3 || combinedCandidates.cardinality() > 4) continue;

                UnitType unitType = base1.getRow() == base2.getRow() ? ROW : COL;
                List<Pair<Cell>> targets = getTargets(base1, unitType, combinedCandidates);
                if (targets.isEmpty()) continue;
                for (Pair<Cell> target : targets) {
                    Exocet exocet = createExocet(base1, base2, target, combinedCandidates, unitType);
                    if (exocet != null) {
                        exocets.add(exocet);
                    }
                }
            }
        }
        return exocets;
    }

    private Exocet createExocet(Cell base1, Cell base2, Pair<Cell> target, BitSet combinedCandidates, UnitType unitType) {
        Cell target1 = target.first();
        Cell target2 = target.second();
        Cell[] mirrors1 = findMirrors(target2, base1, target1);
        Cell[] mirrors2 = findMirrors(target1, base1, target2);

        Cell[] sCells = findSCells(base1, base2, mirrors1, mirrors2, unitType);
        if (isValidExocetPattern(sCells, combinedCandidates, unitType)) {
            return new Exocet(base1, base2, target1, target2, mirrors1, mirrors2, combinedCandidates);
        }
        return null;
    }

    private Cell[] findMirrors(Cell target, Cell base, Cell otherTarget) {
        return grid.getCells(cell -> cell.getBox() == target.getBox() && !cell.isPeer(otherTarget) && !cell.isPeer(base) && cell != target);
    }

    private Cell[] findSCells(Cell base1, Cell base2, Cell[] mirrors1, Cell[] mirrors2, UnitType unitType) {
        return grid.getCells(Predicates.inChute(unitType, base1).negate()
                .and(Predicates.isPeerOf(base1).negate())
                .and(Predicates.isPeerOf(base2).negate())
                .and(cell -> Arrays.stream(mirrors1).noneMatch(cell::isPeer))
                .and(cell -> Arrays.stream(mirrors2).noneMatch(cell::isPeer)));
    }

    // All instances of each Base Digit as a candidate or a given or a solved value in the S Cells
    // must be confined to no more than two Cover Houses
    private boolean isValidExocetPattern(Cell[] sCells, BitSet combinedCandidates, UnitType unitType) {
        for (int candidate : combinedCandidates.stream().toArray()) {
            long distinctUnits = Arrays.stream(sCells)
                    .filter(cell -> cell.hasCandidate(candidate) || cell.value() == candidate)
                    .mapToInt(cell -> cell.getUnitIndex(unitType))
                    .distinct()
                    .count();
            if (distinctUnits > 2) return false;
        }
        return true;
    }

    private Cell[] getSCells(Cell base1, Cell base2, Cell[] mirror1, Cell[] mirror2, UnitType unitType) {
        return grid.getCells(Predicates.inChute(unitType, base1).negate().and(Predicates.isPeerOf(base1).negate().and(Predicates.isPeerOf(base2).negate()))
                .and(cell -> Arrays.stream(mirror1).noneMatch(cell::isPeer) && Arrays.stream(mirror2).noneMatch(cell::isPeer)));
    }

    private List<Pair<Cell>> getTargets(Cell base, UnitType unitType, BitSet combinedCandidates) {
        List<Pair<Cell>> targets = new ArrayList<>();
        Cell[] cells = grid.getCells(Predicates.inChute(unitType, base).and(
                cell -> cell.getBox() != base.getBox() && !cell.isPeer(base) && cell.hasCandidates(combinedCandidates)));
        for (Cell cell1 : cells) {
            for (Cell cell2 : cells) {
                if (cell1 == cell2 || cell1.isPeer(cell2)) continue;
                targets.add(new Pair<>(cell1, cell2));
            }
        }
        return targets;
    }

    record Exocet(Cell base1, Cell base2, Cell target1, Cell target2/*, Cell companion1, Cell companion2*/,
                  Cell[] mirror1, Cell[] mirror2, BitSet candidates) {

        BitSet mirror1Candidates() {
            return combinedCandidates(mirror1);
        }

        BitSet mirror2Candidates() {
            return combinedCandidates(mirror2);
        }

        UnitType unitType() {
            return base1.getRow() == base2.getRow() ? ROW : COL;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Exocet other)) return false;
            return ((base1.equals(other.base1) && base2.equals(other.base2)) || (base1.equals(other.base2) && base2.equals(other.base1)))
                    && ((target1.equals(other.target1) && target2.equals(other.target2)) || (target1.equals(other.target2) && target2.equals(other.target1)));
        }

        @Override
        public int hashCode() {
            return base1.hashCode() + base2.hashCode() + target1.hashCode() + target2.hashCode() + candidates.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Base (%s %s), Target (%s, %s), Mirrors (%s and %s) Candidates %s",
                    base1, base2, target1, target2, /*companion1, companion2, */Arrays.stream(mirror1).toList(), Arrays.stream(mirror2).toList(), candidates);
        }
    }
}
