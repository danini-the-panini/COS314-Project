package psonn.game;

import gamestuff.Board;
import gamestuff.EvaluationFunc;
import psonn.NeuralNetwork;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniel
 */
public class NNEval extends EvaluationFunc
{
    private NeuralNetwork nn;

    public NNEval(NeuralNetwork nn)
    {
        this.nn = nn;
    }
    

    @Override
    public double evaluate(Board b, int player)
    {
        return nn.run(b.getInputs())[0];
    }
    
}
