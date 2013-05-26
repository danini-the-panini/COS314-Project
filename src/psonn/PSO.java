package psonn;


import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Generic Particle Swarm Optimisation Algorithm.
 * To utilise, create a subclass of this class, and override the getFitness(double[]) function.
 * 
 * @author daniel
 */
public abstract class PSO
{
    private int maxIterations;
    protected Particle[] particles;
    private double vmax;
    
    private double w, c1, c2; // weight, cognitive and social coefficients.
    
    private Topology topology;
    
    protected PrintWriter writer = null;
    
    /**
     * Gets the fitness of a particular vector.
     * @param pid The particle ID this is getting the fitness for.
     * @param v The vector to calculate the fitness of.
     * @return The fitness of the vector.
     */
    protected abstract double getFitness(int pid, double[] values);
    
    // gets called for each particle after particle update
    protected abstract void particleUpdate();
    
    // gets called after getting fitness. Can prematurely stop PSO.
    protected abstract boolean intermediate(int iteration);
    
    // called after PSO is complete. values is the global best particle ever.
    protected abstract void finalise(double[] values, double fitness);
    
    // update each particle's fitness using the overridden getFitness function.
    private void updateFitness()
    {
        for (int j = 0; j < particles.length; j++)
        {
            double fitness = getFitness(j, particles[j].getValues());
            particles[j].updateFitness(fitness);
        }
        topology.update();
    }

    /**
     * Creates a Particle Swarm Optimisation
     * @param dimensions Number of dimensions for each particle.
     * @param maxIterations Total number of iterations to go through (stopping condition)
     * @param topology The topology (e.g. Ring or Star) to use for grouping particles.
     * @param w weight/momentum factor
     * @param c1 cognitive coefficient
     * @param c2 social coefficient
     * @param vmax maximum velocity
     * @param numParticles The number of particles to use.
     * @param lowerBound Lower bound for sampling particle positions.
     * @param upperBound Upper bound for sampling particle positions.
     */
    public PSO(int dimensions, int maxIterations, Topology topology, double w,
            double c1, double c2, double vmax, int numParticles,
            double lowerBound, double upperBound)
    {
        this.topology = topology;
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.vmax = vmax;
        this.maxIterations = maxIterations;
        
        particles = new Particle[numParticles];
        
        for (int i = 0; i < particles.length; i++)
        {
            particles[i] = new Particle(dimensions, lowerBound, upperBound);
        }
        
        topology.setPopulation(particles);
    }
    
    public PSO(int maxIterations, Topology topology, double w,
            double c1, double c2, double vmax, Particle[] population)
    {
        this.topology = topology;
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.vmax = vmax;
        this.maxIterations = maxIterations;
        
        this.particles = population;
        
        topology.setPopulation(particles);
    }
 
    /**
     * Runs the Particle Swarm Optimisation.
     */
    public void optimise()
    {
        
        // first run to get the initial fitness
        updateFitness();
        
        // commence particle swarm optimisation!
        for (int i = 1; i <= maxIterations; i++)
        {
            // update each particle's position
            for (int j = 0; j < particles.length; j++)
            {
                particles[j].update(w, c1, c2, vmax, topology.getBest(j));
            }
            
            particleUpdate();
            
            updateFitness();
            
            serializePopulation("./pop/"+System.currentTimeMillis()+".pop");
            
            if (!intermediate(i))
                break;
        }
        
        Particle best = getBestParticle();
        finalise(best.getBestValues(), best.getBestFitness());
        
        if (writer != null)
            writer.flush();
    }
    
    public Particle getCurrentBestParticle()
    {
        int best = 0;
        for (int i = 0; i < particles.length; i++)
        {
            if (particles[i].getFitness() > particles[best].getFitness())
                best = i;
        }
        return particles[best];
    }
    
    public Particle getBestParticle()
    {
        int best = 0;
        for (int i = 0; i < particles.length; i++)
        {
            if (particles[i].getBestFitness() > particles[best].getBestFitness())
                best = i;
        }
        return particles[best];
    }

    /**
     * Sets a destination to output the PSO's training statistics.
     * @param writer 
     */
    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }
    
    public void serializePopulation(String filename)
    {
        try
        {
            
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            
            try
            {
            if (!file.exists())
            {
                file.createNewFile();
            }
            
            for (Particle p :particles)
                oos.writeObject(p);
            }
            finally
            {
                oos.close();
            }
        }
        catch (IOException ex)
        {
            System.err.println("Error writing population: " + ex.getMessage());
        }
        
    }
    
    public static Particle[] deserializePopulation(File file)
            throws IOException
    {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<Particle> list = new ArrayList<Particle>();
        try
        {
            try {
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
        
        return list.toArray(new Particle[0]);
        
    }
}
