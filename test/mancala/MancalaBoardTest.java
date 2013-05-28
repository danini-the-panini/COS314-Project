/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mancala;

import gamestuff.Board;
import java.io.PrintStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Daniel
 */
public class MancalaBoardTest
{

    /**
     * Test of yours method, of class MancalaBoard.
     */
    @Test
    public void testYours()
    {
        System.out.println("yours");
        
        int pit = 4;
        MancalaBoard instance = new MancalaBoard();
        
        boolean expResult = true;
        
        boolean result = instance.yours(pit);
        assertEquals(expResult, result);
        
        pit = 11;
        expResult = false;
        
        result = instance.yours(pit);
        assertEquals(expResult, result);
        
        instance.nextPlayer();
        
        pit = 8;
        expResult = true;
        
        result = instance.yours(pit);
        assertEquals(expResult, result);
        
        pit = 5;
        expResult = false;
        
        result = instance.yours(pit);
        assertEquals(expResult, result);
        
    }

}