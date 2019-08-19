/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import java.util.prefs.BackingStoreException;
import javafx.scene.web.WebEngine;

import org.w3c.dom.Document;

/**
 *
 * @author hp
 */
public class AppBasic {

    static Document document;
    static WebEngine engine;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // TODO code application logic here
        // /* 
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Install inst = new Install();

//                    inst.writeHash();
                    if (inst.test()) {
                        Aplication app = new Aplication();
                        app.init();
                    } else {
                        GUIManager.showErrorMessage(null, Aplication.TITLE + " " + Aplication.VERSION
                                + "\nHa ocurrido un error. ",
                                "Error en la aplicacion");
                    }

                } catch (BackingStoreException ex) {
                    GUIManager.showErrorMessage(null, ex, "Error al iniciar la aplicacion");
                }

            }
        });//*/

    }

}
