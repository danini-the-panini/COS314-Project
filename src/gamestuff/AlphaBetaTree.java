package gamestuff;

/**
 * Generic MinMax tree using AlphaBeta pruning. Must be given a evaluation function to evaluate leaf nodes.
 * @author Daniel
 */
public class AlphaBetaTree
{
    private EvaluationFunc efunc;

    /**
     * Creates a new AlphaBetaTree.
     * @param efunc The evaluation function to use for evaluating leaf nodes in this tree.
     */
    public AlphaBetaTree(EvaluationFunc efunc)
    {
        this.efunc = efunc;
    }
    
    /**
     * Evaluates a game board using MinMax with AlphaBeta pruning.
     * Evaluates win/loss/draw as INT_MAX/INT_MIN/0 respectively,
     * with adjustments for depth (haven't seen this have much of an effect so far).
     * @param board Game board to evaluate.
     * @param maxDepth Depth limit.
     * @return The best place to move according to the calculations.
     */
    public int[] evaluate(Board board, int maxDepth)
    {
        // Get my index (assuming it's my turn)
        int player = board.getCurrentPlayer();
        
        double cvalue; // holds temporary calculated value for comparison
        double value = Double.NEGATIVE_INFINITY; // holds the best value found so far.
        int choice = -1; // holds best move found so far.
        
        int[][] moves = board.getMoves();
        
        // go through all the moves and evaluate each one
        Board nb;
        for (int i = 0; i < moves.length; i++)
        {
            nb = board.sheep();
            nb.applyMove(moves[i]);

            cvalue = alphaBeta(value, Double.POSITIVE_INFINITY, nb, 1, maxDepth-1, player);

            if (cvalue > value)
            {
                value = cvalue;
                choice = i;
            }
        }
        //FOO System.out.println("Choosing: " + value);
        
        // if there's nothing you can do, do something?
        if (choice == -1)
            return null;
        
        return moves[choice];
    }
    
    // recursive helper function to find the value of a particular node using alpha beta pruning
    private double alphaBeta(double alpha, double beta, Board board, int depth, int maxDepth, int player)
    {
        
        String tabs = new String();
        for (int i = 0; i < depth; i++)
            tabs +="    ";
        
        int[][] moves = board.getMoves();
        
        // base case if game over or depth limit reached
        if (moves == null || moves.length == 0 || depth == maxDepth)
        {
            double eval =  efunc.evaluate(board, player);
            //FOO System.out.println(tabs+"D: " + eval);
            return eval;
        }
        Board nb;
        
        // max
        if (depth % 2 == 0)
        {
            for (int i = 0; i < moves.length; i++)
            {
                nb = board.sheep();
                nb.applyMove(moves[i]);
                alpha = Math.max(alpha, alphaBeta(alpha, beta, nb, depth+1, maxDepth, player));
                if (beta <= alpha) {
                    //FOO System.out.println(tabs+"A " + alpha + ": (" + beta + "<=" + alpha + ")");
                    return alpha;
                }
            }
            //FOO System.out.println(tabs+"A " + alpha);
            return alpha;
        }
        
        // min
        for (int i = 0; i < moves.length; i++)
        {
            nb = board.sheep();
            nb.applyMove(moves[i]);
            beta = Math.min(beta, alphaBeta(alpha, beta, nb, depth+1, maxDepth, player));
            
            if (beta <= alpha) {
                //FOO System.out.println(tabs+"B " + beta + ": (" + beta + "<=" + alpha + ")");
                return beta;
            }
        }

        //FOO System.out.println(tabs+"B " + beta);
        return beta;
    }
    
}
