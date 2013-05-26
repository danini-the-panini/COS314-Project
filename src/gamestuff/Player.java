package gamestuff;

import gamestuff.Board;
import tttstuff.TTTBoard;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Daniel
 */
public abstract class Player
{
    private String name;

    public Player(String name)
    {
        this.name = name;
    }
    
    public abstract int[] move(Board board, int playerNum);
    
    public static final String HOST = "karlzoller.dyndns.org";
    public static final int PORT = 7000;
    
    public static final int WIN = 0, LOSS = 1, INVALID = 2, DRAW = 3, ERROR = 99;
    
    public static int playNetworkGame(Player player)
    {
        int result = -1;
        
        try
        {
            Socket socket = new Socket(HOST, PORT);
            
            System.out.println("## Connected");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            //Write name
            out.write((player.name+"\n").getBytes());
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
    
    private static AtomicInteger[] results;
    
    public static void setupNetworkGames(int numGames)
    {
        results = new AtomicInteger[numGames];
        for (int i = 0; i < numGames; i++)
            results[i] = new AtomicInteger(-1);
    }
    
    public static void spawnNetworkGame(final Player player, final int id)
    {
        new Thread(new Runnable() {

            @Override
            public void run()
            {
                results[id].set(playNetworkGame(player));
            }
        }).start();
    }
    
    public static int getNetworkGameResult(int id)
    {
        return results[id].get();
    }
    
    // TODO: this /could/ be generic...
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
