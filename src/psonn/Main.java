package psonn;

import gamestuff.Board;
import java.io.File;
import psonn.game.NNEval;
import java.io.IOException;
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
    public static final int NUM_ITERATIONS = 1000;
    public static final int NUM_PARTICLES = 30;
    public static final int UPPER_BOUND = 1;
    public static final int VMAX = 5;
    public static final double W = 0.72;
    public static final Class boardType = TTTBoard.class;
    public static final int MAX_DEPTH = 5;
    
    public static void printUsage()
    {
        System.out.println("Please supply valid command line arguments:");
        System.out.println("\tplay: play against a neural network. Asks user for file to load.");
        System.out.println("\ttrain: train a neural network");
        System.out.println("\t\ttrain scratch: train a neural network from a random starting point");
        System.out.println("\t\ttrain continue: train an existing neural network. Asks user for folder to load.");
        System.out.println("");
        System.exit(-1);
    }
    
    public static Board newBoard()
    {
        try
        {
            return (Board) boardType.newInstance();
        } catch (ReflectiveOperationException ex)
        {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    public static Particle[] loadParticles()
    {
        JFileChooser chooser = new JFileChooser(".");
        chooser.showOpenDialog(null);
        try
        {
            File file = chooser.getSelectedFile();
            if (file == null)
            {
                System.out.println("No File chosen.");
                return null;
            }

            return PSO.deserializePopulation(file);
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
        
        if (args.length < 1) printUsage();
        else if ("play".equalsIgnoreCase(args[0]))
        {
            
            NeuralNetwork nn = new NeuralNetwork(exampleBoard.getNumInputs(),
                    NUM_HIDDEN_UNITS, 1, new Function.Sigmoid());
                
            Particle[] particles = loadParticles();
            
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
        else if ("train".equalsIgnoreCase(args[0]))
        {
            GameNNPSO pso = null;
            
            if (args.length < 2) printUsage();
            else if ("scratch".equalsIgnoreCase(args[1]))
            {
                pso = new GameNNPSO(exampleBoard, MAX_DEPTH,
                        NUM_HIDDEN_UNITS, new Function.Sigmoid(),
                        NUM_ITERATIONS, new Topology.Ring(2), W, C1, C2, VMAX,
                        NUM_PARTICLES, LOWER_BOUND, UPPER_BOUND);

            }
            else if ("continue".equalsIgnoreCase(args[1]))
            {
                Particle[] particles = loadParticles();
                    
                if (particles != null)
                {
                    pso = new GameNNPSO(exampleBoard, MAX_DEPTH,
                            NUM_HIDDEN_UNITS, new Function.Sigmoid(),
                            NUM_ITERATIONS, new Topology.Ring(2), W, C1, C2,
                            VMAX, particles);
                }

            }
            else printUsage();
            
            if (pso != null)
                pso.optimise();
        }
        else printUsage();
    }
    
    
}
