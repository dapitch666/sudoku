package org.anne.sudoku.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    static Cell givenCell;
    static Cell emptyCell;
    static Cell biValueCell;

    @BeforeEach
    void setUp() {
        givenCell = new Cell(46, 8);
        emptyCell = new Cell(12, new BitSet(9));
        biValueCell = new Cell(30, new BitSet(9));

        emptyCell.candidates().set(2, 7); // Set candidates 2 to 6
        biValueCell.candidates().set(3, 5); // Set candidates 3 and 4
    }


    @Test
    void setValueTest() {
        assertEquals(8, givenCell.getValue());
        givenCell.setValue(5);
        assertEquals(5, givenCell.getValue());
        assertTrue(givenCell.justSolved());
    }

    @Test
    void clearTest() {
        assertEquals(8, givenCell.getValue());
        assertTrue(givenCell.candidates().isEmpty());
        givenCell.clear();
        assertEquals(0, givenCell.getValue());
        assertFalse(givenCell.candidates().isEmpty());
    }

    @Test
    void getRowTest() {
        assertEquals(5, givenCell.getRow());
    }

    @Test
    void getColTest() {
        assertEquals(1, givenCell.getCol());
    }

    @Test
    void getBoxTest() {
        assertEquals(3, givenCell.getBox());
    }

    @Test
    void getHorizontalChuteTest() {
        assertEquals(1, givenCell.getHorizontalChute());
    }

    @Test
    void getVerticalChuteTest() {
        assertEquals(0, givenCell.getVerticalChute());
    }

    @Test
    void getCandidatesTest() {
        assertTrue(givenCell.getCandidates().isEmpty());
        assertFalse(emptyCell.getCandidates().isEmpty());
        assertEquals(5, emptyCell.getCandidates().size());
    }

    @Test
    void getValueTest() {
        assertEquals(8, givenCell.getValue());
        assertEquals(0, emptyCell.getValue());
    }

    @Test
    void hasCandidateTest() {
        assertFalse(emptyCell.hasCandidate(1));
        assertTrue(emptyCell.hasCandidate(2));
    }

    @Test
    void hasCandidatesTest() {
        BitSet candidates = new BitSet();
        candidates.set(3, 5); // Set candidates 3 to 5
        assertTrue(emptyCell.hasCandidates(candidates));
    }

    @Test
    void getCandidateCountTest() {
        assertEquals(0, givenCell.getCandidateCount()); // No candidates when value is set
        assertEquals(5, emptyCell.getCandidateCount()); // All candidates should be available
    }

    @Test
    void isBiValueTest() {
        assertFalse(givenCell.isBiValue());
        assertFalse(emptyCell.isBiValue());
        assertTrue(biValueCell.isBiValue());
    }

    @Test
    void getFirstCandidateTest() {
        assertEquals(2, emptyCell.getFirstCandidate());
        assertEquals(3, biValueCell.getFirstCandidate());
    }

    @Test
    void getOtherCandidateTest() {
        assertEquals(4, biValueCell.getOtherCandidate(3));
        assertEquals(3, biValueCell.getOtherCandidate(4));
        assertThrows(IllegalStateException.class, () -> emptyCell.getOtherCandidate(4));
        assertThrows(IllegalArgumentException.class, () -> biValueCell.getOtherCandidate(8));
    }

    @Test
    void isPeerTest() {
        Cell peerCell = new Cell(47, 8); // Same row as givenCell
        Cell nonPeerCell = new Cell(12, 3); // Different row and column

        assertTrue(givenCell.isPeer(peerCell));
        assertFalse(givenCell.isPeer(nonPeerCell));
        assertFalse(givenCell.isPeer(givenCell)); // A cell should not be a peer of itself
    }

    @Test
    void isSolvedTest() {
        assertTrue(givenCell.isSolved());
        assertFalse(emptyCell.isSolved());
    }

    @Test
    void indexTest() {
        assertEquals(46, givenCell.index());
        assertEquals(12, emptyCell.index());
    }

    @Test
    void valueTest() {
        assertEquals(8, givenCell.value());
        assertEquals(0, emptyCell.value());
    }

    @Test
    void candidatesTest() {
        assertTrue(givenCell.candidates().isEmpty());
        assertFalse(emptyCell.candidates().isEmpty());
        assertEquals(5, emptyCell.candidates().cardinality());
    }

    @Test
    void getUnitIndexTest() {
        assertEquals(5, givenCell.getUnitIndex(UnitType.ROW));
        assertEquals(1, givenCell.getUnitIndex(UnitType.COL));
        assertEquals(3, givenCell.getUnitIndex(UnitType.BOX));

        assertEquals(1, emptyCell.getUnitIndex(UnitType.ROW));
        assertEquals(3, emptyCell.getUnitIndex(UnitType.COL));
        assertEquals(1, emptyCell.getUnitIndex(UnitType.BOX));
    }

    @Test
    void removeAllButTest() {
        BitSet valuesToKeep = new BitSet();
        valuesToKeep.set(3);
        valuesToKeep.set(5);
        BitSet removed = emptyCell.removeAllBut(valuesToKeep);

        assertEquals(3, removed.cardinality());
        assertEquals(emptyCell.candidates(), valuesToKeep);
    }

    @Test
    void removeAllButListTest() {
        BitSet removed = emptyCell.removeAllBut(Collections.singletonList(3));
        assertEquals(4, removed.cardinality()); // 2, 4, 5, 6 should be removed
        assertTrue(emptyCell.candidates().get(3)); // Only candidate 3 should remain
    }

    @Test
    void removeCandidatesTest() {
        BitSet valuesToRemove = new BitSet();
        valuesToRemove.set(2);
        valuesToRemove.set(3);
        BitSet removed = emptyCell.removeCandidates(valuesToRemove);

        assertEquals(2, removed.cardinality()); // 2 and 3 should be removed
        assertFalse(emptyCell.candidates().get(2)); // Candidate 2 should be removed
        assertFalse(emptyCell.candidates().get(3)); // Candidate 3 should be removed
    }

    @Test
    void removeCandidatesTest2() {
        List<Integer> digitsToRemove = List.of(2, 3);
        BitSet removed = emptyCell.removeCandidates(digitsToRemove);
        assertEquals(2, removed.cardinality()); // 2 and 3 should be removed
        assertFalse(emptyCell.candidates().get(2)); // Candidate 2 should be removed
        assertFalse(emptyCell.candidates().get(3)); // Candidate 3 should be removed
    }

    @Test
    void removeCandidateTest() {
        assertFalse(emptyCell.removeCandidate(8));
        assertTrue(emptyCell.removeCandidate(5));
        assertFalse(emptyCell.removeCandidate(5)); // Candidate 5 should already be removed
    }

    @Test
    void getCommonUnitTypeTest() {
        assertTrue(emptyCell.getCommonUnitType(biValueCell).contains(UnitType.COL));
        assertFalse(givenCell.getCommonUnitType(biValueCell).contains(UnitType.BOX));
        assertTrue(biValueCell.getCommonUnitType(givenCell).isEmpty()); // No common unit type with givenCell
    }

    @Test
    void toStringTest() {
        assertEquals("F2", givenCell.toString());
        assertEquals("B4", emptyCell.toString());
        assertEquals("D4", biValueCell.toString());
    }
}