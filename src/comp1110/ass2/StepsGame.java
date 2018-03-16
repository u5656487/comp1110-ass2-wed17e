package comp1110.ass2;

import java.util.*;
import java.util.Arrays;
import java.lang.String;

import static comp1110.ass2.Grid.*;


/*
  This class provides the text interface for the Steps Game

  The game is based directly on Smart Games' IQ-Steps game
  (http://www.smartgames.eu/en/smartgames/iq-steps)
 */



/**
 * ----------- Brief introduction of our design ------------------- By Yifan
 * -------------------------------------version 1.0----------------------------------
 * <p>
 * Step 1. updateCharToGrid( ) method will pick out the 3n th (n is a natural number) chars of the given placement string ( representing the positions of rings)
 * and transform it to the form of grid information, enabling us to operate it more smoothly.
 * Step 2. updateGrids( ) will read the initial placement of rings and update the grid information ( about whether a grid is empty or not, realised by HashMap)
 * Step 3. isPlacementSequenceValid( ) will judge whether a placement of a certain ring with its orientation is valid.
 * Step 4. updateGrids( ) will update the grids state ( empty to not empty) of a placement if the placement is valid.
 * Step 5. addRingToSolution( ) will add the a ring to the final output solution if the placement of the ring is successful. ( If finally failed, start again, trying another way.)
 * Step 6. Output : If all the rings are well placed, then we can output things.
 **/

/* Note :  All codes without author are done by group. */

public class StepsGame {


    /**
     * Determine whether a piece placement is well-formed according to the following:
     * - it consists of exactly three characters
     * - the first character is in the range A .. H (shapes)
     * - the second character is in the range A .. H (orientations)
     * - the third character is in the range A .. Y and a .. y (locations)
     *
     * @param piecePlacement A string describing a piece placement
     * @return True if the piece placement is well-formed
     */
    //completed by Jiawen
    static boolean isPiecePlacementWellFormed(String piecePlacement) {
        // FIXME Task 2: determine whether a piece placement is well-formed
        char[] c = piecePlacement.toCharArray();
        return piecePlacement.length() == 3 && c[0] >= 'A' && c[0] <= 'H' && c[1] >= 'A' && c[1] <= 'H' && ((c[2] >= 'A' && c[2] <= 'Y') || (c[2] >= 'a' && c[2] <= 'y'));
    }

    /**
     * Determine whether a placement string is well-formed:
     * - it consists of exactly N three-character piece placements (where N = 1 .. 8);
     * - each piece placement is well-formed
     * - no shape appears more than once in the placement
     *
     * @param placement A string describing a placement of one or more pieces
     * @return True if the placement is well-formed
     */
    //completed by Jiawen
    static boolean isPlacementWellFormed(String placement) {
        // FIXME Task 3: determine whether a placement is well-formed
        if (placement != null) {
            if (placement.length() % 3 == 0 && placement.length() / 3 >= 1 && placement.length() / 3 <= 8) {
                boolean eachWellFormed = true;
                for (int i = 0; i < placement.length(); i += 3) {
                    eachWellFormed = eachWellFormed && isPiecePlacementWellFormed(placement.substring(i, i + 3));
                }
                if (eachWellFormed) {
                    Set<Character> set = new HashSet<>();
                    List<Character> list = new ArrayList<>();
                    for (int i = 0; i < placement.length(); i += 3) {
                        set.add(placement.charAt(i));
                        list.add(placement.charAt(i));
                    }
                    return set.size() == list.size();
                }
                return false;
            }
            return false;
        }
        return false;
    }



    /* by Jiawen , Yifan**/

//     valid means: https://gitlab.cecs.anu.edu.au/comp1110/comp1110-ass2/blob/master/README.md#legal-piece-placements
//     posted by Jiawen

    /**
     * Determine whether a placement sequence is valid.  To be valid, the placement
     * sequence must be well-formed and each piece placement must be a valid placement
     * (with the pieces ordered according to the order in which they are played).
     *
     * @param placement A placement sequence string
     * @return True if the placement sequence is valid
     */
    public static boolean isPlacementSequenceValid(String placement) {
        // FIXME Task 5: determine whether a placement sequence is valid
        if (isPlacementWellFormed(placement)) {
            long grid = 0L;
            for (int i = 0; i < placement.length(); i += 3) {
                String pieceN = placement.substring(i, i + 3);
                if (!canPut(grid, pieceN)) return false;
                grid |= getPiece(pieceN);
            }
            return true;
        }
        return false;
    }


