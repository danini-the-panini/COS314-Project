package gamestuff;

/**
 *
 * @author Daniel
 */
public class ABPlayer extends Player
{
    private AlphaBetaTree abtree;
    
    private int maxDepth;

    public ABPlayer(int maxDepth, EvaluationFunc eval)
    {
        
        this.maxDepth = maxDepth;
        abtree = new AlphaBetaTree(eval);
    }

    @Override
    public int[] move(Board board, int playerNum)
    {
        return abtree.evaluate(board.makeCopy(), maxDepth);
    }
    
}
