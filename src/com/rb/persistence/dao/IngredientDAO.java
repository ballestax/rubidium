/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;


import com.rb.domain.Ingredient;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface IngredientDAO {

    public Ingredient getIngredient(int id) throws com.rb.persistence.dao.DAOException;

    public ArrayList<Ingredient> getIngredientList() throws com.rb.persistence.dao.DAOException;

    public void addIngredient(Ingredient ingredient) throws com.rb.persistence.dao.DAOException;

    public void deleteIngredient(int id) throws com.rb.persistence.dao.DAOException;

    public void updateIngredient(Ingredient ingredient) throws com.rb.persistence.dao.DAOException;

}
