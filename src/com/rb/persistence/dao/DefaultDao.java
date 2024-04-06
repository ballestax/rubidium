/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

/**
 *
 * @author hp
 */
public interface DefaultDao<T> {

    public T get(Long id);

    public void save(T t);

    public void delete(Long id);

    public void update(T t);

}
