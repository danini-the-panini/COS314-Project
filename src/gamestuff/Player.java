package gamestuff;

import tttstuff.TTTBoard;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import psonn.Function;
import psonn.Main;
import psonn.NeuralNetwork;
import psonn.PSO;
import psonn.Particle;
import psonn.game.NNEval;
import tttstuff.TTTEval;

/**
 *
 * @author Daniel
 */
public abstract class Player
{
    
    public abstract int[] move(Board board, int playerNum);
    
    public static final String HOST = "karlzoller.dyndns.org";
    public static final int PORT = 7000;
    
    public static final int WIN = 0, LOSS = 1, INVALID = 2, DRAW = 3, ERROR = 99;
    
    public static void main(String[] args)
    {
        Particle[] particles = Main.loadParticles(args[0]);
        
        String name = "Blind, Deaf Monkey";
        EvaluationFunc eval;
        
        if (particles == null)
            eval = new TTTEval();
        else
        {
            name = "Prometheus";
            NeuralNetwork nn = new NeuralNetwork(new TTTBoard().getNumInputs(),
                    Main.NUM_HIDDEN_UNITS, 1, new Function.Sigmoid());
            nn.setWeights(PSO.getBest(particles).getValues());
            eval = new NNEval(nn);
        }
        
        switch(playNetworkGame(new ABPlayer(9, eval), name))
        {
            case WIN:
                System.out.println(name + " wins!");
                break;
            case LOSS:
                System.out.println(name + " loses...");
                break;
            case INVALID:
                System.out.println(name + " made an invalid move.");
                break;
            case DRAW:
                System.out.println("Game was a draw.");
                break;
            default:
                System.out.println("AN ERROR OCCURED!");
                break;
        }
    }
    
    public static int playNetworkGame(Player player, String name)
    {
        int result = -1;
        
        try
        {
            Socket socket = new Socket(HOST, PORT);
            
            System.out.println("## Connected");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            //Write name
            out.write((name+"\n").getBytes());
            TTTBoard b = new TTTBoard();
            int[] board = new int[9];
            for (int k = 0; k < 9; k++)
                    board[k] = 0;

            int playerNum = in.read();
            
            System.out.println("## Got Player Num: " + playerNum);

            while(result == -1)
            {
                    char code = (char)in.read();

                    switch(code)
                    {
                            case 'B':
                                    for(int i = 0; i < 9; i++)
                                            board[i] = in.read();
                            break;

                            case 'E':
                                    
                                    b.setBoard(board);
                                    if (b.getCurrentPlayer() != playerNum)
                                        b.nextPlayer();
                                    
                                    out.write(player.move(b, playerNum)[0]);
                            break;

                            case 'R':
                                    result = in.read();
                                    System.out.println("## RESULT: " + result);
                                    if (result > DRAW) result = ERROR;
                            break;
                    }
            }
        }
        catch (IOException e)
        {
            System.out.println("## " + e.toString());
            result = ERROR;
        }
        
        return result;
    }
    
    public static int faceoff(Board board, final Player[] players)
    {
        int status;
        int current;
        
        while ((status = board.getStatus()) == -1)
        {
            current = board.getCurrentPlayer();
            board.applyMove(players[current].move(board, current));
        }
        
        return status;
    }
}
