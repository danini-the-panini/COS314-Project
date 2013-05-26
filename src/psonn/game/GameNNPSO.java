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

    public GameNNPSO(Board board, int maxDepth, int numHiddenUnits,
            Function activationFunction, int maxIterations, Topology topology,
            double w, double c1, double c2, double vmax, int numParticles,
            double lowerBound, double upperBound)
    {
        super(NeuralNetwork.getNumWeights(board.getNumInputs(), numHiddenUnits, 1),
                maxIterations, topology, w, c1, c2, vmax, numParticles, lowerBound, upperBound);
        
        this.board = board;
        
        fitnesses = new double[particles.length];
        for (int i = 0; i < particles.length; i++)
        {
            fitnesses[i] = 0.0;
        }
        
        init(board.getNumInputs(), numHiddenUnits, activationFunction, maxDepth);
    }

    public GameNNPSO(Board board, int maxDepth, int numHiddenUnits, Function activationFunction,
            int maxIterations, Topology topology, double w, double c1,
            double c2, double vmax, Particle[] population)
    {
        super(maxIterations, topology, w, c1, c2, vmax, population);
        
        this.board = board;
        
        fitnesses = new double[particles.length];
        for (int i = 0; i < particles.length; i++)
        {
            fitnesses[i] = population[i].getFitness();
        }
        
        init(board.getNumInputs(),numHiddenUnits, activationFunction, maxDepth);
    }
    
    private void init(int numInputs, int numHiddenUnits, Function activationFunction, int maxDepth)
    {
        nn = new NeuralNetwork[2*particles.length];
        players = new ABPlayer[2*particles.length];
        for (int i = 0; i < 2*particles.length; i++)
        {
            nn[i] = new NeuralNetwork(
                numInputs,
                numHiddenUnits,
                1, // number of outputs... this is an eval function NN so it only evaluates the state.
                activationFunction);
            players[i] = new ABPlayer(maxDepth, "Trainee #"+i, new NNEval(nn[i]));
        }
        
        gameMaster = new GameMaster(players, board, NUM_GAMES);
        
        Particle p = getBestParticle();
        
        double fit = p.getBestFitness();
        double[] values = p.getBestValues();
        
        System.out.printf("INIT, %g, %g", fit, values[0]);
        for (int i = 1; i < values.length; i++)
            System.out.printf(", %g", values[i]);
        System.out.println();
    }

    @Override
    protected void particleUpdate()
    {
        for (int i = 0; i < particles.length; i++)
        {
            nn[i].setWeights(particles[i].getValues());
            nn[i+particles.length].setWeights(particles[i].getBestValues());
        }
        
        for (int i = 0; i < particles.length; i++)
            gameMaster.playSomeGames(i);
        
        double[] subfitnesses = new double[particles.length];
        gameMaster.getResults(subfitnesses);

        for (int i = 0; i < particles.length; i++)
        {
            fitnesses[i] += subfitnesses[i];
            fitnesses[i] *= 0.5;
        }
    }
    

    @Override
    protected double getFitness(int pid, double[] values)
    {
        return fitnesses[pid];
    }

    @Override
    protected boolean intermediate(int iteration)
    {
        Particle best = getCurrentBestParticle();
        double fitness = best.getFitness();
        double[] values = best.getValues();
        System.out.printf("%d, %g, %g", iteration, fitness, values[0]);
        for (int i = 1; i < values.length; i++)
            System.out.printf(", %g", values[i]);
        System.out.println();
        
        return true;
    }

    @Override
    protected void finalise(double[] values, double fitness)
    {
        gameMaster.shutDown();
        
        System.out.printf("FINAL, %g, %g", fitness, values[0]);
        for (int i = 1; i < values.length; i++)
            System.out.printf(", %g", values[i]);
        System.out.println();
    }
    
}
