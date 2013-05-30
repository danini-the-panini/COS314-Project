package psonn.game;

import gamestuff.AlphaBetaTree;
import gamestuff.Board;
import gamestuff.EvaluationFunc;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author daniel
 */
public class TestBot
{
    public static String ESC = "\033[";
    
    public static void clear()
    {
        System.out.print(ESC+"2J\r");
    }
    
    public static void play(Board b, EvaluationFunc bot)
    {
        AlphaBetaTree abtree = new AlphaBetaTree(bot);
        
        Random r = new Random();
        Scanner scan = new Scanner(System.in);
        
        boolean iStart = Math.random()<0.5;
        
        if (iStart)
            b.nextPlayer();
        
        clear();
        System.out.println((iStart ? "I" : "YOU") + " START!");
        System.out.println();
        
        boolean running = true;
        
        b.print(System.out);
        System.out.println();
        
        while (running)
        {
            if (b.getCurrentPlayer() == 0) // human's turn
            {
                System.out.print("Enter a move: ");
                boolean valid;
                while (!(valid = b.applyMove(new int[]{scan.nextInt()})))
                {
                    System.out.println("INVALID MOVE");
                    System.out.print("Enter a move: ");
                }
                clear();
            }
            else // computer's turn
            {
                System.out.print("Computer is thinking...");
		int[] move = abtree.evaluate(b.makeCopy(), 9);
                clear();
                System.out.print("Computer moves");
		for (int i = 0; i < move.length; i++)
		{
			System.out.printf(", %d", move[i]);
		}
                System.out.println(".");
                System.out.println();
                b.applyMove(move);
            }
            
            b.print(System.out);
            System.out.println();
            int status;
            if ((status = b.getStatus()) != -1)
            {       
                System.out.println();
                    System.out.println("      +------------------+");
                if (status == 0)
                    System.out.println("      | <<< You  Win >>> |");
                else if (status == 2)
                    System.out.println("      | game was a draw  |");
                else
                    System.out.println("      | >>> You LOSE <<< |");
                    System.out.println("      +------------------+");
                
                running = false;
            }
        }
    }
}
