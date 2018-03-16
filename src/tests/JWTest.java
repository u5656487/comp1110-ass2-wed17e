package tests;

// This file is by Jiawen.

import org.junit.Test;


import static comp1110.ass2.Grid.*;
import static org.junit.Assert.assertTrue;

public class JWTest {
    private int GRIDAA = 0b100000001110000000011;
    private int GRIDAC = 0b11000000001110000000100;
    private int GRIDAE = 0b10000000001110000000110;
    private int GRIDCA = 0b1100000001100000000010;
    private int GRIDCC = 0b1000000000110000000110;
    private int GRIDCE = 0b11000000000110000000010;
    private int GRIDEA = 0b1100000000110000000010;
    private int GRIDEC = 0b1000000001100000000110;
    private int GRIDEE = 0b11000000001100000000010;
    private int GRIDGA = 0b1100000001100000000110;
    private int GRIDGC = 0b1100000000110000000110;
    private int GRIDGE = 0b11000000000110000000011;
    private int[] GRID9 = {GRIDAA, GRIDAC, GRIDAE, GRIDCA, GRIDCC, GRIDCE,
            GRIDEA, GRIDEC, GRIDEE, GRIDGA, GRIDGC, GRIDGE};

    private void testSingle(char c, char o) {
        int grid9 = GRID9[(c - 'A') / 2 * 3];
        if (o == 'A')
            assertTrue("You failed on test of piece "+c+" in the orientation of "+o, rotate(grid9,'A') == GRID9[(c-'A')/2*3]);
        if (o == 'C')
            assertTrue("You failed on test of piece "+c+" in the orientation of "+o+", should be "+Integer.toString(GRID9[(c-'A')/2*3],2)+", but yours returns "+Integer.toString(rotate(grid9,'C'),2), rotate(grid9,'C') == GRID9[(c-'A')/2*3+1]);
        if (o == 'E')
            assertTrue("You failed on test of piece "+c+" in the orientation of "+o, flip(grid9) == GRID9[(c-'A')/2*3+2]);
    }

    @Test
    public void testRotateOnce() {
        for (int i = 0; i < GRID9.length / 4; i++) {
            char piece = (char)(i*2+'A');
            char orientation = 'A';
            testSingle(piece, orientation);
        }
    }

    @Test
    public void testRotateMultiTimes() {
        for (int i = 0; i < GRID9.length / 4; i++) {
            char piece = (char)(i*2+'A');
            char orientation = 'C';
            testSingle(piece, orientation);
        }
    }

    @Test
    public void testFlip() {
        for (int i = 0; i < GRID9.length / 4; i++) {
            char piece = (char)(i*2+'A');
            char orientation = 'E';
            testSingle(piece, orientation);
        }
    }
}