    /**
     * by Jiawen
     **/

    //get the neighbours of a piece
    //given the position of piece, which is a char
    //  and all available pieces
    private static List<String> getNeighbours(char homePos, List<String> objctv) {
        List<String> neighbours = new ArrayList<>();
        for (String piece : objctv) {
            if (Math.abs(getX(homePos) - getX(piece.charAt(2))) < 4 && Math.abs(getY(homePos) - getY(piece.charAt(2))) < 4) {
                neighbours.add(piece);
            }
        }
        return neighbours;
    }


    /** by Jiawen **/
    /**
     * Given a string describing a placement of pieces and a string describing
     * an (unordered) objective, return a set of all possible next viable
     * piece placements.   A viable piece placement must be a piece that is
     * not already placed (ie not in the placement string), and which will not
     * obstruct any other unplaced piece.
     *
     * @param placement A valid sequence of piece placements where each piece placement is drawn from the objective
     * @param objective A valid game objective, but not necessarily a valid placement string
     * @return An set of viable piece placements
     */
    public static Set<String> getViablePiecePlacements(String placement, String objective) {
        // FIXME Task 6: determine the correct order of piece placements
        List<String> placedPieces = new ArrayList<>();
        for (int i = 0; i < placement.length(); i += 3) {
            placedPieces.add(placement.substring(i, i + 3));
        }
        List<String> unplacedPieces = new ArrayList<>();
        for (int i = 0; i < objective.length(); i += 3) {
            if (!placedPieces.contains(objective.substring(i, i + 3))) {
                for (String piece : placedPieces)
                    if (objective.charAt(i) == piece.charAt(0)) return new HashSet<>();
                unplacedPieces.add(objective.substring(i, i + 3));
            }
        }
        Set<String> viablePieces = new HashSet<>();
        for (String piece : unplacedPieces) {
            boolean isViable = true;
            List<String> piecesLeft = new ArrayList<>(unplacedPieces);
            piecesLeft.remove(piece);
            List<String> neighbours = getNeighbours(piece.charAt(2), piecesLeft);
            for (String neighbour : neighbours) {
                String newPlacement = placement + piece + neighbour;
                boolean valid = isPlacementSequenceValid(newPlacement);
                if (!valid) {
                    isViable = false;
                    break;
                }
            }
            if (isViable) viablePieces.add(piece);
        }
        return viablePieces;
    }

    /**
     * by Jiawen
     **/
    public static String[] dictionary1 = {
            "CGOBEnGAkHCiFGQEDIAALDBg",
            "DFOCGQBAkGGnHCiAALEAgFGS",
            "DFOCGQEFlHCiGGnAALFGSBBg",
            "FDNCGQBAkGGnHCiEDIAALDBg",
            "FDNCGQEFlHCiAALGGnBCTDBg",
            "FDNCGQGHlHCiBDxAALDBgEDI",
            "FDNCGQHFlGDLADgDAiEDIBDx",
            "GDLADgBHnCGODAiHAkFGQEDI",
            "GDLADgEEnCGODAiHAkFGQBCT",
            "BGSFCLGEQHBNADgEAoDAiCDk",
            "BGSFCLHAgAHQCDNDAiGHlEAo",
            "BGSFCLHGQAHOGCgDAiEAoCDk",
            "BGSEGQGHnFDNCAkHCiAALDBg",
            "BGSEGQHFnFDNGDLADgDAiCDk",
            "CHSAHQBGjFHlHCNEAoGBgDCL",
            "CHSAHQEHjFHlHCNGBgBDxDCL"
    };

