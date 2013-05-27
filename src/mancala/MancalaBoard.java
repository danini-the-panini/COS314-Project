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
    public static final int FULL = 14;
    public static final int HALF = 7;
    public static final int M = 6;
    public static final int M1 = M+HALF;
    public static final int DRAW = 2;
    
    private int winner = -1;
    
    private static final int OPP[] = new int[]
    {
        12, 11, 10, 9, 8, 7, 13, 5, 4, 3, 2, 1, 0, 6
    };

    public MancalaBoard()
    {
        super(FULL);
        for (int i = 0; i < M; i++)
        {
            board[i] = board[i+HALF] = 4;
        }
    }

    @Override
    public int[][] getMoves()
    {
        int o = currentPlayer*HALF; // player offset
        int mancala = M+o; // player's mancala
        
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for (int i = 0; i < M; i++)
        {
            int pit = i+o;
            if (board[pit] > 0)
            {
                
                // start with i (which between 0 and 5) and add the number of
                // seeds in the current pit,  mod by board length -1, then check
                // if it were to land in the mancala .
                // This only needs player offset to get the value, everything
                // else is done relative to player one.
                
                // Figure 1 shows the indices of the board. Check for yourself
                // an example, such as sowing 17 seeds from index 2 will finish
                // in position 6.
                
                // +--+  +--+--+--+--+--+--+  +--+
                // |13|  |12|11|10| 9| 8| 7|  |  |
                // |  |  +--+--+--+--+--+--+  |  |
                // |  |  | 0| 1| 2| 3| 4| 5|  |6 |
                // +--+  +--+--+--+--+--+--+  +--+
                //
                //  Figure 1:   Board indices.
                //              Mancala's are 6 and 13
                
                if (i + board[pit]%(FULL-1) == M)
                {
                    // case when player can go again
                    // recursively get more moves
                    
                    MancalaBoard b = (MancalaBoard)this.sheep(); // copy board
                    b.applyHalfMove(pit); // apply move to copied board
                    if (b.currentPlayer != this.currentPlayer)
                    {
                        System.err.println(">.<");
                    }
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
                    else // anchor case when turn causes a win
                    {
                        moves.add(new int[]{pit});
                    }
                }
                else
                {
                    MancalaBoard b = (MancalaBoard)this.sheep(); // copy board
                    b.applyHalfMove(pit); // apply move to copied board
                    if (b.currentPlayer == this.currentPlayer)
                    {
                        //System.err.println("D:<");
                        System.out.println("<<<<<<<<<<");
                        this.print(System.out);
                        System.out.println("==========");
                        b.print(System.out);
                        System.out.println(">>>>>>>>>>");
                    }
                    
                    // anchor case where only one move can be made
                    
                    moves.add(new int[]{pit});
                }
            }
        }
        
        if (moves.isEmpty() && winner == -1)
        {
            System.err.println("We have a problem...");
            print(System.out);
        }
        
        return moves.toArray(new int[0][]);
    }
    
    // get the opposite pit
    private int opposite(int pit)
    {
        return OPP[pit];
    }
    
    // tells whether or not the given pit belongs to currentPlayer
    private boolean yours(int pit)
    {
        pit -= currentPlayer*HALF;
        return pit >= 0 && pit < HALF;
    }
    
    // apply a single move. i.e. perform the act of sowing from one pit
    public boolean applyHalfMove(int pit)
    {
        int o = currentPlayer*HALF; // player offset
        int mancala = M+o; // player's mancala
        int otherMancala = opposite(mancala); // opponent's mancala
        int finish = pit+board[pit]; // finishing point
        
        // invalid move check
        if (!yours(pit) || pit == mancala || board[pit] == 0)
            return false;
        
        // take seeds
        board[pit] = 0;
        
        // sow seeds
        for (int i = pit+1; i <= finish; i++)
        {
            if (i%FULL != otherMancala) // skip opponent's mancala
                board[i%FULL]++;
            else
                finish++; // if skipped, move finish point along
        }
        //System.out.print(" x");
        
        finish %= FULL; // wrap finish point within bounds
        
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
        
        return true;
    }
    
    // check if there is a winner
    public void gameCheck()
    {
        int a = 0, b = 0;
        for (int i = 0; i < M; i++)
        {
            a += board[i];
            b += board[i+HALF];
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
    public boolean applyMove(int[] move)
    {
        for (int i = 0; i < move.length; i++)
        {
            if (!applyHalfMove(move[i]))
                return false;
        }
        gameCheck();
        return true;
    }

    @Override
    public Board sheep()
    {
        MancalaBoard nboard = new MancalaBoard();
        
        nboard.setBoard(this.board);
        nboard.currentPlayer = this.currentPlayer;
        nboard.winner = this.winner;
        
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
    public double[] getInputs(int forwhom)
    {
        double[] inputs = new double[board.length];
        for (int i = 0; i < board.length; i++)
            inputs[i] = (double)board[(i+forwhom*HALF)%FULL];
        return inputs;
    }
    
    @Override
    public void print(PrintStream out)
    {
        out.println("             PLAYER 1");
        out.println("       12 11 10  9  8  7");
        out.println("+--+  +--+--+--+--+--+--+  +--+");
        out.printf( "|%2d|  ", board[M1]);
        for (int i = M1-1; i > M; i--)
            System.out.printf("|%2d",board[i]);
        out.println("|  |  |");
        out.println("|  |  +--+--+--+--+--+--+  |  |");
        out.print("|  |  ");
        for (int i = 0; i < M; i++)
            System.out.printf("|%2d",board[i]);
        out.printf("|  |%2d|", board[M]);
        out.println();
        out.println("+--+  +--+--+--+--+--+--+  +--+");
        out.println("        0  1  2  3  4  5");
        out.println("            PLAYER 0");
    }
    
}
