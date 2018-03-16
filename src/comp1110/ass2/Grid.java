package comp1110.ass2;

/* Note :  All codes in this file are done by Yifan , Jiawen. */

public class Grid {

    //http://www.matrix67.com/blog/archives/263
    //https://www.cnblogs.com/graphics/archive/2010/06/21/1752421.html

    public static void main(String[] args) {
        System.out.println(Long.toString(getPiece("BDx"),2));
        System.out.println(Integer.toString(0b11111001>>3,2));
        System.out.println(0b111111111111);
    }

    private static int put(char piece) {
        int home = 11;
        switch (piece) {
            case 'A': return (1<<(home-11)) | (1<<(home-10)) | (1<<(home-1)) | (1<<home) | (1<<(home+1)) | (1<<(home+9));
            case 'B': return (1<<(home-10)) | (1<<home) | (1<<(home+1)) | (1<<(home+10)) | (1<<(home+11));
            case 'C': return (1<<(home-10)) | (1<<home) | (1<<(home+1)) | (1<<(home+9)) | (1<<(home+10));
            case 'D': return (1<<(home-10)) | (1<<(home-1)) | (1<<home) | (1<<(home+10)) | (1<<(home+11));
            case 'E': return (1<<(home-10)) | (1<<(home-1)) | (1<<home) | (1<<(home+9)) | (1<<(home+10));
            case 'F': return (1<<(home-9)) | (1<<home) | (1<<(home+1)) | (1<<(home+9)) | (1<<(home+10));
            case 'G': return (1<<(home-10)) | (1<<(home-9)) | (1<<home) | (1<<(home+1)) | (1<<(home+9)) | (1<<(home+10));
            case 'H': return (1<<(home-10)) | (1<<(home-9)) | (1<<(home-1)) | (1<<home) | (1<<(home+10)) | (1<<(home+11));
        }
        return -1;
    }

    private static int rotateOnce(int origin) {
        int rotated = 0;
        int home = 11;
        for (int i = 0; i < 23; i++) {
            if (((1<<i) & origin) != 0) {
                if (i == home-11) rotated |= (1<<(home-9));
                if (i == home-10) rotated |= (1<<(home+1));
                if (i == home-9) rotated |= (1<<(home+11));
                if (i == home-1) rotated |= (1<<(home-10));
                if (i == home) rotated |= (1<<home);
                if (i == home+1) rotated |= (1<<(home+10));
                if (i == home+9) rotated |= (1<<(home-11));
                if (i == home+10) rotated |= (1<<(home-1));
                if (i == home+11) rotated |= (1<<(home+9));
            }
        }
        return rotated;
    }

    public static int rotate(int origin, char secChar) {
        int time = (secChar - 'A') % 4;
        for (int i = 0; i < time; i++) {
            origin = rotateOnce(origin);
        }
        return origin;
    }

    public static int flip(int origin) {
        int flipped = 0;
        int home = 11;
        for (int i = 0; i < 23; i++) {
            if (((1<<i) & origin) != 0) {
                if (i-home==11 || i-home==-9 || i-home==1) flipped += (1<<(i-2));
                else if (i-home==9 || i-home==-11 || home-i==1) flipped += (1<<(i+2));
                else flipped += (1<<i);
            }
        }
        return flipped;
    }

    public static long getPiece(String piece) {
        int home = piece.charAt(2) - 'A' - ((piece.charAt(2) - 'A') / 29 * 7);
        int pieceCover = put(piece.charAt(0));
        if ((piece.charAt(1)-'A')/4==0) pieceCover = rotate(pieceCover, piece.charAt(1));
        else pieceCover = rotate(flip(pieceCover), piece.charAt(1));
        long finalCover;
        if (home >= 11) finalCover = (long)pieceCover << (home-11);
        else finalCover = pieceCover >> (11-home);
        return finalCover;
    }

    public static long getPieces(String Piece) {
        long a = 0;
        for(int i = 0; i < Piece.length()-2; i += 3) {
             a = a | getPiece(Piece.substring(i,i+3));
        }
        return a;
    }

    public static int getX(char p) {
        int home = p - 'A' - ((p - 'A') / 29 * 7);
        return home / 10;
    }

    public static int getY(char p) {
        int home = p - 'A' - ((p - 'A') / 29 * 7);
        return home % 10;
    }

    private static int countOnes(long grid) {
        long n = grid;
        int c;
        for (c = 0; n > 0; c++) n &= (n-1);
        return c;
    }

    //too small <= doesn't have enough '1's in the long
    //too large <= 1L<<50 | 1L<<51 | 1L<<52 | 1L<<53 | 1L<<54 | 1L<<55 | 1L<<56 | 1L<<57 | 1L<<58 | 1L<<59
    //wrong position in a row <= loop through all '1's and compare it with the last one
    private static boolean onValidPos(long grid, char piece) {
        if ((grid & 0b11111111111) != 0) {
            if ((piece == 'A' || piece == 'G' || piece == 'H') && countOnes(grid) != 6) return false;
            if ((piece == 'B' || piece == 'C' || piece == 'D' || piece == 'E' || piece == 'F') && countOnes(grid) != 5)
                return false;
        } else if (grid > Math.pow(2,19)) {
            if ((grid & ((1L << 50) | (1L << 51) | (1L << 52) | (1L << 53) | (1L << 54) | (1L << 55) | (1L << 56) | (1L << 57) | (1L << 58) | (1L << 59))) != 0)
                return false;
        }
        int last = 0;
        for (int i = 0; i < 50; i++) {
            if (((1L<<i) & grid) != 0) {
                last = i % 10;
                break;
            }
        }
        for (int i = 0; i < 50; i++) {
            if (((1L<<i) & grid) != 0) {
                if (last - i % 10 > 7) return false;
                last = i % 10;
            }
        }
        return true;
    }

    private static boolean bottomOnPeg(char o, char p) {
        if ((o - 'A') / 4 == 0) return (getX(p) + getY(p)) % 2 == 0;
        else return (getX(p) + getY(p)) % 2 != 0;
    }

    public static long addUpperNeighbours(long oriGrid) {
        for (int i = 0; i < 50; i++) {
            if (((1L<<i) & oriGrid) != 0) {
                int x = i / 10;
                int y = i % 10;
                if (((x+y) & 1) == 1) {
                    if (x+1 <= 4) oriGrid |= 1L<<((x+1)*10+y);
                    if (x-1 >= 0) oriGrid |= 1L<<((x-1)*10+y);
                    if (y+1 <= 9) oriGrid |= 1L<<(x*10+y+1);
                    if (y-1 >= 0) oriGrid |= 1L<<(x*10+y-1);
                }
            }
        }
        return oriGrid;
    }

    private static boolean allOnVacant(long occupiedOriGrid, String piece) {
        return (occupiedOriGrid & getPiece(piece)) == 0;
    }

    //upper neighbours added in this method (mind the side effect)
    static boolean canPut(long oriGrid, String newPiece) {
        long grid = getPiece(newPiece);
        return bottomOnPeg(newPiece.charAt(1), newPiece.charAt(2)) && onValidPos(grid, newPiece.charAt(0)) && allOnVacant(addUpperNeighbours(oriGrid),newPiece);
    }

    static boolean canPutB(long oriGrid, String newPiece) {
        long grid = getPiece(newPiece);
        return onValidPos(grid, newPiece.charAt(0)) && allOnVacant(addUpperNeighbours(oriGrid),newPiece);
    }
}
