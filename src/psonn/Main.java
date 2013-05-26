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
    public static final int NUM_ITERATIONS = 20000;
    public static final int NUM_PARTICLES = 30;
    public static final int UPPER_BOUND = 1;
    public static final int VMAX = 5;
    public static final double W = 0.72;
    public static final Class boardType = MancalaBoard.class;
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
    
    public static void main(String[] args)
    {
        if (args.length < 1) printUsage();
        else if ("play".equalsIgnoreCase(args[0]))
        {
            Board example = newBoard();
            
            NeuralNetwork nn = new NeuralNetwork(example.getNumInputs(), NUM_HIDDEN_UNITS, 1, new Function.Sigmoid());
            JFileChooser chooser = new JFileChooser(".");
            chooser.showOpenDialog(null);
            try
            {
                File file = chooser.getSelectedFile();
                if (file == null)
                {
                    System.out.println("No File chosen.");
                    System.exit(0);
                }
                
                Particle[] particles
                        = PSO.deserializePopulation(file);
                
                Particle best = particles[0];
                double bestFitness = best.getBestFitness();
                for (int i = 1; i < particles.length; i++)
                {
                    if (particles[i].getBestFitness() > bestFitness)
                    {
                        best = particles[i];
                        bestFitness = best.getBestFitness();
                    }
                }
                nn.setWeights(best.getBestValues());
                TestBot.play(newBoard(),new NNEval(nn));
            }
            catch (IOException ex)
            {
                System.out.println("Error reading bot: " + ex.getMessage());
            }
        }
        else if ("train".equalsIgnoreCase(args[0]))
        {
            if (args.length < 2) printUsage();
            else if ("scratch".equalsIgnoreCase(args[1]))
            {
                GameNNPSO pso = new GameNNPSO(newBoard(), MAX_DEPTH,
                        NUM_HIDDEN_UNITS, new Function.Sigmoid(),
                        NUM_ITERATIONS, new Topology.Ring(2), W, C1, C2, VMAX,
                        NUM_PARTICLES, LOWER_BOUND, UPPER_BOUND);

                pso.optimise();
            }
            else if ("continue".equalsIgnoreCase(args[1]))
            {
                JFileChooser chooser = new JFileChooser(".");
                chooser.showOpenDialog(null);
                try
                {
                    File file = chooser.getSelectedFile();
                    if (file == null)
                    {
                        System.out.println("No File chosen.");
                        System.exit(0);
                    }
                    
                    Particle[] particles
                            = PSO.deserializePopulation(file);
                    
                    System.out.println("null? " + (particles == null));
                    
                    GameNNPSO pso = new GameNNPSO(newBoard(), MAX_DEPTH,
                            NUM_HIDDEN_UNITS, new Function.Sigmoid(),
                            NUM_ITERATIONS, new Topology.Ring(2), W, C1, C2,
                            VMAX, particles);

                    pso.optimise();
                }
                catch (IOException ex)
                {
                    System.out.println("Error reading bot: " + ex.getMessage());
                }
            }
            else printUsage();
        }
        else printUsage();
    }
    
    
}
