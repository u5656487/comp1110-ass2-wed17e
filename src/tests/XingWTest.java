package tests;

// This file is by Wei Xing.


import static comp1110.ass2.Grid.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class XingWTest {

    @Test
    public void getPieceRotateTest(){
        long grid9AA = getPiece("AAL");
        assertTrue(grid9AA == 0b100000001110000000011);
        long grid9AB = getPiece("ABL");
        assertTrue(grid9AB == 0b1000000001100000000111);
    }

    @Test
    public void getPieceFlipTest(){
        long grid9AF = getPiece("AFL");

        assertTrue(grid9AF == 0b11100000001100000000010);
    }

    @Test
    public void addUpperNeighboursTest(){

        long gridBAL = 0b11000000001100000000010L;
        assertTrue(addUpperNeighbours(gridBAL) == 0b10000000011100000011100000000111L);

        long gridECY = 0b10000000011000000001100000000000000L;
        assertTrue(addUpperNeighbours(gridECY) == 0b100000000111000000111000000001110000000010000L);
    }

}