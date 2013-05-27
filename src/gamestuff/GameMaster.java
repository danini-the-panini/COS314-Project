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
            games.add(executor.submit(new GameExecution(board.sheep(), pid, comp[i], 0)));
            games.add(executor.submit(new GameExecution(board.sheep(), comp[i], pid, 1)));
        }
    }
    
    /**
     * Returns the loss-win ratio of each player.
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

                results[game.id] += game.result;
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
            }
        }
        
        
        for (int i = 0; i < results.length; i++)
            results[i] /= (double)(numGames*2);
    }
    
    private class Game
    {
        int id;
        int result;

        public Game(int id, int result)
        {
            this.id = id;
            this.result = result;
        }
    }
    
    private class GameExecution implements Callable<Game> {

        int[] pid;
        Player[] players;
        Board board;
        int important;

        public GameExecution(Board board, int a, int b, int important) {
            this.pid = new int[]{ a, b };
            players = new Player[]{
                pool[pid[0]],
                pool[pid[1]],
            };
            this.board = board;
            this.important = important;
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

            return new Game(pid[important], status == important ? 0 : 1); // 1 for win, 0 for loss
        }
    }
    
    public void shutDown()
    {
        executor.shutdown();
    }

}
