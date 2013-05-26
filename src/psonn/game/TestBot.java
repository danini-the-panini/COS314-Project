/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public static void play(Board b, EvaluationFunc bot)
    {
        AlphaBetaTree abtree = new AlphaBetaTree(bot);
        
        Random r = new Random();
        Scanner scan = new Scanner(System.in);
        
        int me = r.nextInt(2);
        
        boolean running = true;
        
        int[] x = b.getBoard();
        b.print(System.out);
        
        while (running)
        {
            
            if (b.getCurrentPlayer() == me)
            {
                System.out.print("Enter a move: ");
                b.applyMove(new int[]{scan.nextInt()});
            }
            else
            {
                b.applyMove(abtree.evaluate(b.sheep(), 9));
            }
            
            x = b.getBoard();
        b.print(System.out);
            int status;
            if ((status = b.getStatus()) != -1)
            {
                System.out.println("End of game: " + status);
                running = false;
            }
        }
    }
}
