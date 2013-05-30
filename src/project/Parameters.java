package project;

import java.io.Serializable;

/**
 *
 * @author Daniel
 */
public class Parameters implements Serializable
{
    public int numHiddenUnits;
    public int populationSize;
    public int plyDepth;
    public int numGames;

    public Parameters(int numHiddenUnits, int populationSize, int plyDepth, int numGames)
    {
        this.numHiddenUnits = numHiddenUnits;
        this.populationSize = populationSize;
        this.plyDepth = plyDepth;
        this.numGames = numGames;
    }
}
