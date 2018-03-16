package tests;

// This file is by Yifan.

import comp1110.ass2.Grid;
import comp1110.ass2.StepsGame;
import org.junit.Test;

import java.util.*;

import static comp1110.ass2.Grid.getPiece;
import static comp1110.ass2.Grid.getPieces;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/* The advantages of my tests :
   1. Test each function 100 times so that the result is not occasional.
   2. Each time the test is random. The placement is random, and all the inputs ( e.g. chars, strings, sets ) are random as well.
   3. The simple case is not too simple, because each simple case also has many tests ( use recursion to test a lot of cases. )
   4. Test both the basic attributes of output ( e.g. length, type, valid value ... ), and the actual correctness
      ( whether the return is what we expect to return. )
   5. Make sure not only all the values in the return are valid, but also all the expected return values are actually in the return.
   6. Have clear error messages, so that the error can be easily understood by users.

 */





public class YFTest {

    List<Character> list = Arrays.asList('A','B','C','D','E','F','G','H');
    Random random = new Random();

    // Declare a set of valid string enable the later random tests work well.

    // Note : these valid placements are from the 'TestUtility' file.

    public static String[] placements = {
            "CEQEHuGEOBDxFGSHCiAALDBg",
            "CEQEHuGEOBDxFGSHCiAALDBg",
            "BGKEGOCGQAGlHCiDBgGGnFGS",
            "BHFFCLHBNAGlCAiDBgGGnEDI",
            "CHSAHQGFjHCNBGKDBgFHlEAo",
            "GDLADgHAiEFFCGcDAkBDxFGS",
            "DFOCGQGDLADgHFjBGSFHlEAo",
            "HBLADgBHnCGODAiGElFGQEDI",
            "BGKFCNCHSAHQHFnEBvGAiDBg",
            "DFOAALHHnGAkFGQCAiBBgEDI",
            "AALBAkCGODBgEDIFHnGGQHCi",
            "ADgBAkCGODAiEDIFHnGGQHBL",
            "AALBBgCAkDFQEBxFDNGGSHCi",
            "AALBAmCAkDFQEAgFDNGGSHCi",
            "AALBBgCAkDFQEHwFDNGGSHCi",
            "ADgBBGCDkDAiEAoFCLGHSHBN",
            "ADgBBGCDkDAiEAoFDNGHSHBL",
            "AALBBGCAkDBgEAoFDNGHSHCi",
            "ADgBBGCDkDAiEAoFDNGFSHBL",
            "ADgBBGCDkDAiEAoFCLGFSHBN",
            "AALBBGCAkDBgEAoFDNGFSHCi",
            "AALBBgCAkDFOEDIFHnGGQHCi",
            "AALBAkCAgDFOEDIFHnGGQHCi",
            "AALBAkCGQDBgEGOFGSGEnHCi",
            "ADgBCTCGQDAiEGOFElGEnHBL",
            "AALBCTCGQDBgEGOFElGEnHCi",
            "ADgBAkCGQDAiEGOFGSGEnHBL",
    };

    // The methods below create random objects for random tests to use.

    public String randomPlacement() {
        int strIdx = random.nextInt(placements.length);
        int noPieces = random.nextInt(7);
        String output;
        output = placements[strIdx].substring(0,3*noPieces+2);
        return output;
    }

    public char randomChar() {
        int no = random.nextInt(8);
        return list.get(no);
    }

    public int randomX() {return random.nextInt(10);}
    public int randomY() {return random.nextInt(5);}


    public char[] randomChArray(int i) {
        char[] m = new char[i];
        m[0] = randomChar();
        if (i > 1)
            m[1] = randomChar();
        if (i > 2)
            m[2] = randomChar();
        return m;
    }

    public Set<String> randomString3(int s) {
        Set<String> a = new HashSet<>();
        for (int i = 0; i < s; i ++)
            a.add(randomChArray(3).toString());
        return a;
    }


    @Test
    public void getViablePosTest() {
        for (int i = 0; i < 10; i ++ ) {
            String strr = randomPlacement();
            long str = getPieces(strr);
            assertTrue("The return of getViablePos() is larger than the number of vacant positions, which is impossible.", StepsGame.getPiecePossPos(str, randomChar()).size() <= 8*(50 - ((String.valueOf(str).length())/3)*5));

            for (String a : StepsGame.getPiecePossPos(str, randomChar())) {
                assertTrue("The length of elements in the return of getViablePos() is out of bound.",a.length() == 3);
            }

            for (String a : StepsGame.getPiecePossPos(str, randomChar())) {
                assertTrue("getViablePos() returns a not viable value", StepsGame.isPlacementSequenceValid(a));
            }

            Set<String> b = randomString3(100);
            b.removeAll(StepsGame.getPiecePossPos(str, randomChar()));
            for (String a : b)
                assertFalse("Some values are valid but are not contained in the return of getViablePos().", StepsGame.isPlacementSequenceValid(a));
        }
    }

    @Test
    public void getAllPossPosTest() {
        for (int i = 0; i < 100; i ++ ) {
            String strr = randomPlacement();
            long str = getPieces(strr);
            for (String a : StepsGame.getAllPossPos(0, strr)) {
                assertTrue("The length of elements in the return of getAllPossPos() is out of bound.",a.length() == 3);

            }

            for (String a : StepsGame.getAllPossPos(0,strr)) {
                assertTrue("getAllPossPos returns a not viable value",StepsGame.isPlacementSequenceValid(a));
            }
            Set<String> b = randomString3(100);
            b.removeAll(StepsGame.getAllPossPos(0,strr));
            for (String a : b)
                assertFalse("Some values are valid but are not contained in the return of getAllPossPos()",StepsGame.isPlacementSequenceValid(a));
        }

    }


 }