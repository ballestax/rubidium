/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.domain;

import java.io.Serializable;

/**
 *
 * @author ballestax
 */
public class User {

    public static enum AccessLevel implements Serializable {

        ADMIN, USER;
    }
    public static final AccessLevel DEFAULT_ACCESS_LEVEL = AccessLevel.USER;
    private int id;
    private String username;
    private String password;
    private AccessLevel accessLevel;
    
    public User(String username) {
        this(username, null);
    }

    public User(String username, String password) {
        this(-1, username, password, DEFAULT_ACCESS_LEVEL);
    }

    public User(String username, String password, AccessLevel accessLevel) {
        this(-1, username, password, accessLevel);
    }

    public User(int id, String username, String password, AccessLevel accessLevel) {
        this.id = id;
        this.username = username.trim().toLowerCase();
        this.password = password;
        this.accessLevel = accessLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof User)) {
            return false;
        }
        return id == ((User) obj).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "User (" + username + ")";
    }
}