    //level 1-6, /** by Jiawen **/
    private static String[][] generateStartingPlacement(int level) {
        HashSet<String> placements = new HashSet<>();
        HashSet<String> duplicates = new HashSet<>();
        for (String placement : dictionary1) {
            if (placements.contains(placement.substring(0, 3 * (8 - level)))) {
                duplicates.add(placement.substring(0, 3 * (8 - level)));
                continue;
            }
            placements.add(placement.substring(0, 3 * (8 - level)));
        }
        ArrayList<String> okPlacements = new ArrayList<>();
        ArrayList<String> okObjectives = new ArrayList<>();
        for (String placement : dictionary1) {
            if (duplicates.contains(placement.substring(0, 3 * (8 - level)))) continue;
            if (getSolutions(placement.substring(0, 3 * (8 - level))).length == 1) {
                okPlacements.add(placement.substring(0, 3 * (8 - level)));
                okObjectives.add(placement);
            }
        }
        String[][] strings = new String[okPlacements.size()][2];
        for (int i = 0; i < okPlacements.size(); i++) {
            strings[i][0] = okPlacements.get(i);
            strings[i][1] = okObjectives.get(i);
        }
        return strings;
    }

    /**
     * by Jiawen
     **/
    private static void printAllStartingPlacements() {
        for (int i = 1; i < 7; i++) {
            String[][] strings = generateStartingPlacement(i);
            for (String[] string : strings)
                System.out.println(Arrays.toString(string));
        }
    }


    /** written by Wei XING
     * Find the remaining pieces which have not put on the board, then put these
     * pieces to the board by sequence. -- exhaustion method
     * My idea is very pure, firstly figure out the name of the pieces which are on the board and know what remaining pieces
     * we need to put on the board. Then I think I need to use the exhaustion method for these remaining pieces, what I have done
     * is from the given placement, I can know what other remaining pieces we need to put into the board.
     * Next time, I think I will try to create a put method to try these remaining pieces one by one (from direction to
     * turnover). I reckon it will take a long time.
     */

