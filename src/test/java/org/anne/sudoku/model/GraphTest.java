package org.anne.sudoku.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void findAllCyclesTest1() {
        Graph<Character> graph = new Graph<>();

        graph.addNode('A', List.of('B'), List.of('B', 'D'));
        graph.addNode('B', List.of('A', 'C'), List.of('A', 'C', 'E'));
        graph.addNode('C', List.of('F'), List.of('B', 'D', 'F'));
        graph.addNode('D', List.of('C'), List.of('A', 'C', 'G'));
        graph.addNode('E', List.of('F'), List.of('B', 'F'));
        graph.addNode('F', List.of('E'), List.of('C', 'E'));
        graph.addNode('G', List.of('D'), List.of('D'));

        Set<Cycle<Character>> cycles = graph.findAllCycles();
        assertEquals(3, cycles.size());
        assertTrue(cycles.contains(new Cycle<>(List.of('B', 'C', 'F', 'E'))));
        assertTrue(cycles.contains(new Cycle<>(List.of('A', 'B', 'C', 'D'))));
        assertTrue(cycles.contains(new Cycle<>(List.of('A', 'B', 'E', 'F', 'C', 'D'))));
    }

    @Test
    void findAllCyclesTest2() {
        Graph<Integer> graph = new Graph<>();

        graph.addNode(1, List.of(2), List.of(2, 4));
        graph.addNode(2, List.of(1, 3), List.of(1, 3, 5));
        graph.addNode(3, List.of(6), List.of(2, 4, 6));
        graph.addNode(4, List.of(3), List.of(1, 3, 7));
        graph.addNode(5, List.of(6), List.of(2, 6));
        graph.addNode(6, List.of(5), List.of(3, 5));
        graph.addNode(7, List.of(4), List.of(4));

        Set<Cycle<Integer>> cycles = graph.findAllCycles();
        assertEquals(3, cycles.size());
        assertTrue(cycles.contains(new Cycle<>(List.of(2, 3, 6, 5))));
        assertTrue(cycles.contains(new Cycle<>(List.of(1, 2, 3, 4))));
        assertTrue(cycles.contains(new Cycle<>(List.of(1, 2, 5, 6, 3, 4))));
    }

    @Test
    void findAllCyclesTest3() {
        Graph<String> graph = new Graph<>();

        graph.addNode("A", List.of("B", "G"), List.of("B", "G"));
        graph.addNode("B", List.of("A"), List.of("A", "C"));
        graph.addNode("C", List.of("D"), List.of("B", "D"));
        graph.addNode("D", List.of("C"), List.of("C", "E"));
        graph.addNode("E", List.of("F"), List.of("D", "F"));
        graph.addNode("F", List.of("E"), List.of("E", "G"));
        graph.addNode("G", List.of("A"), List.of("A", "F"));

        Set<Cycle<String>> cycles = graph.findAllCycles();
        assertEquals(1, cycles.size());
        assertTrue(cycles.contains(new Cycle<>(List.of("A", "B", "C", "D", "E", "F", "G"))));
    }
}