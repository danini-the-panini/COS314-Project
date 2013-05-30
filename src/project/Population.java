/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import psonn.Particle;

/**
 *
 * @author Daniel
 */
public class Population
{
    public Particle[] particles;
    public Parameters params;
    
    public static Population deserializePopulation(File file)
            throws IOException
    {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<Particle> list = new ArrayList<Particle>();
        
        Population pop = new Population();
        try
        {
            try {
                pop.params = (Parameters)ois.readObject();
                while (true)
                {
                    try
                    {
                        list.add((Particle)ois.readObject());
                    }
                    catch (EOFException eof)
                    {
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        finally
        {
            ois.close();
        }
        
        if (list.isEmpty())
            return null;
        
        pop.particles = list.toArray(new Particle[0]);
        
        return pop;
    }
}
