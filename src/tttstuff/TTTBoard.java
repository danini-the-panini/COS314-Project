package tttstuff;

import gamestuff.Board;
import java.io.PrintStream;

/**
 *
 * @author Daniel
 */
public class TTTBoard extends Board
{
    public static final int E = 2; // empty
    
    private int numMoves;
    
    
    public static final int[][] ROWS = {
        {0,1,2},
        {3,4,5},
        {6,7,8},
        
        {0,3,6},{1,4,7},{2,5,8},
        {0,4,8},{2,4,6}
    };

    public TTTBoard()
    {
        super(9);
        for (int i = 0; i < 9; i++)
            board[i] = E; // empty board
        numMoves = 9;
    }

    @Override
    public int[][] getMoves()
    {
        int[][] moves = new int[numMoves][1];
        int j = 0;
        for (int i = 0; i < board.length; i++)
        {
            if (board[i] == E)
            {
                moves[j++][0] = i;
            }
        }
        return moves;
    }

    @Override
    public boolean applyMove(int[] move)
    {
        int m = move[0];
        if (board[m] != E) return false; // invalid move...
        board[m] = currentPlayer;
        numMoves--;
        nextPlayer();
        return true;
    }

    @Override
    public void setBoard(int[] board)
    {
        super.setBoard(board);
        numMoves = 0;
        for (int i = 0; i < this.board.length; i++)
            if (this.board[i] == E) numMoves++;
    }
    
    public int get(int i, int j)
    {
        return board[i*3+j];
    }

    @Override
    public int getNumInputs()
    {
        return board.length*2;
    }

    // this is for tic-tac-toe or similar games. (only)
    public static double[][] enc = {{0,1},{1,0},{0,0}};
    
    @Override
    public double[] getInputs(int forwhom)
    {
        double[] inputs = new double[getNumInputs()];
        int v;
        for (int i = 0; i < board.length; i++)
        {
            v = getValue(i,forwhom);
            inputs[i*2] = enc[v][0];
            inputs[i*2+1] = enc[v][1];
        }
        return inputs;
    }
    
    private int getValue(int i, int forwhom)
    {
        int value = board[i];
        if (forwhom == 1)
            if (value != 2) value = 1-value;
        return value;
    }

    @Override
    public Board sheep()
    {
        TTTBoard nboard = new TTTBoard();
        
        nboard.setBoard(this.board);
        if (nboard.getCurrentPlayer() != this.getCurrentPlayer())
            nboard.nextPlayer();
        
        return nboard;
    }
    
    @Override
    public int getStatus()
    {
        // check for winning moves
        for (int i = 0; i < ROWS.length; i++)
        {
            if (board[ROWS[i][0]] == board[ROWS[i][1]]
                    && board[ROWS[i][1]] == board[ROWS[i][2]]
                    && board[ROWS[i][0]] != E)
            {
                return board[ROWS[i][0]]; // return the player that won
            }
        }
        
        // check if the game is still going
        for (int i = 0; i < board.length; i++)
            if (board[i] == E) return -1; // game is still going
        
        return E; // draw
    }
    
    @Override
    public void print(PrintStream out)
    {
        out.println("-----");
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                int k = i*3+j;
                char c = '?';
                if (board[k] == TTTBoard.E) c = ' ';
                else if (board[k] == 0) c = 'X';
                else if (board[k] == 1) c = '0';
                System.out.print((k) + "" + (c) + " ");
            }
            out.println();
        }
    }
    
}
