package project;



import gamestuff.ABPlayer;
import gamestuff.Board;
import gamestuff.GameMaster;
import gamestuff.Player;
import gamestuff.RandomPlayer;
import java.io.File;
import psonn.game.NNEval;
import java.io.IOException;
import mancala.MancalaBoard;
import psonn.Function;
import psonn.NeuralNetwork;
import psonn.PSO;
import psonn.Particle;
import psonn.Topology;
import psonn.game.GameNNPSO;
import psonn.game.TestBot;
import tttstuff.TTTBoard;


/**
 *
 * @author Daniel
 */
public class Main
{
    public static final double C1 = 1.4;
    public static final double C2 = 1.4;
    public static final double LOWER_BOUND = -0.1;
    public static final int NUM_HIDDEN_UNITS = 3;
    public static final int NUM_ITERATIONS = 500;
    public static final int NUM_PARTICLES = 30;
    public static final double UPPER_BOUND = 0.1;
    public static final int VMAX = 5;
    public static final double W = 0.72;
    public static final Class BOARD_TYPE = MancalaBoard.class;
    public static final int NUM_GAMES = 5;
    public static final int MAX_DEPTH = 1;
    
    public static Board newBoard()
    {
        try
        {
            return (Board) BOARD_TYPE.newInstance();
        } catch (Exception ex)
        {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static Population loadParticles(String filename)
    {
        try
        {
            return Population.deserializePopulation(new File(filename));
        }
        catch (IOException ex)
        {
            System.out.println("Error reading bot: " + ex.getMessage());
        }
        return null;
    }
    
    public static int parseInt(String str, int def)
    {
        if (str == null) return def;
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException nfe)
        { }
        return def;
    }
    
    public static void main(String[] args)
    {
        
        Board exampleBoard = newBoard();
        
        String command = null, inputFile = null, outputFile = null;
        String numItString = null, numGamesString = null, numParticlesString = null,
                plyDepthString = null, numHiddenUnitsString = null;
        
        if (args.length > 0)
            command = args[0];
        
        // collect command line flags
        for (int i = 1; i < args.length-1; i+=2)
        {
            if ("-f".equalsIgnoreCase(args[i]))
                inputFile = args[i+1];
            else if ("-o".equalsIgnoreCase(args[i]))
                outputFile = args[i+1];
            else if ("-i".equalsIgnoreCase(args[i]))
                numItString = args[i+1];
            else if ("-g".equalsIgnoreCase(args[i]))
                numGamesString = args[i+1];
            else if ("-p".equalsIgnoreCase(args[i]))
                numParticlesString = args[i+1];
            else if ("-d".equalsIgnoreCase(args[i]))
                plyDepthString = args[i+1];
            else if ("-h".equalsIgnoreCase(args[i]))
                numHiddenUnitsString = args[i+1];
            else
                System.out.println("Unrecognised flag: " + args[i]);
        }
        
        if ("play".equalsIgnoreCase(command))
        {
            if (inputFile == null)
            {
                System.out.println("No input file specified. Please specify a file using the -f flag.");
                System.exit(-1);
            }
                
            Population pop = loadParticles(inputFile);
            
            if (pop == null)
            {
                System.exit(1);
            }
            else
            {
                NeuralNetwork nn = new NeuralNetwork(exampleBoard.getNumInputs(),
                        pop.params.numHiddenUnits, 1, new Function.Sigmoid());
            
                double[] values = PSO.getBest(pop.particles).getValues();

                nn.setWeights(values);

                TestBot.play(exampleBoard,new NNEval(nn));
            }
        }
        else if ("train".equalsIgnoreCase(command))
        {
            GameNNPSO pso = null;

            int numIterations = parseInt(numItString, NUM_ITERATIONS);
            
            if (inputFile == null)
            {
                
                int numHiddenUnits = parseInt(numHiddenUnitsString, NUM_HIDDEN_UNITS);
                int numGames = parseInt(numGamesString, NUM_GAMES);
                int plyDepth = parseInt(plyDepthString, MAX_DEPTH);
                int numParticles = parseInt(numParticlesString, NUM_PARTICLES);
            
                if (outputFile == null)
                {
                    System.out.println("Please specify an output file.");
                    System.exit(-1);
                }
                
                pso = new GameNNPSO(exampleBoard, plyDepth, numGames,
                        numHiddenUnits, new Function.Sigmoid(),
                        numIterations, new Topology.Ring(2), W, C1, C2, VMAX,
                        numParticles, LOWER_BOUND, UPPER_BOUND, outputFile);
                pso.setMetaData(new Parameters(numHiddenUnits, numIterations, plyDepth, numGames));
            }
            else
            {
                Population pop = loadParticles(inputFile);
                
                if (outputFile == null)
                {
                    System.out.println("No output file specified, overwriting input file.");
                    outputFile = inputFile;
                }
                
                    
                if (pop == null)
                {
                    System.exit(-1);
                }
                else
                {
                    int numGames = parseInt(numGamesString, pop.params.numGames);
                    int plyDepth = parseInt(plyDepthString, pop.params.plyDepth);
                    
                    pso = new GameNNPSO(exampleBoard, plyDepth,
                            numGames, pop.params.numHiddenUnits,
                            new Function.Sigmoid(),
                            numIterations, new Topology.Ring(2), W, C1, C2,
                            VMAX, pop.particles, outputFile);
                    pso.setMetaData(pop.params);
                }

            }
            
            if (pso != null)
                pso.optimise();
        }
        else if ("test".equalsIgnoreCase(command))
        {
            
            if (inputFile == null)
            {
                System.out.println("No input file specified. Please specify a file using the -f flag.");
                System.exit(-1);
            }
            
            Population pop = loadParticles(inputFile);

            if (pop == null)
            {
                System.exit(-1);
            }
            else
            {
                int numIterations = parseInt(numItString, NUM_ITERATIONS);
                
                NeuralNetwork nn = new NeuralNetwork(exampleBoard.getNumInputs(),
                        pop.params.numHiddenUnits, 1, new Function.Sigmoid());
            
                double[] values = PSO.getBest(pop.particles).getValues();

                nn.setWeights(values);
                
                GameMaster gm = new GameMaster(new Player[]{
                
                        new ABPlayer(pop.params.plyDepth, new NNEval(nn)),
                        new RandomPlayer()
                
                }, exampleBoard, numIterations);
                
                gm.playSomeGames(0, 1);
                
                System.out.printf("Win rate: %.3g%%",100.0*gm.getWinRatio(0));
                System.out.println();
                
                gm.shutDown();
            }
        }
        else
        {
            System.out.println("Please specify one of the following commands:");
            System.out.println("\t1. play");
            System.out.println("\t2. train");
            System.out.println("\t3. test");
        }
    }
    
    
}
