/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 *
 * @author ballestax
 */
public class ProgAction extends AbstractAction {

    private Icon icon;

    public ProgAction(String text, Icon icon, String description, char accelerator, String actionCommand) {
        super(text, icon);
//        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
//                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        putValue(SHORT_DESCRIPTION, description);
        putValue(ACTION_COMMAND_KEY, actionCommand);
    }

    public ProgAction(String text, Icon icon, String description, char accelerator) {
        this(text, icon, description, accelerator, null);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

    }

    public void setSmallIcon(Icon icon) {
        putValue(SMALL_ICON, icon);
        this.icon = icon;

    }

    public void setLargeIcon(Icon icon) {
        putValue(LARGE_ICON_KEY, icon);
        this.icon = icon;
    }

}
