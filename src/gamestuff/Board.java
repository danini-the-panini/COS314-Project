package gamestuff;

import java.io.PrintStream;

/**
 * @author Daniel
 */
public abstract class Board
{
    protected int[] board;
    protected int currentPlayer = 0;

    public Board(int boardSize)
    {
        this.board = new int[boardSize];
    }
    
    public void nextPlayer()
    {
        currentPlayer = 1-currentPlayer;
    }

    public int getCurrentPlayer()
    {
        return currentPlayer;
    }
    
    public abstract int[][] getMoves();
    
    public abstract void applyMove(int[] move);

    public int[] getBoard()
    {
        return board;
    }
    
    public abstract int getNumInputs();
    
    public abstract double[] getInputs();

    public void setBoard(int[] board)
    {
        System.arraycopy(board, 0, this.board, 0, board.length);
    }
    
    public abstract Board sheep();
    
    public abstract int getStatus();
    
    public abstract void print(PrintStream out);
}
