package org.anne.sudoku.model;

import java.util.BitSet;
import java.util.List;
import java.util.function.Predicate;

public class Predicates {
    public static Predicate<Cell> solvedCells = Cell::isSolved;
    public static Predicate<Cell> unsolvedCells = cell -> !cell.isSolved();
    public static Predicate<Cell> justSolvedCells = Cell::justSolved;
    public static Predicate<Cell> biValueCells = Cell::isBiValue;

    public static Predicate<Cell> cellsWithNCandidates(int n) {
        return cell -> cell.getCandidateCount() == n;
    }

    public static Predicate<Cell> peers(Cell cell) {
        return c -> c.isPeer(cell);
    }

    public static Predicate<Cell> hasCandidate(int digit) {
        return cell -> cell.hasCandidate(digit);
    }

    public static Predicate<Cell> inUnit(UnitType unitType, int unitIndex) {
        return cell -> cell.getUnitIndex(unitType) == unitIndex;
    }

    public static Predicate<Cell> hasCandidates(List<Integer> candidates) {
        return cell -> candidates.stream().allMatch(cell::hasCandidate);
    }

    public static Predicate<Cell> hasCandidates(BitSet candidates) {
        return cell -> candidates.stream().allMatch(cell::hasCandidate);
    }

    public static Predicate<Cell> candidatesIntersect(BitSet candidates) {
        return cell -> cell.candidates().intersects(candidates);
    }
}
