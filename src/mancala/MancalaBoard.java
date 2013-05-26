package mancala;

import gamestuff.Board;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author daniel
 */
public class MancalaBoard extends Board
{
    public static final int BL = 14;
    public static final int HB = 7;
    public static final int M = 6;
    public static final int M1 = M+HB;
    public static final int DRAW = 2;
    
    private int winner = -1;
    
    private int opposite[] = new int[]
    {
        12, 11, 10, 9, 8, 7, 13, 5, 4, 3, 2, 1, 0, 6
    };

    public MancalaBoard()
    {
        super(BL);
        for (int i = 0; i < M; i++)
        {
            board[i] = board[i+HB] = 4;
        }
    }

    @Override
    public int[][] getMoves()
    {
        int o = currentPlayer*HB; // player offset
        int mancala = M+o; // player's mancala
        
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for (int i = 0; i < M; i++)
        {
            int pit = i+o;
            if (board[pit] > 0)
            {
                
                if (pit + board[pit] == mancala)
                {
                    // case when player can go again
                    // recursively get more moves
                    
                    MancalaBoard b = (MancalaBoard)this.sheep(); // copy board
                    b.applyHalfMove(pit); // apply move to copied board
                    b.gameCheck(); // check if move did not cause a win
                    if (b.winner == -1)
                    {
                        // get future moves
                        int[][] m1 = b.getMoves();
                        for (int j = 0; j < m1.length; j++)
                        {
                            // prepend future move with current move
                            int[] m2 = new int[m1[j].length+1];
                            System.arraycopy(m1[j],0,m2,1,m1[j].length);
                            m2[0] = pit;
                            
                            moves.add(m2);
                        }
                    }
                }
                else
                {
                    // anchor case where only one move can be made
                    
                    moves.add(new int[]{pit});
                }
            }
        }
        
        //if (moves.isEmpty()) return null;
        
        return moves.toArray(new int[0][]);
    }
    
    // get the opposite pit
    private int opposite(int pit)
    {
        return opposite[pit];
    }
    
    // tells whether or not the given pit belongs to currentPlayer
    private boolean yours(int pit)
    {
        pit -= currentPlayer*HB;
        return pit >= 0 && pit < HB;
    }
    
    // apply a single move. i.e. perform the act of sowing from one pit
    public void applyHalfMove(int pit)
    {
        int o = currentPlayer*HB; // player offset
        int mancala = M+o; // player's mancala
        int otherMancala = opposite(mancala); // opponent's mancala
        int finish = pit+board[pit]; // finishing point
        
        // invalid move check
        if (!yours(pit) || pit == mancala || board[pit] == 0)
            return;
        
        // take seeds
        board[pit] = 0;
        
        // sow seeds
        for (int i = pit+1; i <= finish; i++)
        {
            if (i != otherMancala) // skip opponent's mancala
                board[i%BL]++;
            else
                finish++; // if skipped, move finish point along
        }
        //System.out.print(" x");
        
        finish %= BL; // wrap finish point within bounds
        
        // next player if move did not finish in player's mancala
        if (finish != mancala)
        {
        
            // if finish point is on player's side
            // and is not his/her mancala
            // and only has one seed in it
            if (yours(finish) && finish != mancala && board[finish] == 1)
            {
                int opp = opposite(finish);

                // capture seeds from both pits
                board[mancala] += board[opp]+1;
                board[opp] = board[finish] = 0;
            }
            
            nextPlayer();
        }
        
    }
    
    // check if there is a winner
    public void gameCheck()
    {
        int a = 0, b = 0;
        for (int i = 0; i < 6; i++)
        {
            a += board[i];
            b += board[i+HB];
        }
        if (a == 0 || b == 0)
        {
            board[M] += a;
            board[M1] += b;
            
            if (board[M] > board[M1])
                winner = 0;
            else if (board[M] < board[M1])
                winner = 1;
            else winner = DRAW; // draw (possible?)
        }
    }

    @Override
    public void applyMove(int[] move)
    {
        for (int i = 0; i < move.length; i++)
        {
            applyHalfMove(move[i]);
        }
        gameCheck();
    }

    @Override
    public Board sheep()
    {
        MancalaBoard nboard = new MancalaBoard();
        
        nboard.setBoard(this.board);
        if (nboard.getCurrentPlayer() != this.getCurrentPlayer())
            nboard.nextPlayer();
        
        return nboard;
    }

    @Override
    public int getStatus()
    {
        return winner;
    }

    @Override
    public int getNumInputs()
    {
        return board.length;
    }

    @Override
    public double[] getInputs()
    {
        double[] inputs = new double[board.length];
        for (int i = 0; i < board.length; i++)
            inputs[i] = (double)board[i];
        return inputs;
    }
    
    @Override
    public void print(PrintStream out)
    {
        out.println("   +--+--+--+--+--+--+");
        out.printf("%2d ", board[M1]);
        for (int i = M1-1; i > M; i--)
            System.out.printf("|%2d",board[i]);
        out.println("|");
        out.println("   +--+--+--+--+--+--+");
        out.print("   ");
        for (int i = 0; i < M; i++)
            System.out.printf("|%2d",board[i]);
        out.printf("| %2d", board[M]);
        out.println();
        out.println("   +--+--+--+--+--+--+");
    }
    
}
