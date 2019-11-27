/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import java.util.Calendar;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author CHECHE
 */
public final class Install {

    private Preferences registro;
    private final transient String REG = "/BaconApp/soft";
    private static final String CLAVE = "ahbabcbgcd";
    private final transient String REG1 = "/baconapp/inst";
    private static final String CLAVE1 = "vinst";
    private Date TWST;

    public Install() {
        registro = Preferences.userRoot();
        registro.node(REG);
        Calendar cld = Calendar.getInstance();
//        cld.setTimeInMillis(1451581199081L);
//        cld.setTimeInMillis(1452920399120L);
        cld.set(2019, 12, 31, 23, 59, 59);

        TWST = cld.getTime();
        
        writeHash();
    }

    public Date getTWST() {
        return TWST;
    }

    protected final boolean test() throws BackingStoreException {
        if (registro.nodeExists(REG)) {
            //comprobacion hecha
            String get = registro.get(REG + "/" + CLAVE, "null");
            if (!get.equals("null")) {
                try {
                    
                    long parse = Long.parseLong(get);
                    if (parse > 0) {
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    protected final boolean testCdInst() throws BackingStoreException {
        if (registro.nodeExists(REG1)) {
            //comprobacion hecha
            String get = registro.get(REG1 + "/" + CLAVE1, "null");
            if (!get.equals("null")) {
                try {
                    long parse = Long.parseLong(get);
                    if (parse > 0) {
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    protected final void wCdInst() {
        registro.put(REG1 + "/" + CLAVE1, "");
        try {
            registro.flush();
        } catch (Exception e) {
        }
    }

    protected final void writeHash() {
        registro.put(REG + "/" + CLAVE, "1821232734");
        try {
            registro.flush();
        } catch (Exception e) {
            System.err.println("(Exc)guardarPreferencias(): " + e);
        }
    }
}
