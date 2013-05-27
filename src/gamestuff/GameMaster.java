package gamestuff;

import java.util.ArrayDeque;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author daniel
 */
public class GameMaster
{
    public static final double WIN_REWARD = 1.0;
    public static final double LOSS_PUNISHMENT = 2.0;
    private Random random = new Random();
    
    private Player[] pool;
    
    private ArrayDeque<Future<Game>> games;
    private ExecutorService executor;
    
    private int numGames;
    private Board board;
    
    private int[] sample(int n, int x)
    {
        int[] pop = new int[pool.length-1];
        for (int i = 0; i < x; i++)
            pop[i] = i;
        for (int i = x; i < pop.length; i++)
            pop[i] = i+1;
        
        for (int i = 0; i < n; i++)
        {
            int r = random.nextInt(pop.length);
            int temp = pop[i];
            pop[i] = pop[r];
            pop[r] = temp;
        }
        
        return pop;
    }

    public GameMaster(Player[] competitionPool, Board board, int numGames)
    {
        this.numGames = numGames;
        this.pool = competitionPool;
        this.board = board;
        
        games = new ArrayDeque<Future<Game>>();
        
        executor =
        // Toggle between following lines to switch between single-threading
        // and multi-threading
                
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        //Executors.newSingleThreadExecutor();
    }
    
    public void playSomeGames(int pid)
    {
        int[] comp = sample(numGames, pid);
        
        for (int i = 0; i < numGames; i++)
        {
            games.add(executor.submit(new GameExecution(board.sheep(), pid, comp[i])));
            //games.add(executor.submit(new GameExecution(board.sheep(), comp[i], pid)));
        }
    }
    
    /**
     * Returns the results from the tournament
     * @param results 
     */
    public void getResults(double[] results)
    {
        for (int i = 0; i < results.length; i++)
            results[i] = 0;
        
        while (!games.isEmpty())
        {
            try
            {
                Game game = games.remove().get();

                if (game.result != 2)
                {
                    results[game.result] += WIN_REWARD;
                    results[1-game.result] -= LOSS_PUNISHMENT;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
            }
        }
    }
    
    private class Game
    {
        int p1, p2;
        int result;

        public Game(int p1, int p2, int result)
        {
            this.p1 = p1;
            this.p2 = p2;
            this.result = result;
        }
    }
    
    private class GameExecution implements Callable<Game> {

        int[] pid;
        Player[] players;
        Board board;

        public GameExecution(Board board, int a, int b) {
            this.pid = new int[]{ a, b };
            players = new Player[]{
                pool[pid[0]],
                pool[pid[1]],
            };
            this.board = board;
        }

        @Override
        public Game call() throws Exception
        {   
            int status;
            int me;

            while ((status = board.getStatus()) == -1) 
            {
                me = board.getCurrentPlayer();
                board.applyMove(players[me].move(board, me));
            }

            return new Game(pid[0],pid[1],status);
        }
    }
    
    public void shutDown()
    {
        executor.shutdown();
    }

}
