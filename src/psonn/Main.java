package psonn;

import gamestuff.Board;
import java.io.File;
import psonn.game.NNEval;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JFileChooser;
import mancala.MancalaBoard;
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
    public static final int LOWER_BOUND = -1;
    public static final int NUM_HIDDEN_UNITS = 3;
    public static final int NUM_ITERATIONS = 500;
    public static final int NUM_PARTICLES = 30;
    public static final int UPPER_BOUND = 1;
    public static final int VMAX = 5;
    public static final double W = 0.72;
    public static final Class boardType = MancalaBoard.class;
    public static final int MAX_DEPTH = 1;
    
    public static Board newBoard()
    {
        try
        {
            return (Board) boardType.newInstance();
        } catch (Exception ex)
        {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static Particle[] loadParticles(String filename)
    {
        try
        {
            return PSO.deserializePopulation(new File(filename));
        }
        catch (IOException ex)
        {
            System.out.println("Error reading bot: " + ex.getMessage());
        }
        return null;
    }
    
    public static void main(String[] args)
    {
        
        Board exampleBoard = newBoard();
        
        String command = null, inputFile = null, outputFile = null;
        String numItString = null;
        
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
            if (outputFile != null) System.out.println("Ignoring -o flag.");
            if (numItString != null) System.out.println("Ignoring -i flag");
            
            NeuralNetwork nn = new NeuralNetwork(exampleBoard.getNumInputs(),
                    NUM_HIDDEN_UNITS, 1, new Function.Sigmoid());
                
            Particle[] particles = loadParticles(inputFile);
            
            if (particles == null)
            {
                System.exit(1);
            }
            else
            {
                double[] values = PSO.getBest(particles).getValues();

                nn.setWeights(values);

                TestBot.play(exampleBoard,new NNEval(nn));
            }
        }
        else if ("train".equalsIgnoreCase(command))
        {
            GameNNPSO pso = null;

            int numIterations = NUM_ITERATIONS;
            if (numItString != null)
            {
                try
                {
                    numIterations = Integer.parseInt(numItString);
                }
                catch (NumberFormatException nfe)
                {
                    numIterations = -1;
                }
                if (numIterations < 1)
                {
                    System.out.println("Invalid number of iterations, using default. (" + NUM_ITERATIONS + ")");
                    numIterations = NUM_ITERATIONS;
                }
            }
            else
            {
                System.out.println("Using default number of iterations. (" + NUM_ITERATIONS + ")");
            }
            
            if (inputFile == null)
            {
            
                if (outputFile == null)
                {
                    System.out.println("Please specify an output file.");
                    System.exit(-1);
                }
                
                pso = new GameNNPSO(exampleBoard, MAX_DEPTH,
                        NUM_HIDDEN_UNITS, new Function.Sigmoid(),
                        numIterations, new Topology.Ring(2), W, C1, C2, VMAX,
                        NUM_PARTICLES, LOWER_BOUND, UPPER_BOUND, outputFile);
            }
            else
            {
                Particle[] particles = loadParticles(inputFile);
                
                if (outputFile == null)
                {
                    System.out.println("No output file specified, overwriting input file.");
                    outputFile = inputFile;
                }
                    
                if (particles != null)
                {
                    pso = new GameNNPSO(exampleBoard, MAX_DEPTH,
                            NUM_HIDDEN_UNITS, new Function.Sigmoid(),
                            numIterations, new Topology.Ring(2), W, C1, C2,
                            VMAX, particles, outputFile);
                }

            }
            
            if (pso != null)
                pso.optimise();
        }
        else
        {
            System.out.println("Please specify one of the following commands:");
            System.out.println("\t1. play");
            System.out.println("\t2. train");
        }
    }
    
    
}
