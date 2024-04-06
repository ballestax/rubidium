package com.rb.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lrod
 */
public class PresentationTest {
    
    public PresentationTest() {
    }
    
    @Before
    public void setUp() {
    }

  

    /**
     * Test of equals method, of class Presentation.
     */
    @Test
    public void testEquals() {
        Presentation pres1 = new Presentation();
        pres1.setIDProd(1);
        pres1.setName("160 gr");
        pres1.setSerie(1);
        pres1.setPrice(12000);
        
        Presentation pres2 = new Presentation();
        pres2.setIDProd(1);
        pres2.setName("200 gr");
        pres2.setSerie(1);
        pres2.setPrice(12000);
        
        Presentation pres3 = new Presentation();
        pres3.setIDProd(1);
        pres3.setName("160 gr");
        pres3.setSerie(1);
        pres3.setPrice(14000);
        
        Presentation pres4 = pres1;
        
        assertEquals(pres4, pres1);
        assertFalse(pres1.equals(pres2));
        assertFalse(pres1.equals(pres3));
        assertFalse(pres2.equals(pres3));
        assertTrue(pres4.equals(pres1));
        pres2.setName("160 gr");
        assertTrue(pres1.equals(pres2));
        
        
    }

    /**
     * Test of hashCode method, of class Presentation.
     */
    @Test
    public void testHashCode() {
    }
    
}
