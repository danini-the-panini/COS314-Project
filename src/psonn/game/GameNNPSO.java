package psonn.game;

import gamestuff.ABPlayer;
import gamestuff.Board;
import gamestuff.GameMaster;
import psonn.Function;
import psonn.NeuralNetwork;
import psonn.PSO;
import java.util.Random;
import psonn.Particle;
import psonn.Topology;

/**
 *
 * @author Daniel
 */
public class GameNNPSO extends PSO
{
    static Random random = new Random();
    
    private NeuralNetwork[] nn;
    
    private double[] fitnesses;
    
    private ABPlayer[] players;
    
    private GameMaster gameMaster;
    
    private Board board;
    public static final int NUM_GAMES = 5;
    
    private int numPlayers;

    public GameNNPSO(Board board, int maxDepth, int numHiddenUnits,
            Function activationFunction, int maxIterations, Topology topology,
            double w, double c1, double c2, double vmax, int numParticles,
            double lowerBound, double upperBound)
    {
        super(NeuralNetwork.getNumWeights(board.getNumInputs(), numHiddenUnits, 1),
                maxIterations, topology, w, c1, c2, vmax, numParticles, lowerBound, upperBound);
        
        this.board = board;
        
        init(board.getNumInputs(), numHiddenUnits, activationFunction, maxDepth);
    }

    public GameNNPSO(Board board, int maxDepth, int numHiddenUnits, Function activationFunction,
            int maxIterations, Topology topology, double w, double c1,
            double c2, double vmax, Particle[] population)
    {
        super(maxIterations, topology, w, c1, c2, vmax, population);
        
        this.board = board;
        
        init(board.getNumInputs(),numHiddenUnits, activationFunction, maxDepth);
    }
    
    private void init(int numInputs, int numHiddenUnits, Function activationFunction, int maxDepth)
    {
        numPlayers = 2*numParticles;
        fitnesses = new double[numPlayers];
        nn = new NeuralNetwork[numPlayers];
        players = new ABPlayer[numPlayers];
        for (int i = 0; i < numPlayers; i++)
        {
            fitnesses[i] = 0.0;
            nn[i] = new NeuralNetwork(
                numInputs,
                numHiddenUnits,
                1, // eval function has one output
                activationFunction);
            players[i] = new ABPlayer(maxDepth, new NNEval(nn[i]));
        }
        
        gameMaster = new GameMaster(players, board, NUM_GAMES);
        
        Particle p = getBest(pbest);
        
        double fit = p.getFitness();
        double[] values = p.getValues();
        
        System.out.printf("INIT, %g, %g", fit, values[0]);
        for (int i = 1; i < values.length; i++)
            System.out.printf(", %g", values[i]);
        System.out.println();
    }

    @Override
    protected void updateFitness()
    {
        for (int i = 0; i < numParticles; i++)
        {
            nn[i].setWeights(particles[i].getValues());
            nn[i+numParticles].setWeights(pbest[i].getValues());
        }
        
        for (int i = 0; i < numPlayers; i++)
            gameMaster.playSomeGames(i);
        
        gameMaster.getResults(fitnesses);
        
        for (int i = 0; i < numParticles; i++)
        {
            particles[i].setFitness(fitnesses[i]);
            pbest[i].setFitness(fitnesses[i+numParticles]);
        }
    }

    @Override
    protected boolean intermediate(int iteration)
    {
        Particle cbest = getBest(particles);
        Particle gbest = getBest(pbest);
        
        System.out.printf("%d: cbest=%g, gbest=%g",iteration,cbest.getFitness(),
                gbest.getFitness());
        System.out.println();
        
        return true;
    }

    @Override
    protected void finalise()
    {
        gameMaster.shutDown();
        
        Particle gbest = getBest(pbest);
        double[] values = gbest.getValues();
        double fitness = gbest.getFitness();
        
        System.out.printf("FINAL, %g, %g", fitness, values[0]);
        for (int i = 1; i < values.length; i++)
            System.out.printf(", %g", values[i]);
        System.out.println();
    }
    
}
