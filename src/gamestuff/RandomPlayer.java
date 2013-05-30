/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestuff;

import java.util.Random;

/**
 *
 * @author Daniel
 */
public class RandomPlayer extends Player
{
    private Random random = new Random(System.currentTimeMillis());

    @Override
    public int[] move(Board board, int playerNum)
    {
        int[][] moves = board.getMoves();
        return moves[random.nextInt(moves.length)];
    }
    
}