    /**
     * Return an array of all unique (unordered) solutions to the game, given a
     * starting placement.   A given unique solution may have more than one than
     * one placement sequence, however, only a single (unordered) solution should
     * be returned for each such case.
     * <p>
     * ull;
     *
     * @param placement A valid piece placement string.
     * @return An array of strings, each describing a unique unordered solution to
     * the game given the starting point provided by placement.
     */
    static String[] getSolutions(String placement) {

        // completed by Wei XING
        // Find out the name of the pieces which are on the board and know the remaining pieces

        // FIXME Task 9: determine all solutions to the game, given a particular starting placement

        System.out.println("Solving " + placement + "...");
        long grid = 0;
        for (int i = 0; i < placement.length() - 2; i += 3) grid |= getPiece(placement.substring(i, i + 3));
        Set<String> temp = getSolutionsIterator(grid, placement);
        System.out.println("Got sequenced solutions: " + new ArrayList<>(temp).toString());
        HashMap<String, String> map = new HashMap<>();
        for (String solutions : temp) {
            ArrayList<String> t = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                t.add(solutions.substring(3 * j, 3 * (j + 1)));
            }
            ArrayList<String> temp1 = new ArrayList<>();
            for (char position = 'A'; position <= 'H'; position++) {
                for (String s1 : t) {
                    if (s1.charAt(0) == position) {
                        temp1.add(s1);
                    }
                }
            }
            String result = "";
            StringBuilder builder = new StringBuilder(result);
            for (String s2 : temp1) {
                builder.append(s2);
            }
            result = builder.toString();
            map.put(result, solutions);
        }
        String[] solutions = new String[map.values().size()];
        int i = 0;
        for (String string : map.values()) {
            solutions[i] = string;
            i++;
        }
        System.out.println("Got unique solutions: " + Arrays.toString(solutions));
        return solutions;
    }


    /* All the rest by Yifan Cheng */
    private static ArrayList<Integer> sidePos = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 19, 20, 29, 30, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49));

    public static Set<String> getPiecePossPos(long grid, char piece) {
        Set<String> allPoss = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            if (i == 0 || i == 9 || i == 40 || i == 49) continue;
            if (piece != 'B' && piece != 'E' && sidePos.contains(i)) continue;
            char pos = (char) ((i + 'A') + i / 25 * 7);
            for (int o = 'A'; o <= 'D' && (i / 10 + i % 10) % 2 == 0; o++) {
                String newPiece = Character.toString(piece) + Character.toString((char) o) + Character.toString(pos);
                if (canPutB(grid, newPiece)) allPoss.add(newPiece);
            }
            for (int o = 'E'; o <= 'H' && (i / 10 + i % 10) % 2 == 1; o++) {
                String newPiece = Character.toString(piece) + Character.toString((char) o) + Character.toString(pos);
                if (canPutB(grid, newPiece)) allPoss.add(newPiece);
            }
        }
        return allPoss;
    }

    public static Set<String> getAllPossPos(long grid, String placement) {
        Set<String> allPoss = new HashSet<>();
        Set<Character> usedPieces = new HashSet<>();
        for (int i = 0; i < placement.length() - 2; i += 3) usedPieces.add(placement.charAt(i));
        for (int i = 'A'; i <= 'H'; i++) {
            if (!usedPieces.contains((char) i)) allPoss.addAll(getPiecePossPos(grid, (char) i));
        }
        return allPoss;
    }


    private static Set<String> getSolutionsIterator(long grid, String placement) {
        if (placement.length() == 24) return new HashSet<>(Collections.singletonList(placement));
        else {
            Set<String> viablePieces = getAllPossPos(grid, placement);
            Set<String> nextPlacement = new HashSet<>();
            for (String piece : viablePieces)
                nextPlacement.addAll(getSolutionsIterator(grid | getPiece(piece), placement + piece));
            return nextPlacement;
        }
    }


    private static List<String> task11(String m, String o, String n, String p) {
        List<Character> list = new ArrayList<>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'));
        List<Character> list3;
        list3 = new ArrayList<>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'));
        List<String> rtns = new ArrayList<>();
        int[] priority = {78, 79, 80, 81, 105, 106, 107, 108, 76, 83, 103, 110};
        list3.remove((Character) m.charAt(0));
        list3.remove((Character) n.charAt(0));
        String firstPiece = "";
        for (int i = 0; i < priority.length; i++) {
            firstPiece = m + o + Character.toString((char) priority[i]);
            for (int b : priority) {
                for (char d : list3) {
                    if (!isPlacementSequenceValid(firstPiece + n + p + Character.toString((char) b)))
                        break;
                    for (char e : list) {
                        for (int f : priority) {
                            String result = firstPiece + n + p + Character.toString((char) b) + Character.toString(d) + Character.toString((char) e) + Character.toString((char) f);
                            if (isPlacementSequenceValid(result)) {
                                if (getSolutions(result).length == 1) {
                                    rtns.add(getSolutions(result)[0]);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return rtns;
    }


    // m : ACDFGH
    // n : ABCDEFGHG
    // m != n

    // input = MO*NP****
    public static void main(String[] args) {
        List<String> results = task11("F", "D", "C", "G");
        for (String str : results) {
            System.out.println(str);
        }
        System.out.println("Solutionnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
        printAllStartingPlacements();
    }
}


/*
    public static String[][][] dictionary =
            {{{"CGOBEnGAkHCiFGQEDIAAL", "CGOBEnGAkHCiFGQEDIAALDBg"},
                    {"DFOCGQBAkGGnHCiAALEAg", "DFOCGQBAkGGnHCiAALEAgFGS"},
                    {"DFOCGQEFlHCiGGnAALFGS", "DFOCGQEFlHCiGGnAALFGSBBg"},
                    {"FDNCGQBAkGGnHCiEDIAAL", "FDNCGQBAkGGnHCiEDIAALDBg"},
                    {"FDNCGQEFlHCiAALGGnBCT", "FDNCGQEFlHCiAALGGnBCTDBg"},
                    {"FDNCGQGHlHCiBDxAALDBg", "FDNCGQGHlHCiBDxAALDBgEDI"},
                    {"FDNCGQHFlGDLADgDAiEDI", "FDNCGQHFlGDLADgDAiEDIBDx"},
                    {"GDLADgBHnCGODAiHAkFGQ", "GDLADgBHnCGODAiHAkFGQEDI"},
                    {"GDLADgEEnCGODAiHAkFGQ", "GDLADgEEnCGODAiHAkFGQBCT"},
                    {"BGSFCLGEQHBNADgEAoDAi", "BGSFCLGEQHBNADgEAoDAiCDk"},
                    {"BGSFCLHAgAHQCDNDAiGHl", "BGSFCLHAgAHQCDNDAiGHlEAo"},
                    {"BGSFCLHGQAHOGCgDAiEAo", "BGSFCLHGQAHOGCgDAiEAoCDk"},
                    {"BGSEGQGHnFDNCAkHCiAAL", "BGSEGQGHnFDNCAkHCiAALDBg"},
                    {"BGSEGQHFnFDNGDLADgDAi", "BGSEGQHFnFDNGDLADgDAiCDk"},
                    {"CHSAHQBGjFHlHCNEAoGBg", "CHSAHQBGjFHlHCNEAoGBgDCL"},
                    {"CHSAHQEHjFHlHCNGBgBDx", "CHSAHQEHjFHlHCNGBgBDxDCL"}},
                    {{"CGOBEnGAkHCiFGQEDI", "CGOBEnGAkHCiFGQEDIAALDBg"},
                            {"DFOCGQBAkGGnHCiAAL", "DFOCGQBAkGGnHCiAALEAgFGS"},
                            {"DFOCGQEFlHCiGGnAAL", "DFOCGQEFlHCiGGnAALFGSBBg"},
                            {"FDNCGQBAkGGnHCiEDI", "FDNCGQBAkGGnHCiEDIAALDBg"},
                            {"FDNCGQEFlHCiAALGGn", "FDNCGQEFlHCiAALGGnBCTDBg"},
                            {"FDNCGQGHlHCiBDxAAL", "FDNCGQGHlHCiBDxAALDBgEDI"},
                            {"FDNCGQHFlGDLADgDAi", "FDNCGQHFlGDLADgDAiEDIBDx"},
                            {"GDLADgBHnCGODAiHAk", "GDLADgBHnCGODAiHAkFGQEDI"},
                            {"GDLADgEEnCGODAiHAk", "GDLADgEEnCGODAiHAkFGQBCT"},
                            {"BGSFCLGEQHBNADgEAo", "BGSFCLGEQHBNADgEAoDAiCDk"},
                            {"BGSFCLHAgAHQCDNDAi", "BGSFCLHAgAHQCDNDAiGHlEAo"},
                            {"BGSFCLHGQAHOGCgDAi", "BGSFCLHGQAHOGCgDAiEAoCDk"},
                            {"BGSEGQGHnFDNCAkHCi", "BGSEGQGHnFDNCAkHCiAALDBg"},
                            {"BGSEGQHFnFDNGDLADg", "BGSEGQHFnFDNGDLADgDAiCDk"},
                            {"CHSAHQBGjFHlHCNEAo", "CHSAHQBGjFHlHCNEAoGBgDCL"},
                            {"CHSAHQEHjFHlHCNGBg", "CHSAHQEHjFHlHCNGBgBDxDCL"}},
                    {{"CGOBEnGAkHCiFGQ", "CGOBEnGAkHCiFGQEDIAALDBg"},
                            {"DFOCGQBAkGGnHCi", "DFOCGQBAkGGnHCiAALEAgFGS"},
                            {"DFOCGQEFlHCiGGn", "DFOCGQEFlHCiGGnAALFGSBBg"},
                            {"FDNCGQBAkGGnHCi", "FDNCGQBAkGGnHCiEDIAALDBg"},
                            {"FDNCGQEFlHCiAAL", "FDNCGQEFlHCiAALGGnBCTDBg"},
                            {"FDNCGQGHlHCiBDx", "FDNCGQGHlHCiBDxAALDBgEDI"},
                            {"FDNCGQHFlGDLADg", "FDNCGQHFlGDLADgDAiEDIBDx"},
                            {"GDLADgBHnCGODAi", "GDLADgBHnCGODAiHAkFGQEDI"},
                            {"GDLADgEEnCGODAi", "GDLADgEEnCGODAiHAkFGQBCT"},
                            {"BGSFCLGEQHBNADg", "BGSFCLGEQHBNADgEAoDAiCDk"},
                            {"BGSFCLHAgAHQCDN", "BGSFCLHAgAHQCDNDAiGHlEAo"},
                            {"BGSFCLHGQAHOGCg", "BGSFCLHGQAHOGCgDAiEAoCDk"},
                            {"BGSEGQGHnFDNCAk", "BGSEGQGHnFDNCAkHCiAALDBg"},
                            {"BGSEGQHFnFDNGDL", "BGSEGQHFnFDNGDLADgDAiCDk"},
                            {"CHSAHQBGjFHlHCN", "CHSAHQBGjFHlHCNEAoGBgDCL"},
                            {"CHSAHQEHjFHlHCN", "CHSAHQEHjFHlHCNGBgBDxDCL"}},
                    {{"CGOBEnGAkHCi", "CGOBEnGAkHCiFGQEDIAALDBg"},
                            {"DFOCGQBAkGGn", "DFOCGQBAkGGnHCiAALEAgFGS"},
                            {"DFOCGQEFlHCi", "DFOCGQEFlHCiGGnAALFGSBBg"},
                            {"FDNCGQBAkGGn", "FDNCGQBAkGGnHCiEDIAALDBg"},
                            {"FDNCGQEFlHCi", "FDNCGQEFlHCiAALGGnBCTDBg"},
                            {"FDNCGQGHlHCi", "FDNCGQGHlHCiBDxAALDBgEDI"},
                            {"FDNCGQHFlGDL", "FDNCGQHFlGDLADgDAiEDIBDx"},
                            {"GDLADgBHnCGO", "GDLADgBHnCGODAiHAkFGQEDI"},
                            {"GDLADgEEnCGO", "GDLADgEEnCGODAiHAkFGQBCT"},
                            {"BGSFCLGEQHBN", "BGSFCLGEQHBNADgEAoDAiCDk"},
                            {"BGSFCLHAgAHQ", "BGSFCLHAgAHQCDNDAiGHlEAo"},
                            {"BGSFCLHGQAHO", "BGSFCLHGQAHOGCgDAiEAoCDk"},
                            {"BGSEGQGHnFDN", "BGSEGQGHnFDNCAkHCiAALDBg"},
                            {"BGSEGQHFnFDN", "BGSEGQHFnFDNGDLADgDAiCDk"},
                            {"CHSAHQBGjFHl", "CHSAHQBGjFHlHCNEAoGBgDCL"},
                            {"CHSAHQEHjFHl", "CHSAHQEHjFHlHCNGBgBDxDCL"}},
                    {{"CGOBEnGAk", "CGOBEnGAkHCiFGQEDIAALDBg"},
                            {"DFOCGQBAk", "DFOCGQBAkGGnHCiAALEAgFGS"},
                            {"DFOCGQEFl", "DFOCGQEFlHCiGGnAALFGSBBg"},
                            {"FDNCGQBAk", "FDNCGQBAkGGnHCiEDIAALDBg"},
                            {"FDNCGQEFl", "FDNCGQEFlHCiAALGGnBCTDBg"},
                            {"FDNCGQGHl", "FDNCGQGHlHCiBDxAALDBgEDI"},
                            {"FDNCGQHFl", "FDNCGQHFlGDLADgDAiEDIBDx"},
                            {"GDLADgBHn", "GDLADgBHnCGODAiHAkFGQEDI"},
                            {"GDLADgEEn", "GDLADgEEnCGODAiHAkFGQBCT"},
                            {"BGSFCLGEQ", "BGSFCLGEQHBNADgEAoDAiCDk"},
                            {"BGSFCLHAg", "BGSFCLHAgAHQCDNDAiGHlEAo"},
                            {"BGSFCLHGQ", "BGSFCLHGQAHOGCgDAiEAoCDk"},
                            {"BGSEGQGHn", "BGSEGQGHnFDNCAkHCiAALDBg"},
                            {"BGSEGQHFn", "BGSEGQHFnFDNGDLADgDAiCDk"},
                            {"CHSAHQBGj", "CHSAHQBGjFHlHCNEAoGBgDCL"},
                            {"CHSAHQEHj", "CHSAHQEHjFHlHCNGBgBDxDCL"}},
                    {{"CGOBEn", "CGOBEnGAkHCiFGQEDIAALDBg"}}};
}
*/