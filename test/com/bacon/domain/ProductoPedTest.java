package com.rb.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lrod
 */
public class ProductoPedTest {
    
    public ProductoPedTest() {
    }
    
    @Before
    public void setUp() {
    }

    /**
     * Test of getProduct method, of class ProductoPed.
     */
    @Test
    public void testGetProduct() {
    }

    /**
     * Test of setProduct method, of class ProductoPed.
     */
    @Test
    public void testSetProduct() {
    }

    /**
     * Test of getAdicionales method, of class ProductoPed.
     */
    @Test
    public void testGetAdicionales() {
    }

    /**
     * Test of getStAdicionales method, of class ProductoPed.
     */
    @Test
    public void testGetStAdicionales() {
    }

    /**
     * Test of getValueAdicionales method, of class ProductoPed.
     */
    @Test
    public void testGetValueAdicionales() {
    }

    /**
     * Test of setAdicionales method, of class ProductoPed.
     */
    @Test
    public void testSetAdicionales() {
    }

    /**
     * Test of addAdicional method, of class ProductoPed.
     */
    @Test
    public void testAddAdicional() {
    }

    /**
     * Test of getEspecificaciones method, of class ProductoPed.
     */
    @Test
    public void testGetEspecificaciones() {
    }

    /**
     * Test of setEspecificaciones method, of class ProductoPed.
     */
    @Test
    public void testSetEspecificaciones() {
    }

    /**
     * Test of getExclusiones method, of class ProductoPed.
     */
    @Test
    public void testGetExclusiones() {
    }

    /**
     * Test of getStExclusiones method, of class ProductoPed.
     */
    @Test
    public void testGetStExclusiones() {
    }

    /**
     * Test of setExclusiones method, of class ProductoPed.
     */
    @Test
    public void testSetExclusiones() {
    }

    /**
     * Test of addExclusion method, of class ProductoPed.
     */
    @Test
    public void testAddExclusion() {
    }

    /**
     * Test of equals method, of class ProductoPed.
     */
    @Test
    public void testEquals() {
        Product prod = new Product(1, "TRADICIONAL", 100, "image");
        
        ProductoPed pp1 = new ProductoPed(prod);
        
        Additional ad1 = new Additional("a1", "ADITIONAL1", 10);
        Additional ad2 = new Additional("a2", "ADITIONAL2", 10);
        Additional ad3 = new Additional("a3", "ADITIONAL3", 10);
        
        pp1.addAdicional(ad1, 1);
        
        
        ProductoPed pp2 = new ProductoPed(prod);
        
        pp2.addAdicional(ad1, 1);
        
        //misma tamaño mismos objetos
        assertTrue(pp1.equals(pp2));
        
        ProductoPed pp3 = new ProductoPed(prod);
        pp3.addAdicional(ad1, 1);
        pp3.addAdicional(ad2, 2);
        
        //diferente tamaño de objetos
        assertFalse(pp1.equals(pp3));
        
        pp1.addAdicional(ad2, 4);
        
        //mismo tamaño mismos objetos varia cantidad 
        assertFalse(pp1.equals(pp3));
        
        pp1.addAdicional(ad3, 2);
        pp2.addAdicional(ad3, 2);
        pp2.addAdicional(ad2, 4);
        
        //misma tamaño, mismo objetos diferente orden
        assertTrue(pp1.equals(pp2));
        
        
    }

    /**
     * Test of hashCode method, of class ProductoPed.
     */
    @Test
    public void testHashCode() {
    }
    
}
