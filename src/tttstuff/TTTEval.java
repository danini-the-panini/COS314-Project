package tttstuff;

import gamestuff.Board;
import gamestuff.EvaluationFunc;



/**
 *
 * @author Daniel
 */
public class TTTEval extends EvaluationFunc
{
    
    int[][] blah = {
        {0,1,2},
        {3,4,5},
        {6,7,8},
        
        {0,3,6},{1,4,7},{2,5,8},
        {0,4,8},{2,4,6}
    };

    @Override
    public double evaluate(Board board, int player)
    {
        int[] x = board.getBoard();
        
        double eval = 0.0;
        
        for (int i = 0; i < blah.length; i++)
        {
            eval += evaluate(x,blah[i],player);
        }
        
        return eval;
    }
    
    private double evaluate(int[] x, int[] i, int me)
    {
        int mine = 0, theirs = 0;
        for (int j = 0; j < 3; j++)
        {
            if (x[i[j]] == me) mine++;
            else if (x[i[j]] == (1-me)) theirs++; 
        }
        
        if (theirs == 0)
        {
            switch (mine)
            {
                case 0: return 0;
                case 1: return 1;
                case 2: return 10;
                case 3: return 100000;
                default: return 0;
            }
        }
        if (mine == 0)
        {
            switch (theirs)
            {
                case 0: return 0;
                case 1: return -1;
                case 2: return -10;
                case 3: return -110000;
                default: return 0;
            }
        }
        return 0;
    }
}
