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
        System.out.print(ESC+"2J");
    }
    
    public static void play(Board b, EvaluationFunc bot)
    {
        AlphaBetaTree abtree = new AlphaBetaTree(bot);
        
        Random r = new Random();
        Scanner scan = new Scanner(System.in);
        
        int you = r.nextInt(2);
        
        clear();
        System.out.println("YOU ARE PLAYER " + you);
        System.out.println();
        
        boolean running = true;
        
        int[] x = b.getBoard();
        b.print(System.out);
        System.out.println();
        
        while (running)
        {
            if (b.getCurrentPlayer() == you) // human's turn
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
            
            x = b.getBoard();
            b.print(System.out);
            System.out.println();
            int status;
            if ((status = b.getStatus()) != -1)
            {       
                clear();
                System.out.println();
                    System.out.println("      +------------------+");
                if (status == you)
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
