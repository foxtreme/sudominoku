
// Sudominoku, MCPC 2011 Problem D   Alternate Java version by Andy Harrington

/* basic recursive solution with backtracking, complicated by the fact that
   you must keep track of both numbers on the board and dominoes used.
   To avoid many near misses with small one-cell holes, the main iteration
   is filling the board left to right and down.
   The inner loop is trying different dominoes.
*/
package data;

import java.util.*;
import java.io.*;
import static java.lang.Math.*;

public class sudominoku {
    static int N;
    static int[] rowHas,     // *Has short for arrays rowHas, colHas, blockHas
                 colHas;     //   ints are bitfields for used 1-9: n-> 1<<(n-1),
    static int[][] blockHas, //   one set for each row, col, 3x3 block
                   board;    // 9x9 with numbers 1-9 and 0 for unfilled
    static boolean[][] used; // used[small][big] true if use domino small+big
    static String sol;       // solution string
//    static long tries;       //JUDGE data
//    static Scanner keyb = new Scanner(System.in);  // pause in debugging seq
    
    static boolean addNum(int n, int r, int c) {//if can add, true; update *Has
        int bit = 1 << (n-1);  // *Has arrays use bitfield for used numbers
        if (board[r][c] ==  0 && (bit & blockHas[r/3][c/3]) == 0 &&
            (bit & rowHas[r]) == 0 && (bit & colHas[c]) == 0) { //num not used
            rowHas[r] += bit;
            colHas[c] += bit;
            blockHas[r/3][c/3] += bit;
            board[r][c] = n;
            return true;
        }
        return false;
    }

    static void subNum(int r, int c) {//backtrack placement
        int n = board[r][c];
//        if (n==0) {  // DEBUG
//            pr("Removing nothing");
//            return;
//        }
        int bit = 1 << (n-1);
        rowHas[r] -= bit;
        colHas[c] -= bit;
        blockHas[r/3][c/3] -= bit;
        board[r][c] = 0;
    }

    /** assume legal first end at r0,c0; try second number n at r,c */
    static void addSecond(int r0, int c0, int r, int c, int n) {
        if (c == 9 || r == 9) return;
        if (addNum(n, r, c)) {
            nextLoc(r0, c0);
            subNum(r, c); // backtrack before next test
        }
    }

    static void pinFirstEnd(int r, int c, int n1, int n2) {
//        tries++;  //DEBUG
        if (addNum(n1, r, c)) {
            addSecond(r, c, r, c+1, n2);
            addSecond(r, c, r+1, c, n2);
            subNum(r, c); // backtrack before next test
        }
    }

    /** tries everything with pieces at next open place after r,c */
    static void nextLoc(int r, int c) {
        if (ONE_SOL && sol.length() > 0) return; //force stop after solution
        do { //left to right, then down to next row
            c++;
            if (c == 9) {
                c = 0;
                r++;
            }
            if (r == 9) {
               sol += bStr(); // allows for many sudominoku solutions 
               return; //Two different domino arrangements may give same sudoku
            }
        } while (board[r][c] > 0); // skip occupied spots
//        pr(String.format("Try (%d, %d)%n%s", r, c, bStr()));
//        if (DEBUG)keyb.nextLine(); // watch steps unfold
        for (int small = 1; small < 9; small++) // try unused dominos
            for (int big = small+1; big <= 9; big++) {
                if (used[small][big]) continue;
                used[small][big] = true;
                pinFirstEnd(r, c, small, big);
                pinFirstEnd(r, c, big, small);
                used[small][big] = false; // backtrack before next test
            }
    }

    static String bStr() { // string for board; allow incomplete with '_'
        String s = "", nl = String.format("%n");
        for (int[] row : board)  {
            for (int n : row)
                s += ((n== 0) ? "_" : ""+n);
            s += nl;
        }
        return s;
    }

    static void addInput(int n, String loc) { // parameters like 5, "B7"
        if (!addNum(n, loc.charAt(0)-'A', loc.charAt(1) - '1')) //put on board
            pr("Double used spot! " + loc);  //DEBUG
    }

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("src/data/sudominoku1.txt"));
        N = in.nextInt();//numero de piezas de domino a mostrar
        int dataSetN = 1;//indice del juego que se va a resolver
        while (N > 0) { //porque 0 es el caracter de finalizacion del archivo
            board = new int[9][9]; //tablero
            used = new boolean[10][10]; //ignore 0 indices celdas usadas del tablero
            rowHas = new int[9]; //filas 
	    colHas = new int[9]; //columnas
            blockHas  = new int[3][3]; //bloque de matriz 
            sol = "";
//            tries = 0;
            for (int i = 0; i < N; i++) { // recorre  de  0  asta  N que es el numero de pistas iniciales
                int n1 = in.nextInt(); // valor de la primera mitad de la pieza de domino
                addInput(n1, in.next());// esto es una pieza de domino primera mitad
                int n2 = in.nextInt(); //valor de la segunda mitad de la pieza de domino
                addInput(n2, in.next());// esto es una pieza de domino segunda mitad
                used[min(n1, n2)][max(n1, n2)] = true; //marca la pieza de domino como usada
            }
            for (int i = 1; i <= 9; i++) 
                addInput(i, in.next());
//            pr("init:\n"+bStr());
            nextLoc(0, -1);
            System.out.println("Puzzle " + dataSetN);
            System.out.print(sol);
//            pr("Tries: " + tries);
            dataSetN++;
            N = in.nextInt();
        }
    }

    // only judge checks follow /////////////////////////

    static String inSet(int t) { // expand bitset as string
        String s= "";
        for (int n = 1; n <= 9; n++)
           s += (((t&(1<<(n-1)))
                  == 0) ? "_" : (""+n));
        return s;
    }
    
    static boolean DEBUG = false;
    static boolean ONE_SOL = true;  // for longer check make false
    
    static void prp(String msg) { // debug message
        if (DEBUG) System.err.print(msg);
    }
    
    static void pr(String msg) {// debug message with newline
        prp(msg+"\n");
    }
}

