package psonn;

/**
 *
 * @author Daniel
 */
public class NeuralNetwork
{
    private double[][] inputWeights;
    private double[][] hiddenWeights;
    
    private int numInputs;
    private int numHiddenUnits;
    private int numOutputs;
    
    private Function function;

    public NeuralNetwork(int numInputs, int numHiddenUnits, int numOutputs, Function activationFunction)
    {
        // +1 for bias unit.
        this.numInputs = numInputs+1;
        this.numHiddenUnits = numHiddenUnits+1;
        
        this.numOutputs = numOutputs;
        
        inputWeights = new double[numInputs+1][numHiddenUnits];
        hiddenWeights = new double[numHiddenUnits+1][numOutputs];
        
        this.function = activationFunction;
    }
    
    public static boolean closeEnough(double[] a, double[] b, double delta)
    {
        if (a.length != b.length) return false; // can't compare vectors of unequal length
        double temp;
        delta *= delta;
        for (int i = 0 ; i < a.length; i++)
        {
            temp = a[i] - b[i];
            if (temp > delta) return false;
        }
        return true;
    }
    
    /**
     * Run the neural network on a single pattern.
     * @param input
     * @return The output values.
     */
    public double[] run(double[] input)
    {
        double[] hiddenValues = new double[numHiddenUnits];
        hiddenValues[numHiddenUnits-1] = -1; // last one is bias unit

        // Do input layer -> hidden layer
        for (int j = 0; j < numHiddenUnits-1; j++) // -1 for hidden bias unit
        {
            hiddenValues[j] = 0;
            for (int i = 0; i < numInputs; i++)
            {
                // if it's the last one, it's the bias unit.
                double x = i == numInputs-1 ? -1 : input[i];

                hiddenValues[j] += x * inputWeights[i][j];
            }
            hiddenValues[j] = f(hiddenValues[j]);
        }

        double[] outputValues = new double[numOutputs];

        // Do hidden layer -> output layer
        for (int k = 0; k < numOutputs; k++)
        {
            outputValues[k] = 0;
            for (int j = 0; j < numHiddenUnits; j++)
            {
                outputValues[k] += hiddenValues[j] * hiddenWeights[j][k];
            }
            outputValues[k] = f(outputValues[k]);
        }
        
        return outputValues;
    }
    
    /**
     * Set the weights from a particle's vector.
     * @param weights 
     */
    public void setWeights(double[] weights)
    {
        if (weights.length != getNumWeights())
            throw new IllegalArgumentException("Incorrect number of weights: " + String.format("expect: %d, got: %d", getNumWeights(), weights.length));
        
        // set the weights between input and hidden (inputs)*(hidden-1)
        for (int i = 0; i < numInputs; i++)
            System.arraycopy(weights, i*(numHiddenUnits-1),
                    inputWeights[i], 0, numHiddenUnits-1);
            
        // set the wights between hidden and output (hidden)*(output)
        for (int j = 0; j < numHiddenUnits; j++)
            System.arraycopy(weights, numInputs*(numHiddenUnits-1)+j*numOutputs,
                    hiddenWeights[j], 0, numOutputs);
    }
    
    public int getNumWeights()
    {
        return numInputs*(numHiddenUnits-1) + numHiddenUnits*numOutputs;
    }
    
    public static int getNumWeights(int numInputs, int numHiddenUnits, int numOutputs)
    {
        return
                // Dimensions := (I+1)J + (J+1)K
                (numInputs+1)*numHiddenUnits
                + (numHiddenUnits+1)*numOutputs;
    }
    
    // makes activation function calls look nicer in the code
    // f(x) instead of function.f(x)
    private double f(double x)
    {
        return function.f(x);
    }
}
